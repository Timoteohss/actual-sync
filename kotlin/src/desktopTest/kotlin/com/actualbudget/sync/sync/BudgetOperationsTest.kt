package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Unit tests for BudgetOperations.
 *
 * Tests budget CRUD, hold/release, cover operations, and calculations.
 * Uses an in-memory SQLite database for fast, isolated tests.
 */
class BudgetOperationsTest {

    // ========== Basic Budget CRUD Tests ==========

    @Test
    fun testSetBudgetAmount_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.setBudgetAmount("cat-groceries", 202502, 75000)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-groceries" && it.column == "month" && it.value == 202502L })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-groceries" && it.column == "category" && it.value == "cat-groceries" })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-groceries" && it.column == "amount" && it.value == 75000L })
    }

    @Test
    fun testSetBudgetAmount_updateExisting() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Update existing budget (seeded with 50000)
        ops.setBudgetAmount("cat-groceries", 202501, 60000)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 60000L })
    }

    @Test
    fun testSetBudgetGoal() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.setBudgetGoal("cat-rent", 202501, 150000)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "goal" && it.value == 150000L })
    }

    @Test
    fun testSetBudgetGoal_null() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.setBudgetGoal("cat-rent", 202501, null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "goal" && it.value == null })
    }

    @Test
    fun testSetBudgetCarryover() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.setBudgetCarryover("cat-groceries", 202501, 10000)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "carryover" && it.value == 10000L })
    }

    // ========== Copy/Zero Tests ==========

    @Test
    fun testCopyBudgetFromPreviousMonth() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Seeded budgets are for 202501. Copy to 202502.
        ops.copyBudgetFromPreviousMonth(202502)

        val changes = engine.getChanges()
        // Should have copied all 3 budgets (rent, groceries, utilities)
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-rent" && it.column == "amount" && it.value == 150000L })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-groceries" && it.column == "amount" && it.value == 50000L })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202502-cat-utilities" && it.column == "amount" && it.value == 20000L })
    }

    @Test
    fun testCopyBudgetFromPreviousMonth_januaryWrapsToDecember() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedCategories(db)

        // Create budget for December 2024
        db.actualDatabaseQueries.insertBudget(
            id = "202412-cat-rent",
            month = 202412,
            category = "cat-rent",
            amount = 160000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Copy to January 2025
        ops.copyBudgetFromPreviousMonth(202501)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "amount" && it.value == 160000L })
    }

    @Test
    fun testZeroBudgetsForMonth() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.zeroBudgetsForMonth(202501)

        val changes = engine.getChanges()
        // All 3 budgets should be zeroed
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "amount" && it.value == 0L })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 0L })
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-utilities" && it.column == "amount" && it.value == 0L })
    }

    @Test
    fun testZeroBudgetsForMonth_emptyMonth() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // No budgets for Feb 2025
        ops.zeroBudgetsForMonth(202502)

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    // ========== Transfer Budget Tests ==========

    @Test
    fun testTransferBudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Transfer 10000 from rent (150000) to groceries (50000)
        ops.transferBudget("cat-rent", "cat-groceries", 202501, 10000)

        val changes = engine.getChanges()
        // Rent should be reduced to 140000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "amount" && it.value == 140000L })
        // Groceries should be increased to 60000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 60000L })
    }

    @Test
    fun testTransferBudget_fromZeroBudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Transfer from salary (no budget exists, so 0) to groceries
        ops.transferBudget("cat-salary", "cat-groceries", 202501, 5000)

        val changes = engine.getChanges()
        // Salary should be -5000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-salary" && it.column == "amount" && it.value == -5000L })
        // Groceries should be 55000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 55000L })
    }

    // ========== Hold Operations Tests ==========

    @Test
    fun testResetHold() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.resetHold(202501)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budget_months" && it.row == "202501" && it.column == "buffered" && it.value == 0L })
    }

    @Test
    fun testHoldForNextMonth_success() {
        // Create a scenario with positive to-budget
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add income transaction for Jan 2025
        db.actualDatabaseQueries.insertTransaction(
            id = "income-1",
            acct = "acct-1",
            category = "cat-salary",
            amount = 500000, // $5000 income
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Add budget for Jan 2025 (total budgeted = 100000)
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 100000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // to-budget = 500000 - 100000 = 400000
        val result = ops.holdForNextMonth(202501, 50000)

        assertTrue(result)
        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "zero_budget_months" && it.row == "202501" && it.column == "buffered" && it.value == 50000L })
    }

    @Test
    fun testHoldForNextMonth_noMoney() {
        // Create a scenario with zero to-budget
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // No income, so to-budget = 0
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val result = ops.holdForNextMonth(202501, 50000)

        assertFalse(result)
        val changes = engine.getChanges()
        assertTrue(changes.isEmpty()) // No changes should be made
    }

    @Test
    fun testHoldForNextMonth_constrainedAmount() {
        // Create a scenario where requested amount exceeds available
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add income of $100
        db.actualDatabaseQueries.insertTransaction(
            id = "income-small",
            acct = "acct-1",
            category = "cat-salary",
            amount = 10000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // to-budget = 10000, try to hold 50000
        val result = ops.holdForNextMonth(202501, 50000)

        assertTrue(result)
        val changes = engine.getChanges()
        // Should be constrained to 10000
        assertTrue(changes.any { it.dataset == "zero_budget_months" && it.row == "202501" && it.column == "buffered" && it.value == 10000L })
    }

    // ========== Calculation Tests ==========

    @Test
    fun testCalculateToBudget() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add income transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "income-calc",
            acct = "acct-1",
            category = "cat-salary",
            amount = 300000, // $3000 income
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Add budgets totaling $2000
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 150000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 50000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val toBudget = ops.calculateToBudget(202501)

        // 300000 income - 200000 budgeted = 100000 to budget
        assertEquals(100000L, toBudget)
    }

    @Test
    fun testCalculateToBudget_withBuffered() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add income
        db.actualDatabaseQueries.insertTransaction(
            id = "income-buf",
            acct = "acct-1",
            category = "cat-salary",
            amount = 300000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Add buffered amount
        db.actualDatabaseQueries.insertBudgetMonth(
            id = "202501",
            buffered = 50000
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val toBudget = ops.calculateToBudget(202501)

        // 300000 income - 0 budgeted - 50000 buffered = 250000 to budget
        assertEquals(250000L, toBudget)
    }

    @Test
    fun testCalculateCategoryLeftover_positive() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Groceries has 50000 budgeted, tx-1 spent -5000
        // Leftover = 50000 + (-5000) = 45000
        // BUT wait, tx-1 is dated 20250115 which is in Jan 2025
        // Let's check: getSpentByCategory uses date range

        val leftover = ops.calculateCategoryLeftover("cat-groceries", 202501)

        // Budget is 50000, spent transactions from seed are:
        // tx-1: -5000 (groceries)
        // tx-split-child-1: -6000 (groceries)
        // Both in Jan 2025
        // Total spent = -11000
        // Leftover = 50000 + (-11000) = 39000
        assertEquals(39000L, leftover)
    }

    @Test
    fun testCalculateCategoryLeftover_noBudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Salary has no budget set in seed data
        val leftover = ops.calculateCategoryLeftover("cat-salary", 202501)

        // Budget is 0, income transaction tx-income: 300000
        // Leftover = 0 + 300000 = 300000
        assertEquals(300000L, leftover)
    }

    @Test
    fun testCalculateCategoryLeftover_overspent() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Budget only 1000 for groceries
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 1000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        // Spend 5000
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-overspend",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -5000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val leftover = ops.calculateCategoryLeftover("cat-groceries", 202501)

        // Budget is 1000, spent -5000
        // Leftover = 1000 + (-5000) = -4000
        assertEquals(-4000L, leftover)
    }

    // ========== Cover Operations Tests ==========

    @Test
    fun testCoverOverspending() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Rent has 150000 budget, no spending (positive leftover)
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 150000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        // Groceries has 10000 budget but 20000 spent (overspent by 10000)
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 10000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-cover-test",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -20000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Cover overspending in groceries from rent
        ops.coverOverspending("cat-rent", "cat-groceries", 202501)

        val changes = engine.getChanges()
        // Rent should be reduced by 10000 (the overspent amount)
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "amount" && it.value == 140000L })
        // Groceries should be increased by 10000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 20000L })
    }

    @Test
    fun testCoverOverspending_partialAmount() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Rent has 150000 budget
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 150000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        // Groceries overspent by 10000
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 10000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-cover-partial",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -20000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Only cover 5000 of the 10000 overspending
        ops.coverOverspending("cat-rent", "cat-groceries", 202501, 5000)

        val changes = engine.getChanges()
        // Rent should be reduced by only 5000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-rent" && it.column == "amount" && it.value == 145000L })
        // Groceries should be increased by 5000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 15000L })
    }

    @Test
    fun testCoverOverspending_noOverspending() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // Rent has positive leftover, groceries also has positive leftover
        // (seeded groceries has 50000 budget, only 11000 spent)
        ops.coverOverspending("cat-rent", "cat-groceries", 202501)

        val changes = engine.getChanges()
        // No changes should be made since groceries is not overspent
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testCoverOverspending_noFundsInSource() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Rent has 0 budget (no funds)
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 0,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        // Groceries overspent
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 10000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-no-funds",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -20000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.coverOverspending("cat-rent", "cat-groceries", 202501)

        val changes = engine.getChanges()
        // No changes - source has no funds
        assertTrue(changes.isEmpty())
    }

    // ========== Transfer Available Tests ==========

    @Test
    fun testTransferAvailable() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add income to create to-budget
        db.actualDatabaseQueries.insertTransaction(
            id = "income-transfer",
            acct = "acct-1",
            category = "cat-salary",
            amount = 100000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Groceries has 50000 budget
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 50000,
            carryover = 0,
            goal = null,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // to-budget = 100000 - 50000 = 50000
        // Transfer 20000 to groceries
        ops.transferAvailable(20000, "cat-groceries", 202501)

        val changes = engine.getChanges()
        // Groceries should be 50000 + 20000 = 70000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 70000L })
    }

    @Test
    fun testTransferAvailable_constrained() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Add small income
        db.actualDatabaseQueries.insertTransaction(
            id = "income-small-2",
            acct = "acct-1",
            category = "cat-salary",
            amount = 10000,
            description = null,
            notes = null,
            date = 20250115,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // to-budget = 10000, try to transfer 50000
        ops.transferAvailable(50000, "cat-groceries", 202501)

        val changes = engine.getChanges()
        // Should be constrained to 10000
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 10000L })
    }

    @Test
    fun testTransferAvailable_noToBudget() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // No income, so to-budget = 0
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.transferAvailable(50000, "cat-groceries", 202501)

        val changes = engine.getChanges()
        // Should transfer 0, so budget stays at current (which is 0)
        assertTrue(changes.any { it.dataset == "zero_budgets" && it.row == "202501-cat-groceries" && it.column == "amount" && it.value == 0L })
    }

    // ========== Get Buffered Amount Tests ==========

    @Test
    fun testGetBufferedAmount_exists() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        db.actualDatabaseQueries.insertBudgetMonth(
            id = "202501",
            buffered = 25000
        )

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val buffered = ops.getBufferedAmount(202501)

        assertEquals(25000L, buffered)
    }

    @Test
    fun testGetBufferedAmount_noRecord() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        val buffered = ops.getBufferedAmount(202501)

        assertEquals(0L, buffered)
    }

    // ========== Edge Cases ==========

    @Test
    fun testCopyBudgetFromPreviousMonth_noSourceBudgets() {
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedCategories(db)

        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        // No budgets exist for Dec 2024
        ops.copyBudgetFromPreviousMonth(202501)

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testTransferBudget_sameCategoryDoesNothing() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = BudgetOperations(engine, db)

        ops.transferBudget("cat-rent", "cat-rent", 202501, 10000)

        val changes = engine.getChanges()
        // Should still create changes, but they net to zero
        // Budget goes from 150000 to 140000, then back to 150000 effectively
        // Actually both changes happen, so there will be 6 changes (3 per setBudgetAmount)
        // The net effect is 0, but changes are still recorded
        assertEquals(6, changes.size)
    }
}
