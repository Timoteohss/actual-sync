package com.actualbudget.sync.crdt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * A node in the Merkle trie.
 * Keys are base-3 digits (0, 1, 2) representing time buckets.
 */
@Serializable
data class TrieNode(
    val hash: Int = 0,
    val children: MutableMap<Char, TrieNode> = mutableMapOf()
) {
    operator fun get(key: Char): TrieNode? = children[key]
    operator fun set(key: Char, node: TrieNode) { children[key] = node }

    fun keys(): List<Char> = children.keys.filter { it in listOf('0', '1', '2') }.sorted()
}

/**
 * Merkle trie operations for sync state comparison.
 */
object Merkle {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Create an empty trie.
     */
    fun emptyTrie(): TrieNode = TrieNode(hash = 0)

    /**
     * Convert a base-3 key back to a timestamp in milliseconds.
     */
    fun keyToTimestamp(key: String): Long {
        // Pad to 16 characters (full base-3 representation of minutes)
        val fullKey = key.padEnd(16, '0')
        // Parse as base-3 and convert to milliseconds
        return fullKey.toLong(3) * 1000 * 60
    }

    /**
     * Insert a timestamp into the trie.
     * Returns a new trie with the timestamp added.
     */
    fun insert(trie: TrieNode, timestamp: Timestamp): TrieNode {
        val hash = timestamp.hash()
        // Convert millis to minutes, then to base-3 string
        val minutes = timestamp.millis / 1000 / 60
        val key = minutes.toString(3)

        val newTrie = trie.copy(hash = trie.hash xor hash)
        return insertKey(newTrie, key, hash)
    }

    private fun insertKey(trie: TrieNode, key: String, hash: Int): TrieNode {
        if (key.isEmpty()) {
            return trie
        }

        val c = key[0]
        val existing = trie[c] ?: TrieNode()
        val updated = insertKey(existing, key.substring(1), hash)
        val newNode = updated.copy(hash = (existing.hash) xor hash)

        val result = trie.copy()
        result[c] = newNode
        return result
    }

    /**
     * Build a trie from a list of timestamps.
     */
    fun build(timestamps: List<Timestamp>): TrieNode {
        var trie = emptyTrie()
        for (ts in timestamps) {
            trie = insert(trie, ts)
        }
        return trie
    }

    /**
     * Find the earliest point of divergence between two tries.
     * Returns the timestamp (in milliseconds) where they diverge, or null if in sync.
     */
    fun diff(trie1: TrieNode, trie2: TrieNode): Long? {
        if (trie1.hash == trie2.hash) {
            return null // In sync
        }

        var node1 = trie1
        var node2 = trie2
        var k = ""

        while (true) {
            val keys = (node1.keys() + node2.keys()).distinct().sorted()

            var diffKey: Char? = null

            for (key in keys) {
                val next1 = node1[key]
                val next2 = node2[key]

                if (next1 == null || next2 == null) {
                    break
                }

                if (next1.hash != next2.hash) {
                    diffKey = key
                    break
                }
            }

            if (diffKey == null) {
                return keyToTimestamp(k)
            }

            k += diffKey
            node1 = node1[diffKey] ?: emptyTrie()
            node2 = node2[diffKey] ?: emptyTrie()
        }
    }

    /**
     * Prune the trie to keep only the n most recent branches at each level.
     */
    fun prune(trie: TrieNode, n: Int = 2): TrieNode {
        if (trie.hash == 0) {
            return trie
        }

        val keys = trie.keys()
        val keysToKeep = keys.takeLast(n)

        val result = TrieNode(hash = trie.hash)
        for (k in keysToKeep) {
            val child = trie[k] ?: continue
            result[k] = prune(child, n)
        }

        return result
    }

    /**
     * Serialize trie to JSON string.
     */
    fun serialize(trie: TrieNode): String {
        return json.encodeToString(TrieNode.serializer(), trie)
    }

    /**
     * Deserialize trie from JSON string.
     */
    fun deserialize(data: String): TrieNode {
        return try {
            json.decodeFromString(TrieNode.serializer(), data)
        } catch (e: Exception) {
            emptyTrie()
        }
    }

    /**
     * Debug print the trie structure.
     */
    fun debug(trie: TrieNode, key: String = "", indent: Int = 0): String {
        val prefix = " ".repeat(indent)
        val keyStr = if (key.isNotEmpty()) "k: $key " else ""
        val hashStr = if (trie.hash != 0) "hash: ${trie.hash}" else "hash: (empty)"

        val sb = StringBuilder()
        sb.appendLine("$prefix$keyStr$hashStr")

        for (k in trie.keys()) {
            val child = trie[k] ?: continue
            sb.append(debug(child, k.toString(), indent + 2))
        }

        return sb.toString()
    }
}
