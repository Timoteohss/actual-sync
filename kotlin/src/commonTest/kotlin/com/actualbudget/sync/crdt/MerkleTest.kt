package com.actualbudget.sync.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MerkleTest {

    @Test
    fun testEmptyTrie() {
        val trie = Merkle.emptyTrie()
        assertEquals(0, trie.hash)
        assertTrue(trie.keys().isEmpty())
    }

    @Test
    fun testInsertSingleTimestamp() {
        val ts = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        var trie = Merkle.emptyTrie()
        trie = Merkle.insert(trie, ts)

        assertTrue(trie.hash != 0)
    }

    @Test
    fun testInsertMultipleTimestamps() {
        val ts1 = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val ts2 = Timestamp.parse("2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912")!!

        var trie = Merkle.emptyTrie()
        trie = Merkle.insert(trie, ts1)
        val hash1 = trie.hash

        trie = Merkle.insert(trie, ts2)
        val hash2 = trie.hash

        assertTrue(hash1 != hash2)
    }

    @Test
    fun testBuild() {
        val timestamps = listOf(
            Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!,
            Timestamp.parse("2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912")!!,
            Timestamp.parse("2025-04-24T22:25:42.123Z-0001-A219E7A71CC18912")!!
        )

        val trie = Merkle.build(timestamps)
        assertTrue(trie.hash != 0)
    }

    @Test
    fun testDiffIdenticalTries() {
        val ts = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val trie1 = Merkle.insert(Merkle.emptyTrie(), ts)
        val trie2 = Merkle.insert(Merkle.emptyTrie(), ts)

        assertNull(Merkle.diff(trie1, trie2))
    }

    @Test
    fun testDiffDifferentTries() {
        val ts1 = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val ts2 = Timestamp.parse("2025-04-24T22:24:42.123Z-0001-A219E7A71CC18912")!!

        val trie1 = Merkle.insert(Merkle.emptyTrie(), ts1)
        val trie2 = Merkle.insert(Merkle.emptyTrie(), ts2)

        val diff = Merkle.diff(trie1, trie2)
        assertNotNull(diff)
    }

    @Test
    fun testPrune() {
        val timestamps = (1..100).map { i ->
            Timestamp(1745533422123L + i * 60000, 0, "A219E7A71CC18912")
        }

        var trie = Merkle.emptyTrie()
        for (ts in timestamps) {
            trie = Merkle.insert(trie, ts)
        }

        val pruned = Merkle.prune(trie, 2)
        // Pruned trie should have same hash
        assertEquals(trie.hash, pruned.hash)
    }

    @Test
    fun testSerializeDeserialize() {
        val ts = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val trie = Merkle.insert(Merkle.emptyTrie(), ts)

        val json = Merkle.serialize(trie)
        val restored = Merkle.deserialize(json)

        assertEquals(trie.hash, restored.hash)
    }

    @Test
    fun testKeyToTimestamp() {
        // Base-3 key "0" should give timestamp 0
        assertEquals(0L, Merkle.keyToTimestamp("0"))

        // Non-zero key should give non-zero timestamp
        assertTrue(Merkle.keyToTimestamp("1") > 0)
    }
}
