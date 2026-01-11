import 'dart:typed_data';

import 'package:test/test.dart';
import 'package:actual_sync/src/proto/protobuf.dart';
import 'package:actual_sync/src/proto/sync_messages.dart';

void main() {
  group('Protobuf', () {
    test('varint encoding', () {
      // Test small numbers
      expect(Protobuf.encodeVarint(0), equals(Uint8List.fromList([0])));
      expect(Protobuf.encodeVarint(1), equals(Uint8List.fromList([1])));
      expect(Protobuf.encodeVarint(127), equals(Uint8List.fromList([127])));

      // Test numbers requiring 2 bytes
      expect(Protobuf.encodeVarint(128), equals(Uint8List.fromList([0x80, 0x01])));
      expect(Protobuf.encodeVarint(255), equals(Uint8List.fromList([0xFF, 0x01])));

      // Test larger numbers
      final encoded300 = Protobuf.encodeVarint(300);
      expect(encoded300, equals(Uint8List.fromList([0xAC, 0x02])));
    });

    test('varint decoding', () {
      final reader1 = ProtobufReader(Uint8List.fromList([0]));
      expect(reader1.readVarint(), equals(0));

      final reader2 = ProtobufReader(Uint8List.fromList([1]));
      expect(reader2.readVarint(), equals(1));

      final reader3 = ProtobufReader(Uint8List.fromList([0xAC, 0x02]));
      expect(reader3.readVarint(), equals(300));
    });
  });

  group('Message', () {
    test('encode decode', () {
      final original = Message(
        dataset: 'transactions',
        row: 'tx-123',
        column: 'amount',
        value: '1000',
      );

      final encoded = original.encode();
      final decoded = Message.decode(encoded);

      expect(decoded, equals(original));
    });
  });

  group('MessageEnvelope', () {
    test('encode decode', () {
      final message = Message(
        dataset: 'accounts',
        row: 'acc-456',
        column: 'name',
        value: 'Checking',
      );

      final original = MessageEnvelope.create(
        '2024-01-15T10:30:00.000Z-0001-ABCD1234EFGH5678',
        message,
      );

      final encoded = original.encode();
      final decoded = MessageEnvelope.decode(encoded);

      expect(decoded.timestamp, equals(original.timestamp));
      expect(decoded.isEncrypted, equals(original.isEncrypted));
      expect(decoded.content, equals(original.content));

      // Verify we can decode the inner message
      final decodedMessage = decoded.decodeMessage();
      expect(decodedMessage, equals(message));
    });

    test('encrypted envelope', () {
      final envelope = MessageEnvelope(
        timestamp: '2024-01-15T10:30:00.000Z-0000-ABCD1234EFGH5678',
        isEncrypted: true,
        content: Uint8List.fromList([1, 2, 3, 4, 5]), // encrypted bytes
      );

      final encoded = envelope.encode();
      final decoded = MessageEnvelope.decode(encoded);

      expect(decoded.timestamp, equals(envelope.timestamp));
      expect(decoded.isEncrypted, isTrue);
      expect(decoded.content, equals(envelope.content));
    });
  });

  group('SyncRequest', () {
    test('encode decode', () {
      final messages = [
        MessageEnvelope.create(
          '2024-01-15T10:30:00.000Z-0000-ABCD1234EFGH5678',
          Message(
            dataset: 'transactions',
            row: 'tx-1',
            column: 'amount',
            value: '500',
          ),
        ),
        MessageEnvelope.create(
          '2024-01-15T10:30:00.000Z-0001-ABCD1234EFGH5678',
          Message(
            dataset: 'transactions',
            row: 'tx-1',
            column: 'category',
            value: 'food',
          ),
        ),
      ];

      final original = SyncRequest(
        messages: messages,
        fileId: 'file-123',
        groupId: 'group-456',
        keyId: '',
        since: '2024-01-15T10:00:00.000Z-0000-0000000000000000',
      );

      final encoded = original.encode();
      final decoded = SyncRequest.decode(encoded);

      expect(decoded.fileId, equals(original.fileId));
      expect(decoded.groupId, equals(original.groupId));
      expect(decoded.since, equals(original.since));
      expect(decoded.messages.length, equals(original.messages.length));

      // Verify each message
      for (var i = 0; i < original.messages.length; i++) {
        expect(decoded.messages[i].timestamp, equals(original.messages[i].timestamp));
      }
    });

    test('empty request', () {
      final original = SyncRequest(
        messages: [],
        fileId: 'file-123',
        groupId: 'group-456',
      );

      final encoded = original.encode();
      final decoded = SyncRequest.decode(encoded);

      expect(decoded.fileId, equals(original.fileId));
      expect(decoded.groupId, equals(original.groupId));
      expect(decoded.messages, isEmpty);
    });
  });

  group('SyncResponse', () {
    test('encode decode', () {
      final messages = [
        MessageEnvelope.create(
          '2024-01-15T10:30:00.000Z-0002-REMOTE12345678',
          Message(
            dataset: 'categories',
            row: 'cat-1',
            column: 'name',
            value: 'Groceries',
          ),
        ),
      ];

      final original = SyncResponse(
        messages: messages,
        merkle: '{"hash":123456}',
      );

      final encoded = original.encode();
      final decoded = SyncResponse.decode(encoded);

      expect(decoded.merkle, equals(original.merkle));
      expect(decoded.messages.length, equals(original.messages.length));
      expect(decoded.messages[0].timestamp, equals(original.messages[0].timestamp));
    });
  });
}
