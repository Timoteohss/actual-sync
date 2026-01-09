import 'dart:convert';
import 'timestamp.dart';

/// A node in the Merkle trie.
/// Keys are base-3 digits (0, 1, 2) representing time buckets.
class TrieNode {
  int hash;
  final Map<String, TrieNode> children;

  TrieNode({this.hash = 0, Map<String, TrieNode>? children})
      : children = children ?? {};

  TrieNode? operator [](String key) => children[key];
  void operator []=(String key, TrieNode node) => children[key] = node;

  List<String> get keys =>
      children.keys.where((k) => ['0', '1', '2'].contains(k)).toList()..sort();

  TrieNode copy() {
    return TrieNode(
      hash: hash,
      children: Map.fromEntries(
        children.entries.map((e) => MapEntry(e.key, e.value.copy())),
      ),
    );
  }

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{'hash': hash};
    for (final key in keys) {
      map[key] = children[key]!.toJson();
    }
    return map;
  }

  factory TrieNode.fromJson(Map<String, dynamic> json) {
    final node = TrieNode(hash: json['hash'] as int? ?? 0);
    for (final key in ['0', '1', '2']) {
      if (json.containsKey(key) && json[key] is Map) {
        node[key] = TrieNode.fromJson(json[key] as Map<String, dynamic>);
      }
    }
    return node;
  }
}

/// Merkle trie operations for sync state comparison.
class Merkle {
  /// Create an empty trie.
  static TrieNode emptyTrie() => TrieNode(hash: 0);

  /// Convert a base-3 key back to a timestamp in milliseconds.
  static int keyToTimestamp(String key) {
    // Pad to 16 characters (full base-3 representation of minutes)
    final fullKey = key.padRight(16, '0');
    // Parse as base-3 and convert to milliseconds
    return int.parse(fullKey, radix: 3) * 1000 * 60;
  }

  /// Insert a timestamp into the trie.
  /// Returns a new trie with the timestamp added.
  static TrieNode insert(TrieNode trie, Timestamp timestamp) {
    final hash = timestamp.hash();
    // Convert millis to minutes, then to base-3 string
    final minutes = timestamp.millis ~/ 1000 ~/ 60;
    final key = minutes.toRadixString(3);

    final newTrie = trie.copy();
    newTrie.hash = trie.hash ^ hash;
    return _insertKey(newTrie, key, hash);
  }

  static TrieNode _insertKey(TrieNode trie, String key, int hash) {
    if (key.isEmpty) {
      return trie;
    }

    final c = key[0];
    final existing = trie[c] ?? TrieNode();
    final updated = _insertKey(existing.copy(), key.substring(1), hash);
    updated.hash = existing.hash ^ hash;

    trie[c] = updated;
    return trie;
  }

  /// Build a trie from a list of timestamps.
  static TrieNode build(List<Timestamp> timestamps) {
    var trie = emptyTrie();
    for (final ts in timestamps) {
      trie = insert(trie, ts);
    }
    return trie;
  }

  /// Find the earliest point of divergence between two tries.
  /// Returns the timestamp (in milliseconds) where they diverge, or null if in sync.
  static int? diff(TrieNode trie1, TrieNode trie2) {
    if (trie1.hash == trie2.hash) {
      return null; // In sync
    }

    var node1 = trie1;
    var node2 = trie2;
    var k = '';

    while (true) {
      final allKeys = {...node1.keys, ...node2.keys}.toList()..sort();

      String? diffKey;

      for (final key in allKeys) {
        final next1 = node1[key];
        final next2 = node2[key];

        if (next1 == null || next2 == null) {
          break;
        }

        if (next1.hash != next2.hash) {
          diffKey = key;
          break;
        }
      }

      if (diffKey == null) {
        return keyToTimestamp(k);
      }

      k += diffKey;
      node1 = node1[diffKey] ?? emptyTrie();
      node2 = node2[diffKey] ?? emptyTrie();
    }
  }

  /// Prune the trie to keep only the n most recent branches at each level.
  static TrieNode prune(TrieNode trie, [int n = 2]) {
    if (trie.hash == 0) {
      return trie;
    }

    final keys = trie.keys;
    final keysToKeep = keys.length <= n ? keys : keys.sublist(keys.length - n);

    final result = TrieNode(hash: trie.hash);
    for (final k in keysToKeep) {
      final child = trie[k];
      if (child != null) {
        result[k] = prune(child, n);
      }
    }

    return result;
  }

  /// Serialize trie to JSON string.
  static String serialize(TrieNode trie) {
    return jsonEncode(trie.toJson());
  }

  /// Deserialize trie from JSON string.
  static TrieNode deserialize(String data) {
    try {
      final json = jsonDecode(data) as Map<String, dynamic>;
      return TrieNode.fromJson(json);
    } catch (_) {
      return emptyTrie();
    }
  }

  /// Debug print the trie structure.
  static String debug(TrieNode trie, [String key = '', int indent = 0]) {
    final prefix = ' ' * indent;
    final keyStr = key.isNotEmpty ? 'k: $key ' : '';
    final hashStr = trie.hash != 0 ? 'hash: ${trie.hash}' : 'hash: (empty)';

    final sb = StringBuffer();
    sb.writeln('$prefix$keyStr$hashStr');

    for (final k in trie.keys) {
      final child = trie[k];
      if (child != null) {
        sb.write(debug(child, k, indent + 2));
      }
    }

    return sb.toString();
  }
}
