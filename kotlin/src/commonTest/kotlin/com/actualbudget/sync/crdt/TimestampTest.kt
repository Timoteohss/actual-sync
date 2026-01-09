package com.actualbudget.sync.crdt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TimestampTest {

    @Test
    fun testParseValidTimestamp() {
        val ts = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")
        assertNotNull(ts)
        assertEquals(1745533422123, ts.millis)
        assertEquals(1, ts.counter)
        assertEquals("A219E7A71CC18912", ts.node)
    }

    @Test
    fun testParseInvalidTimestamp() {
        assertNull(Timestamp.parse("invalid"))
        assertNull(Timestamp.parse("2025-04-24T22:23:42.123Z"))
        assertNull(Timestamp.parse("2025-04-24T22:23:42.123Z-0001"))
    }

    @Test
    fun testTimestampToString() {
        val ts = Timestamp(1745533422123, 1, "A219E7A71CC18912")
        val str = ts.toString()
        assertTrue(str.contains("2025-04-24"))
        assertTrue(str.contains("-0001-"))
        assertTrue(str.contains("A219E7A71CC18912"))
    }

    @Test
    fun testTimestampRoundTrip() {
        val original = "2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912"
        val ts = Timestamp.parse(original)
        assertNotNull(ts)
        val result = ts.toString()
        // Parse and re-serialize
        val ts2 = Timestamp.parse(result)
        assertNotNull(ts2)
        assertEquals(ts.millis, ts2.millis)
        assertEquals(ts.counter, ts2.counter)
        assertEquals(ts.node, ts2.node)
    }

    @Test
    fun testMakeClientId() {
        val id1 = Timestamp.makeClientId()
        val id2 = Timestamp.makeClientId()
        assertEquals(16, id1.length)
        assertEquals(16, id2.length)
        assertTrue(id1 != id2, "Client IDs should be unique")
    }

    @Test
    fun testTimestampHash() {
        val ts1 = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val ts2 = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val ts3 = Timestamp.parse("2025-04-24T22:23:42.123Z-0002-A219E7A71CC18912")!!

        assertEquals(ts1.hash(), ts2.hash())
        assertTrue(ts1.hash() != ts3.hash())
    }

    @Test
    fun testTimestampComparison() {
        val ts1 = Timestamp.parse("2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912")!!
        val ts2 = Timestamp.parse("2025-04-24T22:23:42.123Z-0002-A219E7A71CC18912")!!
        val ts3 = Timestamp.parse("2025-04-25T22:23:42.123Z-0001-A219E7A71CC18912")!!

        assertTrue(ts1 < ts2)
        assertTrue(ts2 < ts3)
        assertTrue(ts1 < ts3)
    }

    @Test
    fun testZeroTimestamp() {
        assertEquals(0, Timestamp.ZERO.millis)
        assertEquals(0, Timestamp.ZERO.counter)
        assertEquals("0000000000000000", Timestamp.ZERO.node)
    }
}
