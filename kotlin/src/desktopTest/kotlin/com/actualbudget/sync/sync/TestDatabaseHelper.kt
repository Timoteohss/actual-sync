package com.actualbudget.sync.sync

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.actualbudget.sync.db.ActualDatabase

/**
 * Helper for creating in-memory test databases.
 */
object TestDatabaseHelper {

    /**
     * Create an in-memory database with schema applied.
     */
    fun createInMemoryDatabase(): ActualDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        ActualDatabase.Schema.create(driver)
        return ActualDatabase(driver)
    }

    /**
     * Seed the database with test data for accounts.
     */
    fun seedAccounts(db: ActualDatabase) {
        db.actualDatabaseQueries.insertAccount(
            id = "acct-1",
            name = "Checking",
            offbudget = 0,
            closed = 0,
            sort_order = 1000.0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertAccount(
            id = "acct-2",
            name = "Savings",
            offbudget = 0,
            closed = 0,
            sort_order = 2000.0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertAccount(
            id = "acct-3",
            name = "Credit Card",
            offbudget = 1,
            closed = 0,
            sort_order = 3000.0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertAccount(
            id = "acct-closed",
            name = "Old Account",
            offbudget = 0,
            closed = 1,
            sort_order = 4000.0,
            tombstone = 0
        )
    }

    /**
     * Seed the database with test data for payees.
     */
    fun seedPayees(db: ActualDatabase) {
        db.actualDatabaseQueries.insertPayee(
            id = "payee-1",
            name = "Grocery Store",
            category = null,
            tombstone = 0,
            transfer_acct = null
        )
        db.actualDatabaseQueries.insertPayee(
            id = "payee-2",
            name = "Gas Station",
            category = null,
            tombstone = 0,
            transfer_acct = null
        )
        // Transfer payee for Savings account
        db.actualDatabaseQueries.insertPayee(
            id = "payee-transfer-savings",
            name = "Transfer: Savings",
            category = null,
            tombstone = 0,
            transfer_acct = "acct-2"
        )
    }

    /**
     * Seed the database with test data for category groups and categories.
     */
    fun seedCategories(db: ActualDatabase) {
        // Category groups
        db.actualDatabaseQueries.insertCategoryGroup(
            id = "group-1",
            name = "Bills",
            is_income = 0,
            sort_order = 1000.0,
            hidden = 0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertCategoryGroup(
            id = "group-2",
            name = "Food",
            is_income = 0,
            sort_order = 2000.0,
            hidden = 0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertCategoryGroup(
            id = "group-income",
            name = "Income",
            is_income = 1,
            sort_order = 3000.0,
            hidden = 0,
            tombstone = 0
        )

        // Categories
        db.actualDatabaseQueries.insertCategory(
            id = "cat-rent",
            name = "Rent",
            cat_group = "group-1",
            is_income = 0,
            sort_order = 1000.0,
            hidden = 0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertCategory(
            id = "cat-utilities",
            name = "Utilities",
            cat_group = "group-1",
            is_income = 0,
            sort_order = 2000.0,
            hidden = 0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertCategory(
            id = "cat-groceries",
            name = "Groceries",
            cat_group = "group-2",
            is_income = 0,
            sort_order = 1000.0,
            hidden = 0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertCategory(
            id = "cat-salary",
            name = "Salary",
            cat_group = "group-income",
            is_income = 1,
            sort_order = 1000.0,
            hidden = 0,
            tombstone = 0
        )
    }

    /**
     * Seed the database with test transactions.
     */
    fun seedTransactions(db: ActualDatabase) {
        // Regular transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-1",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -5000, // $50.00 expense
            description = "payee-1",
            notes = "Weekly groceries",
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

        // Another transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-2",
            acct = "acct-1",
            category = "cat-utilities",
            amount = -2500, // $25.00 expense
            description = "payee-2",
            notes = "Gas",
            date = 20250116,
            sort_order = null,
            tombstone = 0,
            cleared = 0,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Income transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-income",
            acct = "acct-1",
            category = "cat-salary",
            amount = 300000, // $3000.00 income
            description = null,
            notes = "January salary",
            date = 20250101,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 1,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Split parent transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-split-parent",
            acct = "acct-1",
            category = null, // Parent has no category
            amount = -10000, // $100.00 total
            description = "payee-1",
            notes = "Big shopping trip",
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 1,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        // Split child transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "tx-split-child-1",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -6000, // $60.00
            description = "payee-1",
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 1,
            parent_id = "tx-split-parent",
            transferred_id = null
        )

        db.actualDatabaseQueries.insertTransaction(
            id = "tx-split-child-2",
            acct = "acct-1",
            category = "cat-utilities",
            amount = -4000, // $40.00
            description = "payee-1",
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 1,
            parent_id = "tx-split-parent",
            transferred_id = null
        )
    }

    /**
     * Seed the database with budget data.
     */
    fun seedBudgets(db: ActualDatabase) {
        // January 2025 budgets
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-rent",
            month = 202501,
            category = "cat-rent",
            amount = 150000, // $1500.00
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-groceries",
            month = 202501,
            category = "cat-groceries",
            amount = 50000, // $500.00
            carryover = 0,
            goal = null,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertBudget(
            id = "202501-cat-utilities",
            month = 202501,
            category = "cat-utilities",
            amount = 20000, // $200.00
            carryover = 0,
            goal = null,
            tombstone = 0
        )
    }

    /**
     * Create a fully seeded test database.
     */
    fun createSeededDatabase(): ActualDatabase {
        val db = createInMemoryDatabase()
        seedAccounts(db)
        seedPayees(db)
        seedCategories(db)
        seedTransactions(db)
        seedBudgets(db)
        return db
    }
}
