package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Transaction operations for the Actual Budget database.
 *
 * Handles:
 * - Basic transaction CRUD (create, update, delete)
 * - Split transactions (parent/child management)
 * - Transfers (linked transactions between accounts)
 * - Reconciliation operations
 *
 * All mutation methods use the sync engine to create CRDT changes
 * that propagate to the server.
 *
 * @param engine The change engine for creating sync changes
 * @param database The database for reading transaction data
 */
class TransactionOperations(
    private val engine: ChangeEngine,
    private val database: ActualDatabase
) {
    // ========== Basic CRUD ==========

    /**
     * Create a new transaction.
     *
     * @param id The transaction ID (typically a UUID)
     * @param accountId The account this transaction belongs to
     * @param date Date as YYYYMMDD integer
     * @param amount Amount in cents (negative for expenses, positive for income)
     * @param payeeId Optional payee ID
     * @param categoryId Optional category ID
     * @param notes Optional notes
     * @return The transaction ID
     */
    fun createTransaction(
        id: String,
        accountId: String,
        date: Int,
        amount: Long,
        payeeId: String? = null,
        categoryId: String? = null,
        notes: String? = null
    ): String {
        engine.createChange("transactions", id, "acct", accountId)
        engine.createChange("transactions", id, "date", date)
        engine.createChange("transactions", id, "amount", amount)
        // Database column is 'description' which maps to 'payee' in the public API
        if (payeeId != null) engine.createChange("transactions", id, "description", payeeId)
        if (categoryId != null) engine.createChange("transactions", id, "category", categoryId)
        if (notes != null) engine.createChange("transactions", id, "notes", notes)
        engine.createChange("transactions", id, "cleared", 0)
        engine.createChange("transactions", id, "tombstone", 0)
        return id
    }

    /**
     * Update a transaction field.
     *
     * Field names are normalized to match database schema:
     * - "payee" -> "description"
     * - "account" -> "acct"
     *
     * @param id The transaction ID
     * @param field The field name to update
     * @param value The new value
     */
    fun updateTransaction(id: String, field: String, value: Any?) {
        val normalizedField = normalizeTransactionField(field)
        engine.createChange("transactions", id, normalizedField, value)
    }

    /**
     * Delete a transaction (set tombstone).
     *
     * @param id The transaction ID
     */
    fun deleteTransaction(id: String) {
        engine.createChange("transactions", id, "tombstone", 1)
    }

    /**
     * Normalize transaction field names to match database schema.
     */
    private fun normalizeTransactionField(field: String): String {
        return when (field) {
            "payee" -> "description"  // Database stores payee ID in 'description' column
            "account" -> "acct"       // Database uses 'acct' for account ID
            else -> field
        }
    }

    // ========== Reconciliation ==========

    /**
     * Set the cleared status of a transaction.
     *
     * @param transactionId The transaction ID
     * @param cleared Whether the transaction is cleared
     */
    fun setTransactionCleared(transactionId: String, cleared: Boolean) {
        engine.createChange("transactions", transactionId, "cleared", if (cleared) 1 else 0)
    }

    /**
     * Set the reconciled (locked) status of a transaction.
     *
     * @param transactionId The transaction ID
     * @param reconciled Whether the transaction is reconciled
     */
    fun setTransactionReconciled(transactionId: String, reconciled: Boolean) {
        engine.createChange("transactions", transactionId, "reconciled", if (reconciled) 1 else 0)
    }

    /**
     * Mark multiple transactions as reconciled (locked) in one operation.
     * Creates sync changes for each transaction so they propagate to other devices.
     * Also reconciles parent transactions when children are reconciled.
     *
     * @param transactionIds List of transaction IDs to reconcile
     */
    fun reconcileTransactions(transactionIds: List<String>) {
        val parentIdsToReconcile = mutableSetOf<String>()

        for (id in transactionIds) {
            engine.createChange("transactions", id, "reconciled", 1)

            // Check if this is a child transaction with a parent
            val tx = database.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull()
            tx?.parent_id?.let { parentId ->
                parentIdsToReconcile.add(parentId)
            }
        }

        // Also reconcile any parent transactions
        for (parentId in parentIdsToReconcile) {
            engine.createChange("transactions", parentId, "reconciled", 1)
        }
    }

    // ========== Split Transactions ==========

    /**
     * Convert an existing transaction to a split parent.
     * Sets isParent = 1 and clears the category (parent shouldn't have a category).
     *
     * @param transactionId The transaction ID to convert
     */
    fun convertToSplitParent(transactionId: String) {
        engine.createChange("transactions", transactionId, "isParent", 1)
        engine.createChange("transactions", transactionId, "category", null)
    }

    /**
     * Create a child transaction for a split.
     * The child inherits account, date, payee, cleared, reconciled from parent.
     *
     * @param id The new child transaction ID
     * @param parentId The parent transaction ID
     * @param amount The amount for this split (in cents)
     * @param categoryId The category for this split
     * @param accountId The account ID (inherited from parent)
     * @param date The date (inherited from parent)
     * @param payeeId The payee ID (inherited from parent, optional)
     * @param cleared Whether the transaction is cleared (default: true)
     * @param reconciled Whether the transaction is reconciled (default: false)
     * @return The child transaction ID
     */
    fun createChildTransaction(
        id: String,
        parentId: String,
        amount: Long,
        categoryId: String?,
        accountId: String,
        date: Int,
        payeeId: String? = null,
        cleared: Boolean = true,
        reconciled: Boolean = false
    ): String {
        engine.createChange("transactions", id, "acct", accountId)
        engine.createChange("transactions", id, "date", date)
        engine.createChange("transactions", id, "amount", amount)
        engine.createChange("transactions", id, "description", payeeId)
        engine.createChange("transactions", id, "category", categoryId)
        engine.createChange("transactions", id, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", id, "reconciled", if (reconciled) 1 else 0)
        engine.createChange("transactions", id, "tombstone", 0)
        engine.createChange("transactions", id, "isChild", 1)
        engine.createChange("transactions", id, "parent_id", parentId)
        return id
    }

    /**
     * Update a child transaction's amount or category.
     *
     * @param childId The child transaction ID
     * @param amount New amount (optional)
     * @param categoryId New category (optional)
     */
    fun updateChildTransaction(childId: String, amount: Long? = null, categoryId: String? = null) {
        if (amount != null) {
            engine.createChange("transactions", childId, "amount", amount)
        }
        if (categoryId != null) {
            engine.createChange("transactions", childId, "category", categoryId)
        }
    }

    /**
     * Delete a child transaction from a split.
     * If this is the last child, also converts the parent back to a regular transaction.
     *
     * @param childId The child transaction ID to delete
     */
    fun deleteChildTransaction(childId: String) {
        // Get the child to find its parent
        val child = database.actualDatabaseQueries.getTransactionById(childId).executeAsOneOrNull()
        val parentId = child?.parent_id

        // Delete the child (tombstone it)
        engine.createChange("transactions", childId, "tombstone", 1)

        // Check if parent has any remaining children
        if (parentId != null) {
            val remainingChildren = database.actualDatabaseQueries.hasChildTransactions(parentId).executeAsOne()
            if (remainingChildren <= 1) {
                // This was the last child (or will be after tombstone), convert parent back to normal
                engine.createChange("transactions", parentId, "isParent", 0)
            }
        }
    }

    /**
     * Delete a split parent transaction along with all its children.
     * This ensures orphaned children don't affect balance calculations.
     *
     * @param parentId The parent transaction ID to delete
     */
    fun deleteSplitParent(parentId: String) {
        // First, delete all children
        val children = database.actualDatabaseQueries.getChildTransactions(parentId).executeAsList()
        for (child in children) {
            engine.createChange("transactions", child.id, "tombstone", 1)
        }

        // Then delete the parent
        engine.createChange("transactions", parentId, "tombstone", 1)
    }

    // ========== Transfers ==========

    /**
     * Get or create a transfer payee for an account.
     * Transfer payees have an empty name and transfer_acct set to the account ID.
     * They are used to represent transfers to/from that account.
     *
     * @param accountId The account ID this transfer payee represents
     * @return The payee ID for transfers to this account
     */
    fun getOrCreateTransferPayee(accountId: String): String {
        // Check if transfer payee already exists for this account
        val existing = database.actualDatabaseQueries.getTransferPayeeForAccount(accountId).executeAsOneOrNull()
        if (existing != null) {
            return existing.id
        }

        // Create a new transfer payee
        val payeeId = generateUUID()
        engine.createChange("payees", payeeId, "name", "")
        engine.createChange("payees", payeeId, "transfer_acct", accountId)
        engine.createChange("payees", payeeId, "tombstone", 0)

        return payeeId
    }

    /**
     * Check if a payee is a transfer payee.
     *
     * @param payeeId The payee ID to check
     * @return true if this payee has transfer_acct set
     */
    fun isTransferPayee(payeeId: String): Boolean {
        val payee = database.actualDatabaseQueries.getPayeeById(payeeId).executeAsOneOrNull()
        return payee?.transfer_acct != null
    }

    /**
     * Create a transfer between two accounts.
     * Creates two linked transactions: one in each account with opposite amounts.
     *
     * @param fromAccountId Account money is leaving
     * @param toAccountId Account money is going to
     * @param amount Positive amount in cents (money going TO toAccount)
     * @param date Date as YYYYMMDD integer
     * @param notes Optional notes for the transfer
     * @param cleared Whether the transactions should be cleared (default: true)
     * @return Pair of (fromTxId, toTxId)
     */
    fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Long,
        date: Int,
        notes: String? = null,
        cleared: Boolean = true
    ): Pair<String, String> {
        // Get or create transfer payees for both accounts
        val toAccountPayeeId = getOrCreateTransferPayee(toAccountId)
        val fromAccountPayeeId = getOrCreateTransferPayee(fromAccountId)

        // Generate IDs for both transactions
        val fromTxId = generateUUID()
        val toTxId = generateUUID()

        // Create "from" transaction (negative amount - money leaving)
        engine.createChange("transactions", fromTxId, "acct", fromAccountId)
        engine.createChange("transactions", fromTxId, "date", date)
        engine.createChange("transactions", fromTxId, "amount", -amount)  // Negative: money leaving
        engine.createChange("transactions", fromTxId, "description", toAccountPayeeId)  // Payee points to destination
        engine.createChange("transactions", fromTxId, "category", null)  // Transfers typically don't have categories
        if (notes != null) engine.createChange("transactions", fromTxId, "notes", notes)
        engine.createChange("transactions", fromTxId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", fromTxId, "tombstone", 0)
        engine.createChange("transactions", fromTxId, "transferred_id", toTxId)  // Link to paired transaction

        // Create "to" transaction (positive amount - money arriving)
        engine.createChange("transactions", toTxId, "acct", toAccountId)
        engine.createChange("transactions", toTxId, "date", date)
        engine.createChange("transactions", toTxId, "amount", amount)  // Positive: money arriving
        engine.createChange("transactions", toTxId, "description", fromAccountPayeeId)  // Payee points to source
        engine.createChange("transactions", toTxId, "category", null)
        if (notes != null) engine.createChange("transactions", toTxId, "notes", notes)
        engine.createChange("transactions", toTxId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", toTxId, "tombstone", 0)
        engine.createChange("transactions", toTxId, "transferred_id", fromTxId)  // Link to paired transaction

        return Pair(fromTxId, toTxId)
    }

    /**
     * Update the amount of a transfer.
     * Automatically updates both sides with opposite amounts.
     *
     * @param transactionId Either side of the transfer
     * @param newAmount New absolute amount in cents (will be positive on one side, negative on other)
     */
    fun updateTransferAmount(transactionId: String, newAmount: Long) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Determine which side this is based on current amount sign
        val isSourceSide = (tx.amount ?: 0) < 0

        if (isSourceSide) {
            // This is the "from" side (negative), linked is "to" side (positive)
            engine.createChange("transactions", transactionId, "amount", -newAmount)
            engine.createChange("transactions", linkedId, "amount", newAmount)
        } else {
            // This is the "to" side (positive), linked is "from" side (negative)
            engine.createChange("transactions", transactionId, "amount", newAmount)
            engine.createChange("transactions", linkedId, "amount", -newAmount)
        }
    }

    /**
     * Update the date of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param newDate New date as YYYYMMDD integer
     */
    fun updateTransferDate(transactionId: String, newDate: Int) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "date", newDate)
        engine.createChange("transactions", linkedId, "date", newDate)
    }

    /**
     * Update the notes of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param newNotes New notes (can be null to clear)
     */
    fun updateTransferNotes(transactionId: String, newNotes: String?) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "notes", newNotes)
        engine.createChange("transactions", linkedId, "notes", newNotes)
    }

    /**
     * Update the cleared status of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param cleared New cleared status
     */
    fun updateTransferCleared(transactionId: String, cleared: Boolean) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", linkedId, "cleared", if (cleared) 1 else 0)
    }

    /**
     * Delete a transfer (both sides).
     * Sets tombstone=1 on both linked transactions.
     *
     * @param transactionId Either side of the transfer
     */
    fun deleteTransfer(transactionId: String) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id

        // Clear the link on both sides first
        engine.createChange("transactions", transactionId, "transferred_id", null)
        if (linkedId != null) {
            engine.createChange("transactions", linkedId, "transferred_id", null)
        }

        // Delete this transaction
        engine.createChange("transactions", transactionId, "tombstone", 1)

        // Delete the linked transaction
        if (linkedId != null) {
            engine.createChange("transactions", linkedId, "tombstone", 1)
        }
    }

    /**
     * Get the linked (paired) transaction ID for a transfer.
     *
     * @param transactionId The transaction to check
     * @return The ID of the linked transaction, or null if not a transfer
     */
    fun getLinkedTransactionId(transactionId: String): String? {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
        return tx?.transferred_id
    }

    /**
     * Check if a transaction is a transfer.
     *
     * @param transactionId The transaction to check
     * @return true if the transaction has a transferred_id
     */
    fun isTransferTransaction(transactionId: String): Boolean {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
        return tx?.transferred_id != null
    }

    // ========== Utilities ==========

    /**
     * Generate a UUID for new entities.
     */
    private fun generateUUID(): String {
        val chars = "0123456789abcdef"
        val segments = listOf(8, 4, 4, 4, 12)
        return segments.joinToString("-") { length ->
            (1..length).map { chars.random() }.joinToString("")
        }
    }
}
