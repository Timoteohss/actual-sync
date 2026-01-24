package com.actualbudget.sync.crdt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.concurrent.Volatile

/**
 * Clock state containing the current timestamp and merkle trie.
 */
@Serializable
data class ClockState(
    val timestamp: String,
    val merkle: TrieNode
)

/**
 * Global clock management for CRDT operations.
 *
 * Thread-safe singleton using @Volatile for visibility.
 * Note: In typical usage patterns (single sync operation at a time),
 * this provides sufficient thread safety. For concurrent access,
 * external synchronization should be used.
 */
object ClockManager {
    @Volatile
    private var clock: SyncClock? = null

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    /**
     * Get the current clock instance.
     * Thread-safe read using volatile.
     */
    fun getClock(): SyncClock? = clock

    /**
     * Set the global clock instance.
     * Thread-safe write using volatile.
     */
    fun setClock(newClock: SyncClock) {
        clock = newClock
    }

    /**
     * Create a new clock with the given timestamp and merkle trie.
     */
    fun makeClock(timestamp: Timestamp, merkle: TrieNode = Merkle.emptyTrie()): SyncClock {
        return SyncClock(
            timestamp = MutableClock.from(timestamp),
            initialMerkle = merkle
        )
    }

    /**
     * Serialize the clock state to a JSON string.
     */
    fun serialize(clock: SyncClock): String {
        val state = ClockState(
            timestamp = clock.timestamp.toTimestamp().toString(),
            merkle = clock.merkle
        )
        return json.encodeToString(ClockState.serializer(), state)
    }

    /**
     * Deserialize clock state from a JSON string.
     *
     * If deserialization fails, logs a warning and returns a fresh clock.
     * This prevents sync state loss from going unnoticed.
     */
    fun deserialize(data: String): SyncClock {
        return try {
            val state = json.decodeFromString(ClockState.serializer(), data)
            val ts = Timestamp.parse(state.timestamp)
                ?: throw Timestamp.InvalidError(state.timestamp)
            SyncClock(
                timestamp = MutableClock.from(ts),
                initialMerkle = state.merkle
            )
        } catch (e: Exception) {
            // Log warning - silent failures can cause sync issues
            println("[ClockManager] Warning: Failed to deserialize clock state: ${e.message}. Creating fresh clock.")
            // Return a fresh clock with new client ID
            val nodeId = Timestamp.makeClientId()
            SyncClock(
                timestamp = MutableClock(0, 0, nodeId),
                initialMerkle = Merkle.emptyTrie()
            )
        }
    }
}

/**
 * Sync clock containing mutable timestamp and merkle trie state.
 *
 * Thread-safety note: Uses @Volatile for visibility. For concurrent access
 * from multiple threads, external synchronization should be used.
 * In typical usage (single sync operation at a time), this is sufficient.
 */
class SyncClock(
    val timestamp: MutableClock,
    initialMerkle: TrieNode
) {
    @Volatile
    private var _merkle: TrieNode = initialMerkle

    /**
     * Get the current merkle trie.
     */
    val merkle: TrieNode get() = _merkle

    /**
     * Generate a new timestamp for a local change.
     */
    fun send(): Timestamp {
        val ts = timestamp.send()
        _merkle = Merkle.insert(_merkle, ts)
        _merkle = Merkle.prune(_merkle)
        return ts
    }

    /**
     * Receive and apply a timestamp from a remote client.
     */
    fun recv(msg: Timestamp): Timestamp {
        val ts = timestamp.recv(msg)
        _merkle = Merkle.insert(_merkle, msg)
        _merkle = Merkle.prune(_merkle)
        return ts
    }

    /**
     * Get the current timestamp without advancing the clock.
     */
    fun current(): Timestamp = timestamp.toTimestamp()

    /**
     * Update the merkle trie directly (for deserialization/sync).
     */
    fun setMerkle(newMerkle: TrieNode) {
        _merkle = newMerkle
    }
}
