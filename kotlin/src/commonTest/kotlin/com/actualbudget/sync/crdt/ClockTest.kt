package com.actualbudget.sync.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClockTest {

    @Test
    fun testMutableClockSend() {
        val clock = MutableClock(node = Timestamp.makeClientId())

        val ts1 = clock.send()
        val ts2 = clock.send()

        assertTrue(ts1 < ts2, "Timestamps should be monotonically increasing")
    }

    @Test
    fun testMutableClockSendIncrementsCounter() {
        val clock = MutableClock(millis = 1700000000000L, counter = 0, node = "TESTNODEID123456")

        // Send multiple times quickly - counter should increment
        val ts1 = clock.send()
        val ts2 = clock.send()
        val ts3 = clock.send()

        // If all in same millisecond, counters should increment
        if (ts1.millis == ts2.millis && ts2.millis == ts3.millis) {
            assertTrue(ts1.counter < ts2.counter)
            assertTrue(ts2.counter < ts3.counter)
        }
    }

    @Test
    fun testMutableClockRecv() {
        val clock = MutableClock(node = "LOCALNODEID12345")
        val remoteTs = Timestamp(
            millis = 1700000001000L, // Fixed future timestamp
            counter = 5,
            node = "REMOTENODEID1234"
        )

        val result = clock.recv(remoteTs)
        assertNotNull(result)

        // Clock should have advanced
        assertTrue(clock.millis >= remoteTs.millis)
    }

    @Test
    fun testSyncClockSendUpdatesMerkle() {
        val nodeId = Timestamp.makeClientId()
        val syncClock = ClockManager.makeClock(Timestamp(0, 0, nodeId))

        val initialHash = syncClock.merkle.hash

        syncClock.send()

        assertTrue(syncClock.merkle.hash != initialHash)
    }

    @Test
    fun testSyncClockRecvUpdatesMerkle() {
        val nodeId = Timestamp.makeClientId()
        val syncClock = ClockManager.makeClock(Timestamp(0, 0, nodeId))

        val initialHash = syncClock.merkle.hash

        val remoteTs = Timestamp(
            millis = 1700000000000L,
            counter = 0,
            node = "REMOTENODEID1234"
        )
        syncClock.recv(remoteTs)

        assertTrue(syncClock.merkle.hash != initialHash)
    }

    @Test
    fun testClockSerializeDeserialize() {
        val nodeId = Timestamp.makeClientId()
        val syncClock = ClockManager.makeClock(Timestamp(1000, 5, nodeId))
        syncClock.send()
        syncClock.send()

        val serialized = ClockManager.serialize(syncClock)
        val restored = ClockManager.deserialize(serialized)

        assertEquals(syncClock.merkle.hash, restored.merkle.hash)
        assertEquals(syncClock.timestamp.node, restored.timestamp.node)
    }

    @Test
    fun testClockManagerSetGet() {
        val nodeId = Timestamp.makeClientId()
        val clock = ClockManager.makeClock(Timestamp(0, 0, nodeId))

        ClockManager.setClock(clock)

        assertEquals(clock, ClockManager.getClock())
    }
}
