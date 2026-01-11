import 'dart:convert';
import 'dart:typed_data';

import '../crdt/clock.dart';
import '../crdt/merkle.dart';
import '../crdt/timestamp.dart';
import '../db/database.dart';
import '../db/sync_repository.dart';
import '../proto/sync_messages.dart';
import 'package:drift/drift.dart';

/// Engine for bidirectional CRDT sync with the Actual Budget server.
///
/// Handles:
/// - Generating messages for local changes
/// - Tracking pending messages to send
/// - Merkle trie comparison for efficient sync
/// - Applying remote messages locally
class SyncEngine {
  final ActualDatabase db;
  final MutableClock clock;
  final SyncRepository _repository;

  TrieNode _localMerkle = Merkle.emptyTrie();
  final List<MessageEnvelope> _pendingMessages = [];

  SyncEngine({
    required this.db,
    required this.clock,
  }) : _repository = SyncRepository(db);

  /// Initialize the sync engine by loading local merkle state.
  Future<void> initialize() async {
    // Load merkle from database
    final merkleJson = await db.getSyncMetadataValue('merkle');
    if (merkleJson != null) {
      _localMerkle = Merkle.deserialize(merkleJson);
    }

    // Rebuild merkle from stored messages to ensure consistency
    await _rebuildMerkleFromMessages();
  }

  /// Rebuild the local merkle trie from stored CRDT messages.
  Future<void> _rebuildMerkleFromMessages() async {
    final messages = await db.getMessagesSince('');
    var trie = Merkle.emptyTrie();

    for (final msg in messages) {
      final ts = Timestamp.parse(msg.timestamp);
      if (ts != null) {
        trie = Merkle.insert(trie, ts);
      }
    }

    _localMerkle = trie;
  }

  /// Create a local change and generate a CRDT message.
  /// The message is added to pending queue for next sync.
  Future<MessageEnvelope> createChange(
      String dataset, String row, String column, Object? value) async {
    final ts = clock.send();
    final encodedValue = _encodeValue(value);

    final message = Message(
      dataset: dataset,
      row: row,
      column: column,
      value: encodedValue,
    );

    final envelope = MessageEnvelope.create(ts.toString(), message);

    // Store in local database (value stored as BLOB)
    await db.insertMessage(
      timestamp: ts.toString(),
      dataset: dataset,
      row: row,
      column: column,
      value: Uint8List.fromList(utf8.encode(encodedValue)),
    );

    // Apply the change to entity tables immediately
    await _repository.applyMessage(ts.toString(), message);

    // Update local merkle
    _localMerkle = Merkle.insert(_localMerkle, ts);

    // Add to pending queue
    _pendingMessages.add(envelope);

    return envelope;
  }

  /// Encode a value in Actual's typed format.
  String _encodeValue(Object? value) {
    if (value == null) {
      return '0:';
    } else if (value is String) {
      return 'S:$value';
    } else if (value is num) {
      return 'N:$value';
    } else if (value is bool) {
      return 'N:${value ? 1 : 0}';
    } else {
      return 'S:$value';
    }
  }

  /// Get pending messages that need to be sent to server.
  List<MessageEnvelope> getPendingMessages() => List.unmodifiable(_pendingMessages);

  /// Clear pending messages after successful sync.
  void clearPendingMessages() {
    _pendingMessages.clear();
  }

  /// Build a sync request for the server.
  ///
  /// [fileId] The budget file ID
  /// [groupId] The budget group ID
  /// [fullSync] If true, request all messages from beginning
  Future<SyncRequest> buildSyncRequest(
      String fileId, String groupId, bool fullSync) async {
    final String since;
    if (fullSync) {
      since = '1970-01-01T00:00:00.000Z-0000-0000000000000000';
    } else {
      // Get last sync timestamp
      since = await db.getLastTimestamp() ??
          '1970-01-01T00:00:00.000Z-0000-0000000000000000';
    }

    return SyncRequest(
      messages: _pendingMessages.toList(),
      fileId: fileId,
      groupId: groupId,
      since: since,
    );
  }

  /// Build an incremental sync request based on merkle comparison.
  ///
  /// [fileId] The budget file ID
  /// [groupId] The budget group ID
  /// [serverMerkle] The server's merkle trie (from previous sync response)
  Future<SyncRequest> buildIncrementalSyncRequest(
      String fileId, String groupId, TrieNode serverMerkle) async {
    // Find divergence point
    final diffTimestamp = Merkle.diff(_localMerkle, serverMerkle);

    final String since;
    if (diffTimestamp != null) {
      // Convert milliseconds to timestamp string
      final ts =
          Timestamp(diffTimestamp, 0, '0000000000000000');
      since = ts.toString();
    } else {
      // In sync, just send pending messages
      since = await db.getLastTimestamp() ??
          '1970-01-01T00:00:00.000Z-0000-0000000000000000';
    }

    return SyncRequest(
      messages: _pendingMessages.toList(),
      fileId: fileId,
      groupId: groupId,
      since: since,
    );
  }

  /// Process a sync response from the server.
  /// Applies remote messages and updates local merkle.
  ///
  /// Returns the number of new messages applied.
  Future<int> processSyncResponse(SyncResponse response) async {
    var applied = 0;
    var skippedEncrypted = 0;

    print('[SyncEngine] Processing sync response with ${response.messages.length} messages');

    // Apply remote messages
    for (final envelope in response.messages) {
      if (envelope.isEncrypted) {
        skippedEncrypted++;
        if (skippedEncrypted <= 5) {
          print('[SyncEngine] SKIPPING encrypted message: ${envelope.timestamp}');
        }
        continue;
      }

      try {
        final message = envelope.decodeMessage();
        final ts = Timestamp.parse(envelope.timestamp);

        // Check if we already have this message
        final exists = await db.messageExists(envelope.timestamp);

        if (!exists) {
          // Store and apply
          await _applyMessage(envelope.timestamp, message);
          applied++;

          // Update local merkle
          if (ts != null) {
            _localMerkle = Merkle.insert(_localMerkle, ts);
          }
        } else {
          // Skip - already have this message
          print('[SyncEngine] SKIP (exists): ${message.dataset}.${message.column} = \'${message.value.substring(0, message.value.length.clamp(0, 50))}\'');

          // Update local merkle
          if (ts != null) {
            _localMerkle = Merkle.insert(_localMerkle, ts);
          }
        }
      } catch (e) {
        print('Failed to apply message: $e');
      }
    }

    // Update server merkle
    if (response.merkle.isNotEmpty) {
      // Store for future incremental syncs
      await db.setSyncMetadataValue('server_merkle', response.merkle);
    }

    // Save local merkle
    await db.setSyncMetadataValue('merkle', Merkle.serialize(_localMerkle));

    // Clear pending messages that were successfully sent
    clearPendingMessages();

    print('[SyncEngine] Sync complete: applied=$applied, skippedEncrypted=$skippedEncrypted, total=${response.messages.length}');

    return applied;
  }

  /// Apply a CRDT message to the database.
  Future<void> _applyMessage(String timestamp, Message message) async {
    // Debug logging
    print('[SyncEngine] MSG: ${message.dataset}.${message.column} = \'${message.value.substring(0, message.value.length.clamp(0, 50))}\'');

    // Store in CRDT log (value stored as BLOB)
    await db.insertMessage(
      timestamp: timestamp,
      dataset: message.dataset,
      row: message.row,
      column: message.column,
      value: Uint8List.fromList(utf8.encode(message.value)),
    );

    // Parse and apply to entity tables via repository
    await _repository.applyMessage(timestamp, message);
  }

  /// Get the local merkle trie.
  TrieNode get localMerkle => _localMerkle;

  /// Get the stored server merkle trie.
  Future<TrieNode?> getServerMerkle() async {
    final json = await db.getSyncMetadataValue('server_merkle');
    return json != null ? Merkle.deserialize(json) : null;
  }

  /// Check if local and server are in sync.
  Future<bool> isInSync() async {
    final serverMerkle = await getServerMerkle();
    if (serverMerkle == null) return false;
    return Merkle.diff(_localMerkle, serverMerkle) == null;
  }
}
