import 'dart:typed_data';

import 'protobuf.dart';

/// Encrypted data envelope.
///
/// Proto definition:
/// message EncryptedData {
///     bytes iv = 1;
///     bytes authTag = 2;
///     bytes data = 3;
/// }
class EncryptedData {
  final Uint8List iv;
  final Uint8List authTag;
  final Uint8List data;

  EncryptedData({
    required this.iv,
    required this.authTag,
    required this.data,
  });

  Uint8List encode() {
    final writer = ProtobufWriter();
    writer.writeBytes(1, iv);
    writer.writeBytes(2, authTag);
    writer.writeBytes(3, data);
    return writer.toBytes();
  }

  static EncryptedData decode(Uint8List data) {
    final reader = ProtobufReader(data);
    Uint8List iv = Uint8List(0);
    Uint8List authTag = Uint8List(0);
    Uint8List content = Uint8List(0);

    while (!reader.isAtEnd) {
      final (fieldNumber, wireType) = reader.readTag();
      switch (fieldNumber) {
        case 1:
          iv = reader.readBytes();
          break;
        case 2:
          authTag = reader.readBytes();
          break;
        case 3:
          content = reader.readBytes();
          break;
        default:
          reader.skipField(wireType);
      }
    }

    return EncryptedData(iv: iv, authTag: authTag, data: content);
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is EncryptedData &&
          _listEquals(iv, other.iv) &&
          _listEquals(authTag, other.authTag) &&
          _listEquals(data, other.data);

  @override
  int get hashCode => Object.hash(
        Object.hashAll(iv),
        Object.hashAll(authTag),
        Object.hashAll(data),
      );
}

/// CRDT message content.
///
/// Proto definition:
/// message Message {
///     string dataset = 1;
///     string row = 2;
///     string column = 3;
///     string value = 4;
/// }
class Message {
  final String dataset;
  final String row;
  final String column;
  final String value;

  Message({
    required this.dataset,
    required this.row,
    required this.column,
    required this.value,
  });

  Uint8List encode() {
    final writer = ProtobufWriter();
    writer.writeString(1, dataset);
    writer.writeString(2, row);
    writer.writeString(3, column);
    writer.writeString(4, value);
    return writer.toBytes();
  }

  static Message decode(Uint8List data) {
    final reader = ProtobufReader(data);
    var dataset = '';
    var row = '';
    var column = '';
    var value = '';

    while (!reader.isAtEnd) {
      final (fieldNumber, wireType) = reader.readTag();
      switch (fieldNumber) {
        case 1:
          dataset = reader.readString();
          break;
        case 2:
          row = reader.readString();
          break;
        case 3:
          column = reader.readString();
          break;
        case 4:
          value = reader.readString();
          break;
        default:
          reader.skipField(wireType);
      }
    }

    return Message(dataset: dataset, row: row, column: column, value: value);
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Message &&
          dataset == other.dataset &&
          row == other.row &&
          column == other.column &&
          value == other.value;

  @override
  int get hashCode => Object.hash(dataset, row, column, value);

  @override
  String toString() =>
      'Message(dataset: $dataset, row: $row, column: $column, value: $value)';
}

/// Message envelope containing timestamp and content.
///
/// Proto definition:
/// message MessageEnvelope {
///     string timestamp = 1;
///     bool isEncrypted = 2;
///     bytes content = 3;
/// }
class MessageEnvelope {
  final String timestamp;
  final bool isEncrypted;
  final Uint8List content;

  MessageEnvelope({
    required this.timestamp,
    this.isEncrypted = false,
    required this.content,
  });

  /// Create an envelope with an unencrypted message.
  factory MessageEnvelope.create(String timestamp, Message message) {
    return MessageEnvelope(
      timestamp: timestamp,
      isEncrypted: false,
      content: message.encode(),
    );
  }

  Uint8List encode() {
    final writer = ProtobufWriter();
    writer.writeString(1, timestamp);
    writer.writeBool(2, isEncrypted);
    writer.writeBytes(3, content);
    return writer.toBytes();
  }

  /// Decode the content as a Message (if not encrypted).
  Message decodeMessage() {
    if (isEncrypted) {
      throw StateError('Cannot decode encrypted message without key');
    }
    return Message.decode(content);
  }

  static MessageEnvelope decode(Uint8List data) {
    final reader = ProtobufReader(data);
    var timestamp = '';
    var isEncrypted = false;
    Uint8List content = Uint8List(0);

    while (!reader.isAtEnd) {
      final (fieldNumber, wireType) = reader.readTag();
      switch (fieldNumber) {
        case 1:
          timestamp = reader.readString();
          break;
        case 2:
          isEncrypted = reader.readBool();
          break;
        case 3:
          content = reader.readBytes();
          break;
        default:
          reader.skipField(wireType);
      }
    }

    return MessageEnvelope(
      timestamp: timestamp,
      isEncrypted: isEncrypted,
      content: content,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MessageEnvelope &&
          timestamp == other.timestamp &&
          isEncrypted == other.isEncrypted &&
          _listEquals(content, other.content);

  @override
  int get hashCode => Object.hash(timestamp, isEncrypted, Object.hashAll(content));

  @override
  String toString() =>
      'MessageEnvelope(timestamp: $timestamp, isEncrypted: $isEncrypted)';
}

/// Sync request sent to the server.
///
/// Proto definition:
/// message SyncRequest {
///     repeated MessageEnvelope messages = 1;
///     string fileId = 2;
///     string groupId = 3;
///     string keyId = 5;
///     string since = 6;
/// }
class SyncRequest {
  final List<MessageEnvelope> messages;
  final String fileId;
  final String groupId;
  final String keyId;
  final String since;

  SyncRequest({
    this.messages = const [],
    required this.fileId,
    required this.groupId,
    this.keyId = '',
    this.since = '',
  });

  Uint8List encode() {
    final writer = ProtobufWriter();
    for (final envelope in messages) {
      writer.writeMessage(1, envelope.encode());
    }
    writer.writeString(2, fileId);
    writer.writeString(3, groupId);
    if (keyId.isNotEmpty) writer.writeString(5, keyId);
    if (since.isNotEmpty) writer.writeString(6, since);
    return writer.toBytes();
  }

  static SyncRequest decode(Uint8List data) {
    final reader = ProtobufReader(data);
    final messages = <MessageEnvelope>[];
    var fileId = '';
    var groupId = '';
    var keyId = '';
    var since = '';

    while (!reader.isAtEnd) {
      final (fieldNumber, wireType) = reader.readTag();
      switch (fieldNumber) {
        case 1:
          messages.add(MessageEnvelope.decode(reader.readBytes()));
          break;
        case 2:
          fileId = reader.readString();
          break;
        case 3:
          groupId = reader.readString();
          break;
        case 5:
          keyId = reader.readString();
          break;
        case 6:
          since = reader.readString();
          break;
        default:
          reader.skipField(wireType);
      }
    }

    return SyncRequest(
      messages: messages,
      fileId: fileId,
      groupId: groupId,
      keyId: keyId,
      since: since,
    );
  }

  @override
  String toString() =>
      'SyncRequest(fileId: $fileId, groupId: $groupId, messages: ${messages.length}, since: $since)';
}

/// Sync response from the server.
///
/// Proto definition:
/// message SyncResponse {
///     repeated MessageEnvelope messages = 1;
///     string merkle = 2;
/// }
class SyncResponse {
  final List<MessageEnvelope> messages;
  final String merkle;

  SyncResponse({
    this.messages = const [],
    this.merkle = '',
  });

  Uint8List encode() {
    final writer = ProtobufWriter();
    for (final envelope in messages) {
      writer.writeMessage(1, envelope.encode());
    }
    writer.writeString(2, merkle);
    return writer.toBytes();
  }

  static SyncResponse decode(Uint8List data) {
    final reader = ProtobufReader(data);
    final messages = <MessageEnvelope>[];
    var merkle = '';

    while (!reader.isAtEnd) {
      final (fieldNumber, wireType) = reader.readTag();
      switch (fieldNumber) {
        case 1:
          messages.add(MessageEnvelope.decode(reader.readBytes()));
          break;
        case 2:
          merkle = reader.readString();
          break;
        default:
          reader.skipField(wireType);
      }
    }

    return SyncResponse(messages: messages, merkle: merkle);
  }

  @override
  String toString() =>
      'SyncResponse(messages: ${messages.length}, merkle: ${merkle.isNotEmpty})';
}

/// Helper to compare Uint8List equality.
bool _listEquals(Uint8List a, Uint8List b) {
  if (a.length != b.length) return false;
  for (var i = 0; i < a.length; i++) {
    if (a[i] != b[i]) return false;
  }
  return true;
}
