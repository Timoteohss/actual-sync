import 'package:test/test.dart';
import 'package:actual_sync/src/crdt/clock.dart';
import 'package:actual_sync/src/crdt/merkle.dart';
import 'package:actual_sync/src/crdt/timestamp.dart';

void main() {
  group('MutableClock', () {
    test('send', () {
      final clock = MutableClock(node: Timestamp.makeClientId());

      final ts1 = clock.send();
      final ts2 = clock.send();

      expect(ts1.compareTo(ts2), lessThan(0));
    });

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

    test('recv', () {
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

  group('SyncClock', () {
    test('send updates merkle', () {
      final nodeId = Timestamp.makeClientId();
      final syncClock = ClockManager.makeClock(Timestamp(0, 0, nodeId));

      final initialHash = syncClock.merkle.hash;

      syncClock.send();

      expect(syncClock.merkle.hash, isNot(equals(initialHash)));
    });

    test('recv updates merkle', () {
      final nodeId = Timestamp.makeClientId();
      final syncClock = ClockManager.makeClock(Timestamp(0, 0, nodeId));

      final initialHash = syncClock.merkle.hash;

      final remoteTs = Timestamp(
        1700000000000,
        0,
        'REMOTENODEID1234',
      );
      syncClock.recv(remoteTs);

      expect(syncClock.merkle.hash, isNot(equals(initialHash)));
    });
  });

  group('ClockManager', () {
    test('serialize deserialize', () {
      final nodeId = Timestamp.makeClientId();
      final syncClock = ClockManager.makeClock(Timestamp(1000, 5, nodeId));
      syncClock.send();
      syncClock.send();

      final serialized = ClockManager.serialize(syncClock);
      final restored = ClockManager.deserialize(serialized);

      expect(restored.merkle.hash, equals(syncClock.merkle.hash));
      expect(restored.timestamp.node, equals(syncClock.timestamp.node));
    });

    test('set and get clock', () {
      final nodeId = Timestamp.makeClientId();
      final clock = ClockManager.makeClock(Timestamp(0, 0, nodeId));

      ClockManager.setClock(clock);

      expect(ClockManager.clock, equals(clock));
    });
  });
}
