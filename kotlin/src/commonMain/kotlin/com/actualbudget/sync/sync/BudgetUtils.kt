package com.actualbudget.sync.sync

/**
 * Utility functions for budget calculations.
 * These are pure functions that can be easily unit tested.
 */
object BudgetUtils {

    /** Standard increment for sort order values */
    const val SORT_INCREMENT = 16384.0

    /**
     * Calculate the previous month given a month in YYYYMM format.
     *
     * @param month The month in YYYYMM format (e.g., 202501 for January 2025)
     * @return The previous month in YYYYMM format
     */
    fun calculatePreviousMonth(month: Long): Long {
        val year = (month / 100).toInt()
        val m = (month % 100).toInt()
        return if (m == 1) {
            ((year - 1) * 100 + 12).toLong()
        } else {
            (year * 100 + m - 1).toLong()
        }
    }

    /**
     * Calculate the next month given a month in YYYYMM format.
     *
     * @param month The month in YYYYMM format
     * @return The next month in YYYYMM format
     */
    fun calculateNextMonth(month: Long): Long {
        val year = (month / 100).toInt()
        val m = (month % 100).toInt()
        return if (m == 12) {
            ((year + 1) * 100 + 1).toLong()
        } else {
            (year * 100 + m + 1).toLong()
        }
    }

    /**
     * Calculate the date range (startDate, endDate) for N previous months.
     * Dates are in YYYYMMDD format.
     *
     * @param month Current month in YYYYMM format
     * @param n Number of previous months
     * @return Pair of (startDate, endDate) in YYYYMMDD format
     */
    fun calculateDateRangeForPreviousMonths(month: Long, n: Int): Pair<Long, Long> {
        var m = month
        repeat(n) {
            m = calculatePreviousMonth(m)
        }
        val startDate = m * 100 + 1 // First day of start month

        // End date is the last day of the month before current month
        val prevMonth = calculatePreviousMonth(month)
        val prevYear = (prevMonth / 100).toInt()
        val prevM = (prevMonth % 100).toInt()
        val lastDay = getLastDayOfMonth(prevYear, prevM)
        val endDate = prevMonth * 100 + lastDay

        return startDate to endDate
    }

    /**
     * Convert a month (YYYYMM) to a date range (first day to last day).
     *
     * @param month The month in YYYYMM format
     * @return Pair of (startDate, endDate) in YYYYMMDD format
     */
    fun monthToDateRange(month: Long): Pair<Long, Long> {
        val year = (month / 100).toInt()
        val m = (month % 100).toInt()
        val startDate = month * 100 + 1
        val lastDay = getLastDayOfMonth(year, m)
        val endDate = month * 100 + lastDay
        return startDate to endDate
    }

    /**
     * Get the last day of a month.
     *
     * @param year The year
     * @param month The month (1-12)
     * @return The last day of the month (28, 29, 30, or 31)
     */
    fun getLastDayOfMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31
        }
    }

    /**
     * Check if a year is a leap year.
     */
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    /**
     * Calculate the new sort order for an item being moved.
     * Uses midpoint calculation when inserting between items, or appends with SORT_INCREMENT.
     *
     * @param sortOrders List of (id, sortOrder) pairs, ordered by sort_order
     * @param targetId ID of item to insert before, or null to append
     * @return New sort order for the item being moved
     */
    fun calculateNewSortOrder(sortOrders: List<Pair<String, Double?>>, targetId: String?): Double {
        if (sortOrders.isEmpty()) {
            return SORT_INCREMENT
        }

        if (targetId == null) {
            // Append at end
            val maxSortOrder = sortOrders.mapNotNull { it.second }.maxOrNull() ?: 0.0
            return maxSortOrder + SORT_INCREMENT
        }

        val targetIndex = sortOrders.indexOfFirst { it.first == targetId }
        if (targetIndex == -1) {
            // Target not found, append at end
            val maxSortOrder = sortOrders.mapNotNull { it.second }.maxOrNull() ?: 0.0
            return maxSortOrder + SORT_INCREMENT
        }

        val targetSortOrder = sortOrders[targetIndex].second ?: 0.0

        if (targetIndex == 0) {
            // Insert at beginning - use half of first item's sort order
            return targetSortOrder / 2.0
        }

        // Insert between prev and target
        val prevSortOrder = sortOrders[targetIndex - 1].second ?: 0.0
        return (prevSortOrder + targetSortOrder) / 2.0
    }

    /**
     * Parse a month from YYYYMM format to year and month components.
     *
     * @param month The month in YYYYMM format
     * @return Pair of (year, month)
     */
    fun parseMonth(month: Long): Pair<Int, Int> {
        val year = (month / 100).toInt()
        val m = (month % 100).toInt()
        return year to m
    }

    /**
     * Create a month in YYYYMM format from year and month components.
     *
     * @param year The year
     * @param month The month (1-12)
     * @return The month in YYYYMM format
     */
    fun createMonth(year: Int, month: Int): Long {
        return (year * 100 + month).toLong()
    }

    /**
     * Calculate the number of months between two months.
     *
     * @param fromMonth Start month in YYYYMM format
     * @param toMonth End month in YYYYMM format
     * @return Number of months between (positive if toMonth > fromMonth)
     */
    fun monthsBetween(fromMonth: Long, toMonth: Long): Int {
        val (fromYear, fromM) = parseMonth(fromMonth)
        val (toYear, toM) = parseMonth(toMonth)
        return (toYear - fromYear) * 12 + (toM - fromM)
    }

    // ========== Validation Helpers ==========

    /**
     * Validate that a month is in valid YYYYMM format.
     * Valid range: 190001 to 209912 (years 1900-2099)
     *
     * @param month The month to validate
     * @return true if valid, false otherwise
     */
    fun isValidMonth(month: Long): Boolean {
        val year = (month / 100).toInt()
        val m = (month % 100).toInt()
        return year in 1900..2099 && m in 1..12
    }

    /**
     * Validate month format and throw if invalid.
     *
     * @param month The month to validate
     * @param paramName Parameter name for error message
     * @throws IllegalArgumentException if month is invalid
     */
    fun requireValidMonth(month: Long, paramName: String = "month") {
        require(isValidMonth(month)) {
            "Invalid $paramName format: $month. Expected YYYYMM format (e.g., 202501)"
        }
    }

    /**
     * Validate that a string ID is not blank.
     *
     * @param id The ID to validate
     * @param paramName Parameter name for error message
     * @throws IllegalArgumentException if ID is blank
     */
    fun requireValidId(id: String, paramName: String = "id") {
        require(id.isNotBlank()) {
            "$paramName cannot be blank"
        }
    }

    /**
     * Validate that a count is positive.
     *
     * @param n The count to validate
     * @param paramName Parameter name for error message
     * @throws IllegalArgumentException if count is not positive
     */
    fun requirePositive(n: Int, paramName: String = "count") {
        require(n > 0) {
            "$paramName must be positive, got: $n"
        }
    }
}
