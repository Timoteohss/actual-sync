package com.actualbudget.sync.crdt

import com.benasher44.uuid.uuid4
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Hybrid Unique Logical Clock (HULC) timestamp.
 *
 * Globally-unique, monotonic timestamps are generated from the combination of:
 * - Unreliable system time
 * - A counter for ordering within the same millisecond
 * - A node identifier for the current client
 *
 * Format: `{ISO-8601}-{counter}-{nodeId}`
 * Example: `2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912`
 */
data class Timestamp(
    val millis: Long,
    val counter: Int,
    val node: String
) : Comparable<Timestamp> {

    override fun toString(): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val isoDate = instant.toString()
        val counterHex = counter.toString(16).uppercase().padStart(4, '0')
        val paddedNode = node.padStart(16, '0').takeLast(16)
        return "$isoDate-$counterHex-$paddedNode"
    }

    /**
     * MurmurHash3 of the timestamp string for merkle trie insertion.
     */
    fun hash(): Int = MurmurHash3.hash32(toString())

    /**
     * Compare timestamps using numeric fields for performance.
     * Avoids string allocation on every comparison.
     * Order: millis → counter → node (lexicographic)
     */
    override fun compareTo(other: Timestamp): Int {
        // Compare millis first (most significant)
        val millisCmp = millis.compareTo(other.millis)
        if (millisCmp != 0) return millisCmp

        // Then counter
        val counterCmp = counter.compareTo(other.counter)
        if (counterCmp != 0) return counterCmp

        // Finally node (string comparison for tie-breaker)
        return node.compareTo(other.node)
    }

    companion object {
        private const val MAX_COUNTER = 0xFFFF
        private const val MAX_NODE_LENGTH = 16
        private const val MAX_DRIFT_MS = 5 * 60 * 1000L // 5 minutes

        val ZERO = Timestamp(0, 0, "0000000000000000")
        val MAX = parse("9999-12-31T23:59:59.999Z-FFFF-FFFFFFFFFFFFFFFF")!!

        /**
         * Generate a unique client ID (last 16 hex chars of a UUID).
         */
        fun makeClientId(): String {
            return uuid4().toString().replace("-", "").takeLast(16).uppercase()
        }

        /**
         * Parse a timestamp string into a Timestamp object.
         */
        fun parse(timestamp: String): Timestamp? {
            val parts = timestamp.split("-")
            if (parts.size != 5) return null

            return try {
                val dateStr = "${parts[0]}-${parts[1]}-${parts[2]}"
                val instant = Instant.parse(dateStr)
                val millis = instant.toEpochMilliseconds()
                val counter = parts[3].toIntOrNull(16) ?: return null
                val node = parts[4]

                if (counter > MAX_COUNTER) return null
                if (node.length > MAX_NODE_LENGTH) return null

                Timestamp(millis, counter, node)
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Create a timestamp string for querying "since" a given ISO date.
         */
        fun since(isoString: String): String {
            return "$isoString-0000-0000000000000000"
        }
    }

    // Error types
    class ClockDriftError(message: String = "Maximum clock drift exceeded") : Exception(message)
    class OverflowError : Exception("Timestamp counter overflow")
    class InvalidError(timestamp: String) : Exception("Invalid timestamp: $timestamp")
}

/**
 * Mutable clock state for timestamp generation.
 */
class MutableClock(
    var millis: Long = 0,
    var counter: Int = 0,
    val node: String
) {
    /**
     * Generate a new timestamp for a local change (send operation).
     */
    fun send(): Timestamp {
        val phys = Clock.System.now().toEpochMilliseconds()

        val lNew = maxOf(millis, phys)
        val cNew = if (lNew == millis) counter + 1 else 0

        if (lNew - phys > MAX_DRIFT_MS) {
            throw Timestamp.ClockDriftError("Drift: ${lNew - phys}ms exceeds max ${MAX_DRIFT_MS}ms")
        }
        if (cNew > MAX_COUNTER) {
            throw Timestamp.OverflowError()
        }

        millis = lNew
        counter = cNew

        return Timestamp(millis, counter, node)
    }

    /**
     * Receive and merge a timestamp from a remote client.
     */
    fun recv(msg: Timestamp): Timestamp {
        val phys = Clock.System.now().toEpochMilliseconds()

        if (msg.millis - phys > MAX_DRIFT_MS) {
            throw Timestamp.ClockDriftError()
        }

        val lNew = maxOf(millis, phys, msg.millis)
        val cNew = when {
            lNew == millis && lNew == msg.millis -> maxOf(counter, msg.counter) + 1
            lNew == millis -> counter + 1
            lNew == msg.millis -> msg.counter + 1
            else -> 0
        }

        if (lNew - phys > MAX_DRIFT_MS) {
            throw Timestamp.ClockDriftError()
        }
        if (cNew > MAX_COUNTER) {
            throw Timestamp.OverflowError()
        }

        millis = lNew
        counter = cNew

        return Timestamp(millis, counter, node)
    }

    fun toTimestamp(): Timestamp = Timestamp(millis, counter, node)

    companion object {
        private const val MAX_DRIFT_MS = 5 * 60 * 1000L
        private const val MAX_COUNTER = 0xFFFF

        fun from(timestamp: Timestamp): MutableClock {
            return MutableClock(timestamp.millis, timestamp.counter, timestamp.node)
        }
    }
}
