package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for AccountOperations.
 *
 * Tests account CRUD and reordering.
 * Uses an in-memory SQLite database for fast, isolated tests.
 */
class AccountOperationsTest {

    // ========== Account CRUD Tests ==========

    @Test
    fun testCreateAccount_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        val acctId = ops.createAccount(
            id = "new-acct-1",
            name = "New Checking"
        )

        assertEquals("new-acct-1", acctId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-1" && it.column == "name" && it.value == "New Checking" })
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-1" && it.column == "offbudget" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-1" && it.column == "closed" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-1" && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateAccount_offbudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        val acctId = ops.createAccount(
            id = "new-acct-2",
            name = "Investment Account",
            offbudget = true
        )

        assertEquals("new-acct-2", acctId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-2" && it.column == "name" && it.value == "Investment Account" })
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-2" && it.column == "offbudget" && it.value == 1 })
    }

    @Test
    fun testCreateAccount_withSortOrder() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        val acctId = ops.createAccount(
            id = "new-acct-3",
            name = "Savings",
            sortOrder = 5000.0
        )

        assertEquals("new-acct-3", acctId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "new-acct-3" && it.column == "sort_order" && it.value == 5000.0 })
    }

    @Test
    fun testUpdateAccount_name() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.updateAccount("acct-1", "name", "Primary Checking")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "name" && it.value == "Primary Checking" })
    }

    @Test
    fun testUpdateAccount_offbudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.updateAccount("acct-1", "offbudget", 1)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "offbudget" && it.value == 1 })
    }

    @Test
    fun testDeleteAccount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.deleteAccount("acct-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Close/Reopen Tests ==========

    @Test
    fun testCloseAccount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.closeAccount("acct-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "closed" && it.value == 1 })
    }

    @Test
    fun testReopenAccount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.reopenAccount("acct-closed")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-closed" && it.column == "closed" && it.value == 0 })
    }

    // ========== Move Account Tests ==========

    @Test
    fun testMoveAccount_onBudget_toBeginning() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Move Savings (acct-2, sort_order 2000) before Checking (acct-1, sort_order 1000)
        ops.moveAccount("acct-2", "acct-1")

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "acct-2" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be less than 1000 (half of 1000 = 500)
        assertTrue(newSortOrder < 1000.0, "Expected sort_order < 1000, got $newSortOrder")
    }

    @Test
    fun testMoveAccount_onBudget_toEnd() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Move Checking (acct-1) to end of on-budget accounts
        ops.moveAccount("acct-1", null)

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "acct-1" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be greater than Savings' 2000 (2000 + 16384 = 18384)
        assertTrue(newSortOrder > 2000.0, "Expected sort_order > 2000, got $newSortOrder")
    }

    @Test
    fun testMoveAccount_offBudget() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Credit Card is the only off-budget account, move to end
        ops.moveAccount("acct-3", null)

        val changes = engine.getChanges()
        // Should still create a change
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-3" && it.column == "sort_order" })
    }

    @Test
    fun testMoveAccount_closed() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Old Account is the only closed account, move to end
        ops.moveAccount("acct-closed", null)

        val changes = engine.getChanges()
        // Should still create a change
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-closed" && it.column == "sort_order" })
    }

    @Test
    fun testMoveAccount_betweenAccounts() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        // Create 3 on-budget accounts
        db.actualDatabaseQueries.insertAccount(
            id = "acct-a",
            name = "Account A",
            offbudget = 0,
            closed = 0,
            sort_order = 1000.0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertAccount(
            id = "acct-b",
            name = "Account B",
            offbudget = 0,
            closed = 0,
            sort_order = 2000.0,
            tombstone = 0
        )
        db.actualDatabaseQueries.insertAccount(
            id = "acct-c",
            name = "Account C",
            offbudget = 0,
            closed = 0,
            sort_order = 3000.0,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Move C between A and B
        ops.moveAccount("acct-c", "acct-b")

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "acct-c" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Should be between 1000 and 2000 (midpoint = 1500)
        assertTrue(newSortOrder > 1000.0 && newSortOrder < 2000.0, "Expected 1000 < sort_order < 2000, got $newSortOrder")
    }

    @Test
    fun testMoveAccount_nonExistent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Try to move non-existent account - should do nothing
        ops.moveAccount("non-existent", null)

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testMoveAccount_targetNotFound() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Move with non-existent target - should append at end
        ops.moveAccount("acct-1", "non-existent-account")

        val changes = engine.getChanges()
        // Should still create a change (appends at end)
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "sort_order" })
    }

    // ========== Edge Cases ==========

    @Test
    fun testCreateAccount_noSortOrder() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.createAccount(
            id = "acct-no-sort",
            name = "No Sort"
        )

        val changes = engine.getChanges()
        // Should NOT have a sort_order change (it's null/omitted)
        val sortOrderChange = changes.find { it.row == "acct-no-sort" && it.column == "sort_order" }
        assertEquals(null, sortOrderChange)
    }

    @Test
    fun testUpdateAccount_nullValue() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        ops.updateAccount("acct-1", "bank_id", null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "accounts" && it.row == "acct-1" && it.column == "bank_id" && it.value == null })
    }

    @Test
    fun testMoveAccount_singleAccountInCategory() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        // Create single off-budget account
        db.actualDatabaseQueries.insertAccount(
            id = "single-offbudget",
            name = "Single Off-Budget",
            offbudget = 1,
            closed = 0,
            sort_order = 1000.0,
            tombstone = 0
        )

        val engine = TestSyncEngine(db)
        val ops = AccountOperations(engine, db)

        // Move single account - should still work
        ops.moveAccount("single-offbudget", null)

        val changes = engine.getChanges()
        val sortOrderChange = changes.find { it.row == "single-offbudget" && it.column == "sort_order" }
        val newSortOrder = sortOrderChange?.value as? Double ?: 0.0
        // Empty list + append = SORT_INCREMENT
        assertEquals(16384.0, newSortOrder)
    }
}
