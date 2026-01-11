import 'package:test/test.dart';
import 'package:actual_sync/src/crdt/timestamp.dart';

void main() {
  group('Timestamp', () {
    test('parse valid timestamp', () {
      final ts = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912');
      expect(ts, isNotNull);
      expect(ts!.millis, equals(1745533422123));
      expect(ts.counter, equals(1));
      expect(ts.node, equals('A219E7A71CC18912'));
    });

    test('parse invalid timestamp', () {
      expect(Timestamp.parse('invalid'), isNull);
      expect(Timestamp.parse('2025-04-24T22:23:42.123Z'), isNull);
      expect(Timestamp.parse('2025-04-24T22:23:42.123Z-0001'), isNull);
    });

    test('toString', () {
      final ts = Timestamp(1745533422123, 1, 'A219E7A71CC18912');
      final str = ts.toString();
      expect(str, contains('2025-04-24'));
      expect(str, contains('-0001-'));
      expect(str, contains('A219E7A71CC18912'));
    });

    test('round trip', () {
      const original = '2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912';
      final ts = Timestamp.parse(original);
      expect(ts, isNotNull);
      final result = ts!.toString();
      // Parse and re-serialize
      final ts2 = Timestamp.parse(result);
      expect(ts2, isNotNull);
      expect(ts2!.millis, equals(ts.millis));
      expect(ts2.counter, equals(ts.counter));
      expect(ts2.node, equals(ts.node));
    });

    test('makeClientId', () {
      final id1 = Timestamp.makeClientId();
      final id2 = Timestamp.makeClientId();
      expect(id1.length, equals(16));
      expect(id2.length, equals(16));
      expect(id1, isNot(equals(id2)));
    });

    test('hash', () {
      final ts1 = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final ts2 = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final ts3 = Timestamp.parse('2025-04-24T22:23:42.123Z-0002-A219E7A71CC18912')!;

      expect(ts1.hash(), equals(ts2.hash()));
      expect(ts1.hash(), isNot(equals(ts3.hash())));
    });

    test('comparison', () {
      final ts1 = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final ts2 = Timestamp.parse('2025-04-24T22:23:42.123Z-0002-A219E7A71CC18912')!;
      final ts3 = Timestamp.parse('2025-04-25T22:23:42.123Z-0001-A219E7A71CC18912')!;

      expect(ts1.compareTo(ts2), lessThan(0));
      expect(ts2.compareTo(ts3), lessThan(0));
      expect(ts1.compareTo(ts3), lessThan(0));
    });

    test('zero timestamp', () {
      expect(Timestamp.zero.millis, equals(0));
      expect(Timestamp.zero.counter, equals(0));
      expect(Timestamp.zero.node, equals('0000000000000000'));
    });
  });

  group('MutableClock', () {
    test('send increments counter', () {
      final clock = MutableClock(millis: 1700000000000, counter: 0, node: 'TESTNODEID123456');

      // Send multiple times quickly - counter should increment
      final ts1 = clock.send();
      final ts2 = clock.send();
      final ts3 = clock.send();

      // If all in same millisecond, counters should increment
      if (ts1.millis == ts2.millis && ts2.millis == ts3.millis) {
        expect(ts1.counter, lessThan(ts2.counter));
        expect(ts2.counter, lessThan(ts3.counter));
      }
    });

    test('recv merges timestamps', () {
      final clock = MutableClock(node: 'LOCALNODEID12345');
      final remoteTs = Timestamp(
        1700000001000, // Fixed future timestamp
        5,
        'REMOTENODEID1234',
      );

      final result = clock.recv(remoteTs);
      expect(result, isNotNull);

      // Clock should have advanced
      expect(clock.millis, greaterThanOrEqualTo(remoteTs.millis));
    });
  });
}
