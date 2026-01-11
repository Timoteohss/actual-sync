import 'package:test/test.dart';
import 'package:actual_sync/src/crdt/merkle.dart';
import 'package:actual_sync/src/crdt/timestamp.dart';

void main() {
  group('Merkle', () {
    test('empty trie', () {
      final trie = Merkle.emptyTrie();
      expect(trie.hash, equals(0));
      expect(trie.keys, isEmpty);
    });

    test('insert single timestamp', () {
      final ts = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      var trie = Merkle.emptyTrie();
      trie = Merkle.insert(trie, ts);

      expect(trie.hash, isNot(equals(0)));
    });

    test('insert multiple timestamps', () {
      final ts1 = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final ts2 = Timestamp.parse('2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912')!;

      var trie = Merkle.emptyTrie();
      trie = Merkle.insert(trie, ts1);
      final hash1 = trie.hash;

      trie = Merkle.insert(trie, ts2);
      final hash2 = trie.hash;

      expect(hash1, isNot(equals(hash2)));
    });

    test('build', () {
      final timestamps = [
        Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!,
        Timestamp.parse('2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912')!,
        Timestamp.parse('2025-04-24T22:25:42.123Z-0001-A219E7A71CC18912')!,
      ];

      final trie = Merkle.build(timestamps);
      expect(trie.hash, isNot(equals(0)));
    });

    test('diff identical tries', () {
      final ts = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final trie1 = Merkle.insert(Merkle.emptyTrie(), ts);
      final trie2 = Merkle.insert(Merkle.emptyTrie(), ts);

      expect(Merkle.diff(trie1, trie2), isNull);
    });

    test('diff different tries', () {
      final ts1 = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final ts2 = Timestamp.parse('2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912')!;

      final trie1 = Merkle.insert(Merkle.emptyTrie(), ts1);
      final trie2 = Merkle.insert(Merkle.emptyTrie(), ts2);

      final diff = Merkle.diff(trie1, trie2);
      expect(diff, isNotNull);
    });

    test('prune', () {
      final timestamps = List.generate(
        100,
        (i) => Timestamp(1745533422123 + i * 60000, 0, 'A219E7A71CC18912'),
      );

      var trie = Merkle.emptyTrie();
      for (final ts in timestamps) {
        trie = Merkle.insert(trie, ts);
      }

      final pruned = Merkle.prune(trie, 2);
      // Pruned trie should have same hash
      expect(pruned.hash, equals(trie.hash));
    });

    test('serialize deserialize', () {
      final ts = Timestamp.parse('2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912')!;
      final trie = Merkle.insert(Merkle.emptyTrie(), ts);

      final json = Merkle.serialize(trie);
      final restored = Merkle.deserialize(json);

      expect(restored.hash, equals(trie.hash));
    });

    test('keyToTimestamp', () {
      // Base-3 key "0" should give timestamp 0
      expect(Merkle.keyToTimestamp('0'), equals(0));

      // Non-zero key should give non-zero timestamp
      expect(Merkle.keyToTimestamp('1'), greaterThan(0));
    });
  });
}
