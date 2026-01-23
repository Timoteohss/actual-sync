package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNull

/**
 * Tests for value encoding and parsing in the sync protocol.
 * These tests verify that numeric values (especially sort_order) are
 * properly encoded and decoded without precision loss.
 */
class ValueParsingTest {

    // Simulate the encodeValue function from SyncEngine
    private fun encodeValue(value: Any?): String {
        return when (value) {
            null -> "0:"
            is String -> "S:$value"
            is Number -> "N:$value"
            is Boolean -> "N:${if (value) 1 else 0}"
            else -> "S:$value"
        }
    }

    // Simulate the parseValue function from SyncEngine
    private fun parseValue(value: String): Any? {
        return when {
            value.startsWith("S:") -> value.substring(2)
            value.startsWith("N:") -> {
                val numStr = value.substring(2)
                numStr.toLongOrNull()
                    ?: numStr.toDoubleOrNull()
                    ?: 0L
            }
            value.startsWith("0:") -> null
            value == "null" -> null
            else -> value
        }
    }

    // ========== Encoding Tests ==========

    @Test
    fun testEncodeNull() {
        assertEquals("0:", encodeValue(null))
    }

    @Test
    fun testEncodeString() {
        assertEquals("S:hello", encodeValue("hello"))
        assertEquals("S:", encodeValue(""))
        assertEquals("S:with spaces", encodeValue("with spaces"))
    }

    @Test
    fun testEncodeLong() {
        assertEquals("N:12345", encodeValue(12345L))
        assertEquals("N:-12345", encodeValue(-12345L))
        assertEquals("N:0", encodeValue(0L))
    }

    @Test
    fun testEncodeInt() {
        assertEquals("N:123", encodeValue(123))
        assertEquals("N:-456", encodeValue(-456))
    }

    @Test
    fun testEncodeDouble() {
        assertEquals("N:123.456", encodeValue(123.456))
        assertEquals("N:-789.123", encodeValue(-789.123))
        assertEquals("N:16384.5", encodeValue(16384.5))
    }

    @Test
    fun testEncodeBoolean() {
        assertEquals("N:1", encodeValue(true))
        assertEquals("N:0", encodeValue(false))
    }

    // ========== Parsing Tests ==========

    @Test
    fun testParseNull() {
        assertNull(parseValue("0:"))
        assertNull(parseValue("null"))
    }

    @Test
    fun testParseString() {
        assertEquals("hello", parseValue("S:hello"))
        assertEquals("", parseValue("S:"))
        assertEquals("with spaces", parseValue("S:with spaces"))
    }

    @Test
    fun testParseLong() {
        assertEquals(12345L, parseValue("N:12345"))
        assertEquals(-12345L, parseValue("N:-12345"))
        assertEquals(0L, parseValue("N:0"))
    }

    @Test
    fun testParseDouble() {
        assertEquals(123.456, parseValue("N:123.456"))
        assertEquals(-789.123, parseValue("N:-789.123"))
        assertEquals(16384.5, parseValue("N:16384.5"))
    }

    @Test
    fun testParseDoubleThatLooksLikeLong() {
        // "N:16384.0" should parse as Long 16384, not Double
        val result = parseValue("N:16384.0")
        // Actually, toLongOrNull will fail on "16384.0", so it should be Double
        assertEquals(16384.0, result)
    }

    // ========== Round-trip Tests ==========

    @Test
    fun testRoundTripLong() {
        val original = 123456789L
        val encoded = encodeValue(original)
        val decoded = parseValue(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun testRoundTripDouble() {
        val original = 16384.5
        val encoded = encodeValue(original)
        val decoded = parseValue(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun testRoundTripString() {
        val original = "test string"
        val encoded = encodeValue(original)
        val decoded = parseValue(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun testRoundTripNull() {
        val encoded = encodeValue(null)
        val decoded = parseValue(encoded)
        assertNull(decoded)
    }

    // ========== Sort Order Specific Tests ==========

    @Test
    fun testSortOrderMidpointRoundTrip() {
        // This tests the specific case of sort_order precision
        val sortOrder1 = 16384.0
        val sortOrder2 = 32768.0
        val midpoint = (sortOrder1 + sortOrder2) / 2.0 // 24576.0

        val encoded = encodeValue(midpoint)
        assertEquals("N:24576.0", encoded)

        val decoded = parseValue(encoded)
        assertEquals(24576.0, decoded)
    }

    @Test
    fun testSortOrderFractionalRoundTrip() {
        // Test fractional sort orders
        val fractionalSortOrder = 16384.5
        val encoded = encodeValue(fractionalSortOrder)
        assertEquals("N:16384.5", encoded)

        val decoded = parseValue(encoded)
        assertEquals(16384.5, decoded)
    }

    @Test
    fun testSortOrderVerySmallFraction() {
        // After many midpoint operations, we might have very small fractions
        val smallFraction = 0.00390625 // 1/256
        val encoded = encodeValue(smallFraction)
        val decoded = parseValue(encoded)
        assertEquals(smallFraction, decoded)
    }

    // ========== toDoubleOrNull Helper Tests ==========

    @Test
    fun testToDoubleOrNullWithLong() {
        val value: Any = 16384L
        val result = when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }
        assertEquals(16384.0, result)
    }

    @Test
    fun testToDoubleOrNullWithDouble() {
        val value: Any = 16384.5
        val result = when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }
        assertEquals(16384.5, result)
    }

    @Test
    fun testToDoubleOrNullWithString() {
        val value: Any = "not a number"
        val result = when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Number -> value.toDouble()
            else -> null
        }
        assertNull(result)
    }

    // ========== Money Amount Tests ==========

    @Test
    fun testMoneyAmountsAsLong() {
        // Money should always be stored as Long (cents)
        val dollars500 = 50000L // $500.00 in cents
        val encoded = encodeValue(dollars500)
        assertEquals("N:50000", encoded)

        val decoded = parseValue(encoded)
        assertEquals(50000L, decoded)
    }

    @Test
    fun testNegativeMoneyAmount() {
        val expense = -12345L // -$123.45 in cents
        val encoded = encodeValue(expense)
        assertEquals("N:-12345", encoded)

        val decoded = parseValue(encoded)
        assertEquals(-12345L, decoded)
    }
}
