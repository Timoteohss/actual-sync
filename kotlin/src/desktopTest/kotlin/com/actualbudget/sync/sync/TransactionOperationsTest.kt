package com.actualbudget.sync.sync

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Unit tests for TransactionOperations.
 *
 * Tests transaction CRUD, splits, transfers, and reconciliation.
 * Uses an in-memory SQLite database for fast, isolated tests.
 */
class TransactionOperationsTest {

    // ========== Basic CRUD Tests ==========

    @Test
    fun testCreateTransaction_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val txId = ops.createTransaction(
            id = "new-tx-1",
            accountId = "acct-1",
            date = 20250120,
            amount = -5000
        )

        assertEquals("new-tx-1", txId)

        // Verify changes were created
        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-1" && it.column == "acct" && it.value == "acct-1" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-1" && it.column == "date" && it.value == 20250120 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-1" && it.column == "amount" && it.value == -5000L })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-1" && it.column == "cleared" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-1" && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateTransaction_withAllFields() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val txId = ops.createTransaction(
            id = "new-tx-2",
            accountId = "acct-1",
            date = 20250120,
            amount = -10000,
            payeeId = "payee-1",
            categoryId = "cat-groceries",
            notes = "Test transaction"
        )

        assertEquals("new-tx-2", txId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-2" && it.column == "description" && it.value == "payee-1" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-2" && it.column == "category" && it.value == "cat-groceries" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-tx-2" && it.column == "notes" && it.value == "Test transaction" })
    }

    @Test
    fun testUpdateTransaction_amount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.updateTransaction("tx-1", "amount", -7500L)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "amount" && it.value == -7500L })
    }

    @Test
    fun testUpdateTransaction_normalizedFields() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // "payee" should be normalized to "description"
        ops.updateTransaction("tx-1", "payee", "payee-2")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "description" && it.value == "payee-2" })
    }

    @Test
    fun testUpdateTransaction_accountNormalized() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // "account" should be normalized to "acct"
        ops.updateTransaction("tx-1", "account", "acct-2")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "acct" && it.value == "acct-2" })
    }

    @Test
    fun testDeleteTransaction() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.deleteTransaction("tx-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Reconciliation Tests ==========

    @Test
    fun testReconcileTransactions_single() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.reconcileTransactions(listOf("tx-1"))

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "reconciled" && it.value == 1 })
    }

    @Test
    fun testReconcileTransactions_multiple() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.reconcileTransactions(listOf("tx-1", "tx-2"))

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "reconciled" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-2" && it.column == "reconciled" && it.value == 1 })
    }

    @Test
    fun testReconcileTransactions_childAlsoReconcilesParent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Reconcile a child transaction - should also reconcile parent
        ops.reconcileTransactions(listOf("tx-split-child-1"))

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "reconciled" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-parent" && it.column == "reconciled" && it.value == 1 })
    }

    // ========== Split Transaction Tests ==========

    @Test
    fun testConvertToSplitParent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.convertToSplitParent("tx-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "isParent" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-1" && it.column == "category" && it.value == null })
    }

    @Test
    fun testCreateChildTransaction_basic() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val childId = ops.createChildTransaction(
            id = "new-child-1",
            parentId = "tx-split-parent",
            amount = -2000,
            categoryId = "cat-groceries",
            accountId = "acct-1",
            date = 20250120
        )

        assertEquals("new-child-1", childId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "acct" && it.value == "acct-1" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "date" && it.value == 20250120 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "amount" && it.value == -2000L })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "category" && it.value == "cat-groceries" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "isChild" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "parent_id" && it.value == "tx-split-parent" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "cleared" && it.value == 1 }) // default
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-1" && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateChildTransaction_withPayeeAndFlags() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val childId = ops.createChildTransaction(
            id = "new-child-2",
            parentId = "tx-split-parent",
            amount = -3000,
            categoryId = "cat-utilities",
            accountId = "acct-1",
            date = 20250120,
            payeeId = "payee-1",
            cleared = false,
            reconciled = true
        )

        assertEquals("new-child-2", childId)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-2" && it.column == "description" && it.value == "payee-1" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-2" && it.column == "cleared" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "new-child-2" && it.column == "reconciled" && it.value == 1 })
    }

    @Test
    fun testUpdateChildTransaction_amount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.updateChildTransaction("tx-split-child-1", amount = -8000L)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "amount" && it.value == -8000L })
    }

    @Test
    fun testUpdateChildTransaction_category() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.updateChildTransaction("tx-split-child-1", categoryId = "cat-rent")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "category" && it.value == "cat-rent" })
    }

    @Test
    fun testUpdateChildTransaction_both() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.updateChildTransaction("tx-split-child-1", amount = -5000L, categoryId = "cat-rent")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "amount" && it.value == -5000L })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "category" && it.value == "cat-rent" })
    }

    @Test
    fun testDeleteChildTransaction() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.deleteChildTransaction("tx-split-child-1")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "tombstone" && it.value == 1 })
    }

    @Test
    fun testDeleteChildTransaction_lastChildConvertsParentBack() {
        // Use empty database and create a split with only one child
        val db = TestDatabaseHelper.createInMemoryDatabase()
        TestDatabaseHelper.seedAccounts(db)
        TestDatabaseHelper.seedCategories(db)

        // Create parent transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "single-parent",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
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

        // Create single child
        db.actualDatabaseQueries.insertTransaction(
            id = "single-child",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 1,
            parent_id = "single-parent",
            transferred_id = null
        )

        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Delete the only child - should convert parent back to normal
        ops.deleteChildTransaction("single-child")

        val changes = engine.getChanges()
        // Child should be tombstoned
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "single-child" && it.column == "tombstone" && it.value == 1 })
        // Parent should have isParent set to 0 after last child deleted
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "single-parent" && it.column == "isParent" && it.value == 0 })
    }

    @Test
    fun testDeleteSplitParent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        ops.deleteSplitParent("tx-split-parent")

        val changes = engine.getChanges()
        // Parent should be tombstoned
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-parent" && it.column == "tombstone" && it.value == 1 })
        // Both children should be tombstoned
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-1" && it.column == "tombstone" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "tx-split-child-2" && it.column == "tombstone" && it.value == 1 })
    }

    // ========== Transfer Tests ==========

    @Test
    fun testGetOrCreateTransferPayee_existing() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // acct-2 has an existing transfer payee (payee-transfer-savings)
        val payeeId = ops.getOrCreateTransferPayee("acct-2")

        assertEquals("payee-transfer-savings", payeeId)
        // No new payee should be created
        val changes = engine.getChanges()
        assertFalse(changes.any { it.dataset == "payees" })
    }

    @Test
    fun testGetOrCreateTransferPayee_new() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // acct-1 does not have a transfer payee
        val payeeId = ops.getOrCreateTransferPayee("acct-1")

        assertNotNull(payeeId)
        assertTrue(payeeId.isNotEmpty())

        // New payee should be created with transfer_acct
        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "payees" && it.row == payeeId && it.column == "transfer_acct" && it.value == "acct-1" })
        assertTrue(changes.any { it.dataset == "payees" && it.row == payeeId && it.column == "tombstone" && it.value == 0 })
    }

    @Test
    fun testCreateTransfer() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val (fromTxId, toTxId) = ops.createTransfer(
            fromAccountId = "acct-1",
            toAccountId = "acct-2",
            amount = 10000,
            date = 20250120
        )

        assertNotNull(fromTxId)
        assertNotNull(toTxId)
        assertTrue(fromTxId != toTxId)

        val changes = engine.getChanges()

        // From transaction should have negative amount
        assertTrue(changes.any { it.dataset == "transactions" && it.row == fromTxId && it.column == "acct" && it.value == "acct-1" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == fromTxId && it.column == "amount" && it.value == -10000L })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == fromTxId && it.column == "transferred_id" && it.value == toTxId })

        // To transaction should have positive amount
        assertTrue(changes.any { it.dataset == "transactions" && it.row == toTxId && it.column == "acct" && it.value == "acct-2" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == toTxId && it.column == "amount" && it.value == 10000L })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == toTxId && it.column == "transferred_id" && it.value == fromTxId })
    }

    @Test
    fun testCreateTransfer_withNotesAndCleared() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val (fromTxId, toTxId) = ops.createTransfer(
            fromAccountId = "acct-1",
            toAccountId = "acct-2",
            amount = 5000,
            date = 20250120,
            notes = "Monthly transfer",
            cleared = false
        )

        val changes = engine.getChanges()

        // Both should have same notes
        assertTrue(changes.any { it.dataset == "transactions" && it.row == fromTxId && it.column == "notes" && it.value == "Monthly transfer" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == toTxId && it.column == "notes" && it.value == "Monthly transfer" })

        // Both should be uncleared
        assertTrue(changes.any { it.dataset == "transactions" && it.row == fromTxId && it.column == "cleared" && it.value == 0 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == toTxId && it.column == "cleared" && it.value == 0 })
    }

    @Test
    fun testUpdateTransferAmount() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // First create a transfer
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-to"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-from"
        )

        ops.updateTransferAmount("transfer-from", 7500)

        val changes = engine.getChanges()
        // From side should be negative new amount
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from" && it.column == "amount" && it.value == -7500L })
        // To side should be positive new amount
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to" && it.column == "amount" && it.value == 7500L })
    }

    @Test
    fun testUpdateTransferDate() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create transfer transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from-2",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-to-2"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to-2",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-from-2"
        )

        ops.updateTransferDate("transfer-from-2", 20250125)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from-2" && it.column == "date" && it.value == 20250125 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to-2" && it.column == "date" && it.value == 20250125 })
    }

    @Test
    fun testUpdateTransferNotes() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create transfer transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from-3",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-to-3"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to-3",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-from-3"
        )

        ops.updateTransferNotes("transfer-from-3", "Updated notes")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from-3" && it.column == "notes" && it.value == "Updated notes" })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to-3" && it.column == "notes" && it.value == "Updated notes" })
    }

    @Test
    fun testUpdateTransferCleared() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create transfer transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from-4",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
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
            transferred_id = "transfer-to-4"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to-4",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
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
            transferred_id = "transfer-from-4"
        )

        ops.updateTransferCleared("transfer-from-4", true)

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from-4" && it.column == "cleared" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to-4" && it.column == "cleared" && it.value == 1 })
    }

    @Test
    fun testDeleteTransfer() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create transfer transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from-5",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-to-5"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to-5",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-from-5"
        )

        ops.deleteTransfer("transfer-from-5")

        val changes = engine.getChanges()
        // Both should have transferred_id cleared
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from-5" && it.column == "transferred_id" && it.value == null })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to-5" && it.column == "transferred_id" && it.value == null })
        // Both should be tombstoned
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-from-5" && it.column == "tombstone" && it.value == 1 })
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "transfer-to-5" && it.column == "tombstone" && it.value == 1 })
    }

    @Test
    fun testGetLinkedTransactionId_existingTransfer() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create transfer transactions
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-from-6",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-to-6"
        )
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-to-6",
            acct = "acct-2",
            category = null,
            amount = 5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "transfer-from-6"
        )

        val linkedId = ops.getLinkedTransactionId("transfer-from-6")

        assertEquals("transfer-to-6", linkedId)
    }

    @Test
    fun testGetLinkedTransactionId_notTransfer() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val linkedId = ops.getLinkedTransactionId("tx-1")

        assertNull(linkedId)
    }

    @Test
    fun testIsTransferTransaction_true() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create a transfer transaction
        db.actualDatabaseQueries.insertTransaction(
            id = "transfer-check",
            acct = "acct-1",
            category = null,
            amount = -5000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 0,
            parent_id = null,
            transferred_id = "some-other-tx"
        )

        val isTransfer = ops.isTransferTransaction("transfer-check")

        assertTrue(isTransfer)
    }

    @Test
    fun testIsTransferTransaction_false() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val isTransfer = ops.isTransferTransaction("tx-1")

        assertFalse(isTransfer)
    }

    @Test
    fun testIsTransferTransaction_nonExistent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        val isTransfer = ops.isTransferTransaction("non-existent")

        assertFalse(isTransfer)
    }

    // ========== Edge Case Tests ==========

    @Test
    fun testUpdateTransferAmount_nonTransfer() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Should not throw, just do nothing
        ops.updateTransferAmount("tx-1", 10000)

        // No changes should be made (tx-1 is not a transfer)
        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testUpdateTransferAmount_nonExistent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Should not throw, just do nothing
        ops.updateTransferAmount("non-existent", 10000)

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testDeleteTransfer_nonExistent() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Should not throw, just do nothing
        ops.deleteTransfer("non-existent")

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testReconcileTransactions_emptyList() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Should not throw
        ops.reconcileTransactions(emptyList())

        val changes = engine.getChanges()
        assertTrue(changes.isEmpty())
    }

    @Test
    fun testDeleteChildTransaction_orphan() {
        val db = TestDatabaseHelper.createSeededDatabase()
        val engine = TestSyncEngine(db)
        val ops = TransactionOperations(engine, db)

        // Create an orphan child (no parent_id)
        db.actualDatabaseQueries.insertTransaction(
            id = "orphan-child",
            acct = "acct-1",
            category = "cat-groceries",
            amount = -1000,
            description = null,
            notes = null,
            date = 20250120,
            sort_order = null,
            tombstone = 0,
            cleared = 1,
            pending = 0,
            reconciled = 0,
            isParent = 0,
            isChild = 1,
            parent_id = null,
            transferred_id = null
        )

        // Should not throw
        ops.deleteChildTransaction("orphan-child")

        val changes = engine.getChanges()
        assertTrue(changes.any { it.dataset == "transactions" && it.row == "orphan-child" && it.column == "tombstone" && it.value == 1 })
    }
}
