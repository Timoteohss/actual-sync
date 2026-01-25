package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for PayeeOperations.
 *
 * Tests payee CRUD operations.
 * Uses an in-memory SQLite database for fast, isolated tests.
 */
class PayeeOperationsTest {

    // ========== Payee CRUD Tests ==========

    @Test
    fun testCreatePayee_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        val payeeId = ops.createPayee(
            id = "new-payee-1",
            name = "Grocery Store"
        )

        assertEquals("new-payee-1", payeeId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "payees" && it.row == "new-payee-1" && it.column == "name" && it.value == "Grocery Store" })
        assertTrue(changes.any { it.dataset == "payees" && it.row == "new-payee-1" && it.column == "tombstone" && it.value == 0 })
        // Verify payee_mapping is created
        assertTrue(changes.any { it.dataset == "payee_mapping" && it.row == "new-payee-1" && it.column == "targetId" && it.value == "new-payee-1" })
    }

    @Test
    fun testCreatePayee_multiplePayees() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.createPayee("payee-a", "Store A")
        ops.createPayee("payee-b", "Store B")
        ops.createPayee("payee-c", "Store C")

        val changes = engine.getChanges()
        // 3 changes per payee: name, tombstone, payee_mapping
        assertEquals(9, changes.size)

        assertTrue(changes.any { it.row == "payee-a" && it.column == "name" && it.value == "Store A" })
        assertTrue(changes.any { it.row == "payee-b" && it.column == "name" && it.value == "Store B" })
        assertTrue(changes.any { it.row == "payee-c" && it.column == "name" && it.value == "Store C" })
    }

    @Test
    fun testUpdatePayee_name() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.updatePayee("payee-1", "name", "Updated Store Name")

        val changes = engine.getChanges()
        assertEquals(1, changes.size)
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-1" && it.column == "name" && it.value == "Updated Store Name" })
    }

    @Test
    fun testUpdatePayee_transferAcct() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        // Link payee to a transfer account
        ops.updatePayee("payee-1", "transfer_acct", "acct-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-1" && it.column == "transfer_acct" && it.value == "acct-1" })
    }

    @Test
    fun testUpdatePayee_nullValue() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.updatePayee("payee-1", "transfer_acct", null)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-1" && it.column == "transfer_acct" && it.value == null })
    }

    @Test
    fun testDeletePayee() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.deletePayee("payee-1")

        val changes = engine.getChanges()
        assertEquals(1, changes.size)
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-1" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Payee Merge Tests ==========

    @Test
    fun testMergePayee_basic() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        // Create two payees (id, name, category, tombstone, transfer_acct)
        db.actualDatabaseQueries.insertPayee("payee-source", "Source Payee", null, 0, null)
        db.actualDatabaseQueries.insertPayee("payee-target", "Target Payee", null, 0, null)

        // Create transaction with source payee
        // (id, acct, category, amount, description, notes, date, sort_order, tombstone, cleared, pending, reconciled, isParent, isChild, parent_id, transferred_id)
        db.actualDatabaseQueries.insertTransaction(
            id = "txn-1",
            acct = "acct-1",
            category = "cat-1",
            amount = 1000,
            description = "payee-source",  // payee ID is stored in description
            notes = null,
            date = 20250120,
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

        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.mergePayee("payee-source", "payee-target")

        val changes = engine.getChanges()
        // Should have: source tombstone=1, txn-1 payee updated
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-source" && it.column == "tombstone" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "txn-1" && it.column == "payee" && it.value == "payee-target" })
    }

    @Test
    fun testMergePayee_multipleTransactions() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        // Create payees
        db.actualDatabaseQueries.insertPayee("payee-source", "Source", null, 0, null)
        db.actualDatabaseQueries.insertPayee("payee-target", "Target", null, 0, null)

        // Create multiple transactions with source payee
        for (i in 1..3) {
            db.actualDatabaseQueries.insertTransaction(
                id = "txn-$i",
                acct = "acct-1",
                category = "cat-1",
                amount = 1000L * i,
                description = "payee-source",
                notes = null,
                date = 20250120L + i,
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
        }

        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.mergePayee("payee-source", "payee-target")

        val changes = engine.getChanges()
        // 1 tombstone + 3 transaction updates = 4 changes
        assertEquals(4, changes.size)
        assertTrue(changes.any { it.row == "txn-1" && it.column == "payee" && it.value == "payee-target" })
        assertTrue(changes.any { it.row == "txn-2" && it.column == "payee" && it.value == "payee-target" })
        assertTrue(changes.any { it.row == "txn-3" && it.column == "payee" && it.value == "payee-target" })
    }

    @Test
    fun testMergePayee_noTransactions() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        // Create payees with no transactions
        db.actualDatabaseQueries.insertPayee("payee-source", "Source", null, 0, null)
        db.actualDatabaseQueries.insertPayee("payee-target", "Target", null, 0, null)

        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.mergePayee("payee-source", "payee-target")

        val changes = engine.getChanges()
        // Just the tombstone
        assertEquals(1, changes.size)
        assertTrue(changes.any { it.dataset == "payees" && it.row == "payee-source" && it.column == "tombstone" && it.value == 1 })
    }

    @Test
    fun testMergePayee_skipsTombstonedTransactions() {
        val db = TestDatabaseHelper.createInMemoryDatabase()

        db.actualDatabaseQueries.insertPayee("payee-source", "Source", null, 0, null)
        db.actualDatabaseQueries.insertPayee("payee-target", "Target", null, 0, null)

        // Create active transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "txn-active",
            acct = "acct-1",
            category = "cat-1",
            amount = 1000,
            description = "payee-source",
            notes = null,
            date = 20250120,
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

        // Create tombstoned transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "txn-deleted",
            acct = "acct-1",
            category = "cat-1",
            amount = 2000,
            description = "payee-source",
            notes = null,
            date = 20250121,
            sort_order = null,
            tombstone = 1,
            cleared = 0,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        ops.mergePayee("payee-source", "payee-target")

        val changes = engine.getChanges()
        // 1 tombstone + 1 active transaction update (skip tombstoned)
        assertEquals(2, changes.size)
        assertTrue(changes.any { it.row == "txn-active" && it.column == "payee" })
        assertTrue(changes.none { it.row == "txn-deleted" })
    }

    // ========== Edge Cases ==========

    @Test
    fun testCreatePayee_withSpecialCharacters() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        val payeeId = ops.createPayee(
            id = "payee-special",
            name = "Store & Co. (Main St.)"
        )

        val changes = engine.getChanges()
        assertTrue(changes.any { it.column == "name" && it.value == "Store & Co. (Main St.)" })
    }

    @Test
    fun testCreatePayee_withEmptyName() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = PayeeOperations(engine, db)

        // Empty names are allowed (user can create blank payees)
        val payeeId = ops.createPayee(
            id = "payee-empty",
            name = ""
        )

        assertEquals("payee-empty", payeeId)
        val changes = engine.getChanges()
        assertTrue(changes.any { it.column == "name" && it.value == "" })
    }
}
