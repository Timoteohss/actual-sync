package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Budget operations for the Actual Budget database.
 *
 * Handles:
 * - Budget CRUD (set amount, goal, carryover)
 * - Copy/zero budget operations
 * - Budget transfers between categories
 * - Hold for next month
 * - Cover overspending operations
 * - To-budget calculations
 *
 * All mutation methods use the sync engine to create CRDT changes
 * that propagate to the server.
 *
 * @param engine The change engine for creating sync changes
 * @param database The database for reading budget data
 */
class BudgetOperations(
    private val engine: ChangeEngine,
    private val database: ActualDatabase
) {
    // ========== Basic Budget CRUD ==========

    /**
     * Set the budget amount for a category in a specific month.
     *
     * @param categoryId The category ID to set budget for
     * @param month The month in YYYYMM format (e.g., 202501 for January 2025)
     * @param amount The budget amount in cents (e.g., 50000 for $500.00)
     * @throws IllegalArgumentException if month format is invalid or categoryId is blank
     */
    fun setBudgetAmount(categoryId: String, month: Long, amount: Long) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)

        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "amount", amount)
    }

    /**
     * Set a budget goal for a category in a specific month.
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param goal The goal amount in cents (or null to clear)
     */
    fun setBudgetGoal(categoryId: String, month: Long, goal: Long?) {
        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "goal", goal)
    }

    /**
     * Set the carryover amount for a category in a specific month.
     * Carryover is used for envelope budgeting to carry unused funds to next month.
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param carryover The carryover amount in cents
     */
    fun setBudgetCarryover(categoryId: String, month: Long, carryover: Long) {
        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "carryover", carryover)
    }

    // ========== Copy/Zero Operations ==========

    /**
     * Copy budget amounts from previous month to current month.
     * Useful for quickly setting up a new month's budget.
     *
     * @param targetMonth The month to copy TO in YYYYMM format
     */
    fun copyBudgetFromPreviousMonth(targetMonth: Long) {
        val prevMonth = BudgetUtils.calculatePreviousMonth(targetMonth)

        // Get budgets from previous month
        val prevBudgets = database.actualDatabaseQueries.getBudgetForMonth(prevMonth).executeAsList()

        // Copy each budget to target month
        for (budget in prevBudgets) {
            if (budget.tombstone == 0L) {
                setBudgetAmount(budget.category, targetMonth, budget.amount)
            }
        }
    }

    /**
     * Set all budget amounts for a month to zero.
     *
     * @param month The month in YYYYMM format
     */
    fun zeroBudgetsForMonth(month: Long) {
        val budgets = database.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()
        for (budget in budgets) {
            if (budget.tombstone == 0L) {
                setBudgetAmount(budget.category, month, 0)
            }
        }
    }

    // ========== Transfer Budget ==========

    /**
     * Transfer budget amount from one category to another within the same month.
     *
     * @param fromCategoryId Source category ID
     * @param toCategoryId Destination category ID
     * @param month The month in YYYYMM format
     * @param amount Amount to transfer in cents
     */
    fun transferBudget(fromCategoryId: String, toCategoryId: String, month: Long, amount: Long) {
        BudgetUtils.requireValidId(fromCategoryId, "fromCategoryId")
        BudgetUtils.requireValidId(toCategoryId, "toCategoryId")
        BudgetUtils.requireValidMonth(month)

        // Get current budgets
        val fromBudgetId = "$month-$fromCategoryId"
        val toBudgetId = "$month-$toCategoryId"

        val fromBudget = database.actualDatabaseQueries.getBudgetById(fromBudgetId).executeAsOneOrNull()
        val toBudget = database.actualDatabaseQueries.getBudgetById(toBudgetId).executeAsOneOrNull()

        val fromAmount = fromBudget?.amount ?: 0L
        val toAmount = toBudget?.amount ?: 0L

        // Update both categories
        setBudgetAmount(fromCategoryId, month, fromAmount - amount)
        setBudgetAmount(toCategoryId, month, toAmount + amount)
    }

    // ========== Hold Operations ==========

    /**
     * Hold money for next month.
     * Sets aside part of the "To Budget" amount to be available next month.
     *
     * @param month The month in YYYYMM format
     * @param amount The amount to hold (will be constrained to available)
     * @return true if successful, false if not enough money to hold
     */
    fun holdForNextMonth(month: Long, amount: Long): Boolean {
        val monthId = month.toString()
        val current = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        val currentBuffered = current?.buffered ?: 0L

        // Calculate available to-budget
        val toBudget = calculateToBudget(month)

        if (toBudget <= 0) return false

        // Constrain amount: not negative, not more than available
        val actualAmount = maxOf(0L, minOf(amount, toBudget))
        val newBuffered = currentBuffered + actualAmount

        engine.createChange("zero_budget_months", monthId, "buffered", newBuffered)
        return true
    }

    /**
     * Reset the hold amount for a month.
     *
     * @param month The month in YYYYMM format
     */
    fun resetHold(month: Long) {
        engine.createChange("zero_budget_months", month.toString(), "buffered", 0L)
    }

    /**
     * Get the buffered (held) amount for a month.
     *
     * @param month The month in YYYYMM format
     * @return The buffered amount
     */
    fun getBufferedAmount(month: Long): Long {
        val monthId = month.toString()
        val budgetMonth = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        return budgetMonth?.buffered ?: 0L
    }

    // ========== Calculations ==========

    /**
     * Calculate the "To Budget" amount for a month.
     * This is: total income - total budgeted - current buffered.
     *
     * @param month The month in YYYYMM format
     * @return The available amount to budget
     */
    fun calculateToBudget(month: Long): Long {
        // Get total income for the month
        val totalIncome = database.actualDatabaseQueries
            .getTotalIncomeForMonth(month)
            .executeAsOne()
            .toLong()

        // Get total budgeted for the month
        val totalBudgeted = database.actualDatabaseQueries
            .getTotalBudgetedForMonth(month)
            .executeAsOne()
            .toLong()

        // Get current buffered amount
        val monthId = month.toString()
        val budgetMonth = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        val buffered = budgetMonth?.buffered ?: 0L

        // Calculate to-budget
        return totalIncome - totalBudgeted - buffered
    }

    /**
     * Calculate the leftover (available) amount for a category in a month.
     * Leftover = budgeted + spent (spent is negative for expenses).
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @return The available/leftover amount (negative if overspent)
     */
    fun calculateCategoryLeftover(categoryId: String, month: Long): Long {
        val budgetId = "$month-$categoryId"
        val budget = database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()
        val budgeted = budget?.amount ?: 0L

        val (startDate, endDate) = BudgetUtils.monthToDateRange(month)
        val spentData = database.actualDatabaseQueries
            .getSpentByCategory(startDate, endDate)
            .executeAsList()

        val spentResult = spentData.find { it.category == categoryId }
        val spent: Long = spentResult?.spent?.toLong() ?: 0L

        return budgeted + spent // spent is negative for expenses
    }

    // ========== Cover Operations ==========

    /**
     * Cover overspending in one category from another.
     * Transfers budget from a category with available funds to cover overspending.
     *
     * @param fromCategoryId Category with available funds
     * @param toCategoryId Overspent category to cover
     * @param month The month in YYYYMM format
     * @param amount Amount to cover (or null to cover full overspending)
     */
    fun coverOverspending(
        fromCategoryId: String,
        toCategoryId: String,
        month: Long,
        amount: Long? = null
    ) {
        val toLeftover = calculateCategoryLeftover(toCategoryId, month)
        val fromLeftover = calculateCategoryLeftover(fromCategoryId, month)

        // Only proceed if 'to' is overspent and 'from' has funds
        if (toLeftover >= 0 || fromLeftover <= 0) return

        val overspentAmount = -toLeftover // Make positive
        val amountToCover = amount ?: overspentAmount
        val coverableAmount = minOf(amountToCover, fromLeftover, overspentAmount)

        // Transfer budget from -> to
        val fromBudgetId = "$month-$fromCategoryId"
        val toBudgetId = "$month-$toCategoryId"
        val fromBudget = database.actualDatabaseQueries.getBudgetById(fromBudgetId).executeAsOneOrNull()?.amount ?: 0L
        val toBudget = database.actualDatabaseQueries.getBudgetById(toBudgetId).executeAsOneOrNull()?.amount ?: 0L

        setBudgetAmount(fromCategoryId, month, fromBudget - coverableAmount)
        setBudgetAmount(toCategoryId, month, toBudget + coverableAmount)
    }

    /**
     * Transfer available "To Budget" money to a category.
     *
     * @param amount Amount to transfer
     * @param toCategoryId Target category
     * @param month The month in YYYYMM format
     */
    fun transferAvailable(amount: Long, toCategoryId: String, month: Long) {
        val toBudget = calculateToBudget(month)
        val actualAmount = maxOf(0L, minOf(amount, toBudget))

        val budgetId = "$month-$toCategoryId"
        val currentBudget = database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()?.amount ?: 0L
        setBudgetAmount(toCategoryId, month, currentBudget + actualAmount)
    }
}
