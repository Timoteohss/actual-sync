package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BudgetUtilsTest {

    // ========== Month Calculation Tests ==========

    @Test
    fun testCalculatePreviousMonth_midYear() {
        assertEquals(202504L, BudgetUtils.calculatePreviousMonth(202505L))
        assertEquals(202506L, BudgetUtils.calculatePreviousMonth(202507L))
        assertEquals(202511L, BudgetUtils.calculatePreviousMonth(202512L))
    }

    @Test
    fun testCalculatePreviousMonth_january() {
        // January should go back to December of previous year
        assertEquals(202412L, BudgetUtils.calculatePreviousMonth(202501L))
        assertEquals(202312L, BudgetUtils.calculatePreviousMonth(202401L))
        assertEquals(199912L, BudgetUtils.calculatePreviousMonth(200001L))
    }

    @Test
    fun testCalculateNextMonth_midYear() {
        assertEquals(202506L, BudgetUtils.calculateNextMonth(202505L))
        assertEquals(202508L, BudgetUtils.calculateNextMonth(202507L))
        assertEquals(202502L, BudgetUtils.calculateNextMonth(202501L))
    }

    @Test
    fun testCalculateNextMonth_december() {
        // December should go forward to January of next year
        assertEquals(202501L, BudgetUtils.calculateNextMonth(202412L))
        assertEquals(202401L, BudgetUtils.calculateNextMonth(202312L))
        assertEquals(200001L, BudgetUtils.calculateNextMonth(199912L))
    }

    @Test
    fun testParseMonth() {
        assertEquals(2025 to 1, BudgetUtils.parseMonth(202501L))
        assertEquals(2025 to 12, BudgetUtils.parseMonth(202512L))
        assertEquals(1999 to 6, BudgetUtils.parseMonth(199906L))
    }

    @Test
    fun testCreateMonth() {
        assertEquals(202501L, BudgetUtils.createMonth(2025, 1))
        assertEquals(202512L, BudgetUtils.createMonth(2025, 12))
        assertEquals(199906L, BudgetUtils.createMonth(1999, 6))
    }

    @Test
    fun testMonthsBetween_sameYear() {
        assertEquals(0, BudgetUtils.monthsBetween(202505L, 202505L))
        assertEquals(3, BudgetUtils.monthsBetween(202501L, 202504L))
        assertEquals(-3, BudgetUtils.monthsBetween(202504L, 202501L))
        assertEquals(11, BudgetUtils.monthsBetween(202501L, 202512L))
    }

    @Test
    fun testMonthsBetween_differentYears() {
        assertEquals(12, BudgetUtils.monthsBetween(202401L, 202501L))
        assertEquals(13, BudgetUtils.monthsBetween(202412L, 202601L))
        assertEquals(24, BudgetUtils.monthsBetween(202301L, 202501L))
        assertEquals(-12, BudgetUtils.monthsBetween(202501L, 202401L))
    }

    // ========== Date Range Tests ==========

    @Test
    fun testMonthToDateRange_31DayMonths() {
        // January, March, May, July, August, October, December have 31 days
        assertEquals(20250101L to 20250131L, BudgetUtils.monthToDateRange(202501L))
        assertEquals(20250301L to 20250331L, BudgetUtils.monthToDateRange(202503L))
        assertEquals(20250501L to 20250531L, BudgetUtils.monthToDateRange(202505L))
        assertEquals(20250701L to 20250731L, BudgetUtils.monthToDateRange(202507L))
        assertEquals(20250801L to 20250831L, BudgetUtils.monthToDateRange(202508L))
        assertEquals(20251001L to 20251031L, BudgetUtils.monthToDateRange(202510L))
        assertEquals(20251201L to 20251231L, BudgetUtils.monthToDateRange(202512L))
    }

    @Test
    fun testMonthToDateRange_30DayMonths() {
        // April, June, September, November have 30 days
        assertEquals(20250401L to 20250430L, BudgetUtils.monthToDateRange(202504L))
        assertEquals(20250601L to 20250630L, BudgetUtils.monthToDateRange(202506L))
        assertEquals(20250901L to 20250930L, BudgetUtils.monthToDateRange(202509L))
        assertEquals(20251101L to 20251130L, BudgetUtils.monthToDateRange(202511L))
    }

    @Test
    fun testMonthToDateRange_february_nonLeapYear() {
        // 2025 is not a leap year
        assertEquals(20250201L to 20250228L, BudgetUtils.monthToDateRange(202502L))
        // 2023 is not a leap year
        assertEquals(20230201L to 20230228L, BudgetUtils.monthToDateRange(202302L))
    }

    @Test
    fun testMonthToDateRange_february_leapYear() {
        // 2024 is a leap year
        assertEquals(20240201L to 20240229L, BudgetUtils.monthToDateRange(202402L))
        // 2000 is a leap year (divisible by 400)
        assertEquals(20000201L to 20000229L, BudgetUtils.monthToDateRange(200002L))
    }

    @Test
    fun testIsLeapYear() {
        // Regular years divisible by 4
        assertTrue(BudgetUtils.isLeapYear(2024))
        assertTrue(BudgetUtils.isLeapYear(2020))
        assertTrue(BudgetUtils.isLeapYear(2016))

        // Non-leap years
        assertTrue(!BudgetUtils.isLeapYear(2025))
        assertTrue(!BudgetUtils.isLeapYear(2023))
        assertTrue(!BudgetUtils.isLeapYear(2019))

        // Century years - only leap if divisible by 400
        assertTrue(BudgetUtils.isLeapYear(2000))  // Divisible by 400
        assertTrue(!BudgetUtils.isLeapYear(1900)) // Divisible by 100 but not 400
        assertTrue(!BudgetUtils.isLeapYear(2100)) // Divisible by 100 but not 400
    }

    @Test
    fun testGetLastDayOfMonth() {
        // 31-day months
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 1))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 3))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 5))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 7))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 8))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 10))
        assertEquals(31, BudgetUtils.getLastDayOfMonth(2025, 12))

        // 30-day months
        assertEquals(30, BudgetUtils.getLastDayOfMonth(2025, 4))
        assertEquals(30, BudgetUtils.getLastDayOfMonth(2025, 6))
        assertEquals(30, BudgetUtils.getLastDayOfMonth(2025, 9))
        assertEquals(30, BudgetUtils.getLastDayOfMonth(2025, 11))

        // February
        assertEquals(28, BudgetUtils.getLastDayOfMonth(2025, 2)) // Non-leap
        assertEquals(29, BudgetUtils.getLastDayOfMonth(2024, 2)) // Leap
    }

    @Test
    fun testCalculateDateRangeForPreviousMonths_singleMonth() {
        // For month 202505, 1 previous month should give April 2025
        val (start, end) = BudgetUtils.calculateDateRangeForPreviousMonths(202505L, 1)
        assertEquals(20250401L, start)
        assertEquals(20250430L, end)
    }

    @Test
    fun testCalculateDateRangeForPreviousMonths_threeMonths() {
        // For month 202505, 3 previous months should be Feb 1 - Apr 30
        val (start, end) = BudgetUtils.calculateDateRangeForPreviousMonths(202505L, 3)
        assertEquals(20250201L, start)
        assertEquals(20250430L, end)
    }

    @Test
    fun testCalculateDateRangeForPreviousMonths_acrossYearBoundary() {
        // For month 202503, 6 previous months should be Sep 1, 2024 - Feb 28, 2025
        val (start, end) = BudgetUtils.calculateDateRangeForPreviousMonths(202503L, 6)
        assertEquals(20240901L, start)
        assertEquals(20250228L, end)
    }

    @Test
    fun testCalculateDateRangeForPreviousMonths_twelveMonths() {
        // For month 202501, 12 previous months should be Jan 1, 2024 - Dec 31, 2024
        val (start, end) = BudgetUtils.calculateDateRangeForPreviousMonths(202501L, 12)
        assertEquals(20240101L, start)
        assertEquals(20241231L, end)
    }

    // ========== Sort Order Calculation Tests ==========

    @Test
    fun testCalculateNewSortOrder_emptyList() {
        val result = BudgetUtils.calculateNewSortOrder(emptyList(), null)
        assertEquals(BudgetUtils.SORT_INCREMENT, result)
    }

    @Test
    fun testCalculateNewSortOrder_appendToEnd() {
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0,
            "c" to 3000.0
        )
        val result = BudgetUtils.calculateNewSortOrder(items, null)
        assertEquals(3000.0 + BudgetUtils.SORT_INCREMENT, result)
    }

    @Test
    fun testCalculateNewSortOrder_insertAtBeginning() {
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0,
            "c" to 3000.0
        )
        val result = BudgetUtils.calculateNewSortOrder(items, "a")
        assertEquals(500.0, result) // Half of first item
    }

    @Test
    fun testCalculateNewSortOrder_insertInMiddle() {
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0,
            "c" to 3000.0
        )
        // Insert before "b" - should be midpoint between "a" (1000) and "b" (2000)
        val result = BudgetUtils.calculateNewSortOrder(items, "b")
        assertEquals(1500.0, result)
    }

    @Test
    fun testCalculateNewSortOrder_insertBeforeLast() {
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0,
            "c" to 3000.0
        )
        // Insert before "c" - should be midpoint between "b" (2000) and "c" (3000)
        val result = BudgetUtils.calculateNewSortOrder(items, "c")
        assertEquals(2500.0, result)
    }

    @Test
    fun testCalculateNewSortOrder_targetNotFound() {
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0
        )
        // Target "x" doesn't exist, should append to end
        val result = BudgetUtils.calculateNewSortOrder(items, "x")
        assertEquals(2000.0 + BudgetUtils.SORT_INCREMENT, result)
    }

    @Test
    fun testCalculateNewSortOrder_withNullSortOrders() {
        val items = listOf(
            "a" to null,
            "b" to 1000.0,
            "c" to 2000.0
        )
        // Should handle null sort orders gracefully
        val result = BudgetUtils.calculateNewSortOrder(items, null)
        assertEquals(2000.0 + BudgetUtils.SORT_INCREMENT, result)
    }

    @Test
    fun testCalculateNewSortOrder_allNullSortOrders() {
        val items = listOf(
            "a" to null,
            "b" to null,
            "c" to null
        )
        // Should return SORT_INCREMENT when all are null
        val result = BudgetUtils.calculateNewSortOrder(items, null)
        assertEquals(BudgetUtils.SORT_INCREMENT, result)
    }

    @Test
    fun testCalculateNewSortOrder_consecutiveMoves() {
        // Simulate multiple moves to ensure midpoint calculation works
        val items = listOf(
            "a" to 1000.0,
            "b" to 2000.0
        )

        // First insert between a and b
        val first = BudgetUtils.calculateNewSortOrder(items, "b")
        assertEquals(1500.0, first)

        // Now simulate items list with new item
        val itemsAfterFirst = listOf(
            "a" to 1000.0,
            "new1" to 1500.0,
            "b" to 2000.0
        )

        // Insert before new1
        val second = BudgetUtils.calculateNewSortOrder(itemsAfterFirst, "new1")
        assertEquals(1250.0, second)
    }

    // ========== Sort Increment Constant Test ==========

    @Test
    fun testSortIncrementValue() {
        // Ensure the constant matches expected value
        assertEquals(16384.0, BudgetUtils.SORT_INCREMENT)
    }

    // ========== Edge Cases ==========

    @Test
    fun testPreviousAndNextMonthAreInverses() {
        val months = listOf(202501L, 202506L, 202512L, 202402L)
        for (month in months) {
            val prev = BudgetUtils.calculatePreviousMonth(month)
            val backToOriginal = BudgetUtils.calculateNextMonth(prev)
            assertEquals(month, backToOriginal, "prev then next should return original for $month")

            val next = BudgetUtils.calculateNextMonth(month)
            val backToOriginal2 = BudgetUtils.calculatePreviousMonth(next)
            assertEquals(month, backToOriginal2, "next then prev should return original for $month")
        }
    }

    @Test
    fun testParseAndCreateMonthAreInverses() {
        val months = listOf(202501L, 202512L, 199906L, 200012L)
        for (month in months) {
            val (year, m) = BudgetUtils.parseMonth(month)
            val recreated = BudgetUtils.createMonth(year, m)
            assertEquals(month, recreated)
        }
    }

    // ========== Validation Tests ==========

    @Test
    fun testIsValidMonth_validMonths() {
        assertTrue(BudgetUtils.isValidMonth(202501L))
        assertTrue(BudgetUtils.isValidMonth(202512L))
        assertTrue(BudgetUtils.isValidMonth(190001L))
        assertTrue(BudgetUtils.isValidMonth(209912L))
        assertTrue(BudgetUtils.isValidMonth(200006L))
    }

    @Test
    fun testIsValidMonth_invalidMonths() {
        // Invalid month (0)
        assertTrue(!BudgetUtils.isValidMonth(202500L))
        // Invalid month (13)
        assertTrue(!BudgetUtils.isValidMonth(202513L))
        // Year too old
        assertTrue(!BudgetUtils.isValidMonth(189912L))
        // Year too new
        assertTrue(!BudgetUtils.isValidMonth(210001L))
        // Malformed
        assertTrue(!BudgetUtils.isValidMonth(2025L))
        assertTrue(!BudgetUtils.isValidMonth(20250101L))
    }

    @Test
    fun testRequireValidMonth_valid() {
        // Should not throw
        BudgetUtils.requireValidMonth(202501L)
        BudgetUtils.requireValidMonth(202512L)
    }

    @Test
    fun testRequireValidMonth_invalid() {
        var thrown = false
        try {
            BudgetUtils.requireValidMonth(202513L)
        } catch (e: IllegalArgumentException) {
            thrown = true
            assertTrue(e.message?.contains("Invalid") == true)
        }
        assertTrue(thrown, "Should throw IllegalArgumentException for invalid month")
    }

    @Test
    fun testRequireValidId_valid() {
        // Should not throw
        BudgetUtils.requireValidId("abc-123")
        BudgetUtils.requireValidId("x")
    }

    @Test
    fun testRequireValidId_blank() {
        var thrown = false
        try {
            BudgetUtils.requireValidId("")
        } catch (e: IllegalArgumentException) {
            thrown = true
            assertTrue(e.message?.contains("blank") == true)
        }
        assertTrue(thrown, "Should throw IllegalArgumentException for blank id")
    }

    @Test
    fun testRequirePositive_valid() {
        // Should not throw
        BudgetUtils.requirePositive(1)
        BudgetUtils.requirePositive(100)
    }

    @Test
    fun testRequirePositive_zero() {
        var thrown = false
        try {
            BudgetUtils.requirePositive(0)
        } catch (e: IllegalArgumentException) {
            thrown = true
            assertTrue(e.message?.contains("positive") == true)
        }
        assertTrue(thrown, "Should throw IllegalArgumentException for zero")
    }

    @Test
    fun testRequirePositive_negative() {
        var thrown = false
        try {
            BudgetUtils.requirePositive(-5)
        } catch (e: IllegalArgumentException) {
            thrown = true
        }
        assertTrue(thrown, "Should throw IllegalArgumentException for negative")
    }

    // ========== Budget Averaging Rounding Tests ==========

    @Test
    fun testAverageRoundingBehavior() {
        // Test that kotlin.math.round behaves correctly for budget calculations
        // These simulate the budget averaging calculation

        // -10001 / 3 = -3333.67 -> should round to -3334
        val avg1 = kotlin.math.round(-10001.0 / 3).toLong()
        assertEquals(-3334L, avg1)

        // -10000 / 3 = -3333.33 -> should round to -3333
        val avg2 = kotlin.math.round(-10000.0 / 3).toLong()
        assertEquals(-3333L, avg2)

        // -10002 / 3 = -3334.00 -> should be exactly -3334
        val avg3 = kotlin.math.round(-10002.0 / 3).toLong()
        assertEquals(-3334L, avg3)

        // Positive case: 10001 / 3 = 3333.67 -> should round to 3334
        val avg4 = kotlin.math.round(10001.0 / 3).toLong()
        assertEquals(3334L, avg4)
    }

    @Test
    fun testSortOrderMidpointPrecision() {
        // Test that sort order midpoint calculations maintain precision
        // This verifies the fix for the sort_order precision issue

        val items = listOf(
            "a" to 16384.0,
            "b" to 16385.0  // Very close together
        )

        // Insert between a and b
        val midpoint = BudgetUtils.calculateNewSortOrder(items, "b")
        assertEquals(16384.5, midpoint)

        // Further subdivisions should also work
        val items2 = listOf(
            "a" to 16384.0,
            "new" to 16384.5,
            "b" to 16385.0
        )
        val midpoint2 = BudgetUtils.calculateNewSortOrder(items2, "new")
        assertEquals(16384.25, midpoint2)
    }

    @Test
    fun testSortOrderManySubdivisions() {
        // Test that we can subdivide sort orders many times without losing precision
        var sortOrder = BudgetUtils.SORT_INCREMENT // 16384.0
        val subdivisions = mutableListOf(sortOrder)

        // Subdivide 20 times (should still have precision)
        repeat(20) {
            sortOrder /= 2.0
            subdivisions.add(sortOrder)
        }

        // All values should be distinct
        val distinctCount = subdivisions.distinct().size
        assertEquals(21, distinctCount, "All 21 subdivisions should be distinct")

        // Last value should still be > 0
        assertTrue(subdivisions.last() > 0, "Should not reach zero after 20 subdivisions")
    }
}
