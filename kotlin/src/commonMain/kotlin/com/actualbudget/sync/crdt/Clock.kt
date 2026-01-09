package com.actualbudget.sync.crdt

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
 */
object ClockManager {
    private var clock: SyncClock? = null

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    /**
     * Get the current clock instance.
     */
    fun getClock(): SyncClock? = clock

    /**
     * Set the global clock instance.
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
            merkle = merkle
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
     */
    fun deserialize(data: String): SyncClock {
        return try {
            val state = json.decodeFromString(ClockState.serializer(), data)
            val ts = Timestamp.parse(state.timestamp)
                ?: throw Timestamp.InvalidError(state.timestamp)
            SyncClock(
                timestamp = MutableClock.from(ts),
                merkle = state.merkle
            )
        } catch (e: Exception) {
            // Return a fresh clock with new client ID
            val nodeId = Timestamp.makeClientId()
            SyncClock(
                timestamp = MutableClock(0, 0, nodeId),
                merkle = Merkle.emptyTrie()
            )
        }
    }
}

/**
 * Sync clock containing mutable timestamp and merkle trie state.
 */
data class SyncClock(
    val timestamp: MutableClock,
    var merkle: TrieNode
) {
    /**
     * Generate a new timestamp for a local change.
     */
    fun send(): Timestamp {
        val ts = timestamp.send()
        merkle = Merkle.insert(merkle, ts)
        merkle = Merkle.prune(merkle)
        return ts
    }

    /**
     * Receive and apply a timestamp from a remote client.
     */
    fun recv(msg: Timestamp): Timestamp {
        val ts = timestamp.recv(msg)
        merkle = Merkle.insert(merkle, msg)
        merkle = Merkle.prune(merkle)
        return ts
    }

    /**
     * Get the current timestamp without advancing the clock.
     */
    fun current(): Timestamp = timestamp.toTimestamp()
}
