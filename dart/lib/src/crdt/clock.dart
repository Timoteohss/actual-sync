import 'dart:convert';
import 'timestamp.dart';
import 'merkle.dart';

/// Global clock management for CRDT operations.
class ClockManager {
  static SyncClock? _clock;

  /// Get the current clock instance.
  static SyncClock? get clock => _clock;

  /// Set the global clock instance.
  static void setClock(SyncClock newClock) {
    _clock = newClock;
  }

  /// Create a new clock with the given timestamp and merkle trie.
  static SyncClock makeClock(Timestamp timestamp, [TrieNode? merkle]) {
    return SyncClock(
      timestamp: MutableClock.from(timestamp),
      merkle: merkle ?? Merkle.emptyTrie(),
    );
  }

  /// Serialize the clock state to a JSON string.
  static String serialize(SyncClock clock) {
    final state = {
      'timestamp': clock.timestamp.toTimestamp().toString(),
      'merkle': clock.merkle.toJson(),
    };
    return jsonEncode(state);
  }

  /// Deserialize clock state from a JSON string.
  static SyncClock deserialize(String data) {
    try {
      final state = jsonDecode(data) as Map<String, dynamic>;
      final tsString = state['timestamp'] as String;
      final ts = Timestamp.parse(tsString);
      if (ts == null) {
        throw InvalidTimestampError(tsString);
      }

      final merkleJson = state['merkle'] as Map<String, dynamic>?;
      final merkle = merkleJson != null
          ? TrieNode.fromJson(merkleJson)
          : Merkle.emptyTrie();

      return SyncClock(
        timestamp: MutableClock.from(ts),
        merkle: merkle,
      );
    } catch (_) {
      // Return a fresh clock with new client ID
      final nodeId = Timestamp.makeClientId();
      return SyncClock(
        timestamp: MutableClock(node: nodeId),
        merkle: Merkle.emptyTrie(),
      );
    }
  }
}

/// Sync clock containing mutable timestamp and merkle trie state.
class SyncClock {
  final MutableClock timestamp;
  TrieNode merkle;

  SyncClock({
    required this.timestamp,
    required this.merkle,
  });

  /// Generate a new timestamp for a local change.
  Timestamp send() {
    final ts = timestamp.send();
    merkle = Merkle.insert(merkle, ts);
    merkle = Merkle.prune(merkle);
    return ts;
  }

  /// Receive and apply a timestamp from a remote client.
  Timestamp recv(Timestamp msg) {
    timestamp.recv(msg);
    merkle = Merkle.insert(merkle, msg);
    merkle = Merkle.prune(merkle);
    return msg;
  }

  /// Get the current timestamp without advancing the clock.
  Timestamp current() => timestamp.toTimestamp();
}
