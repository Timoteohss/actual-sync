package com.actualbudget.sync.db

import com.actualbudget.sync.proto.Message
import com.actualbudget.sync.proto.MessageEnvelope

/**
 * Repository for syncing CRDT messages to the local database.
 */
class SyncRepository(private val db: ActualDatabase) {

    /**
     * Apply a list of message envelopes to the database.
     * Messages are stored in the CRDT log and applied to their respective tables.
     */
    fun applyMessages(envelopes: List<MessageEnvelope>) {
        envelopes.forEach { envelope ->
            if (!envelope.isEncrypted) {
                try {
                    val message = envelope.decodeMessage()
                    applyMessage(envelope.timestamp, message)
                } catch (e: Exception) {
                    // Log error but continue processing
                    println("Failed to apply message ${envelope.timestamp}: ${e.message}")
                }
            }
        }
    }

    /**
     * Apply a single CRDT message.
     */
    private fun applyMessage(timestamp: String, message: Message) {
        // Store in CRDT log (value stored as BLOB)
        db.actualDatabaseQueries.insertMessage(
            timestamp = timestamp,
            dataset = message.dataset,
            row = message.row,
            column = message.column,
            value_ = message.value.encodeToByteArray()
        )

        // Parse the value
        val parsedValue = parseValue(message.value)

        // Ensure row exists, then update the column
        when (message.dataset) {
            "accounts" -> {
                ensureAccountExists(message.row)
                applyAccountColumn(message.row, message.column, parsedValue)
            }
            "payees" -> {
                ensurePayeeExists(message.row)
                applyPayeeColumn(message.row, message.column, parsedValue)
            }
            "payee_mapping" -> {
                ensurePayeeMappingExists(message.row)
                applyPayeeMappingColumn(message.row, message.column, parsedValue)
            }
            "categories" -> {
                ensureCategoryExists(message.row)
                applyCategoryColumn(message.row, message.column, parsedValue)
            }
            "category_groups" -> {
                ensureCategoryGroupExists(message.row)
                applyCategoryGroupColumn(message.row, message.column, parsedValue)
            }
            "transactions" -> {
                ensureTransactionExists(message.row)
                applyTransactionColumn(message.row, message.column, parsedValue)
            }
            "zero_budgets" -> {
                ensureBudgetExists(message.row)
                applyBudgetColumn(message.row, message.column, parsedValue)
            }
            "zero_budget_months" -> {
                // Buffer amounts per month - not critical for basic budget display
                // Can be implemented later for carryover feature
            }
            "schedules" -> {
                // Handle schedules
            }
            "rules" -> {
                // Handle rules
            }
            "notes" -> {
                // Handle notes
            }
            else -> {
                // Unknown dataset, skip
            }
        }
    }

    /**
     * Parse Actual's typed value format.
     * Values are prefixed with type: S: for string, N: for number, 0: for null
     */
    private fun parseValue(value: String): Any? {
        return when {
            value.startsWith("S:") -> value.substring(2)
            value.startsWith("N:") -> value.substring(2).toLongOrNull() ?: 0L
            value.startsWith("0:") -> null
            value == "null" -> null
            else -> value // Fallback to raw value
        }
    }

    // ========== Account Operations ==========

    private fun ensureAccountExists(id: String) {
        val existing = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertAccount(
                id = id,
                name = "",
                offbudget = 0,
                closed = 0,
                sort_order = null,
                tombstone = 0
            )
        }
    }

    private fun applyAccountColumn(id: String, column: String, value: Any?) {
        when (column) {
            "name" -> {
                val current = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
                if (current != null) {
                    db.actualDatabaseQueries.insertAccount(
                        id = id,
                        name = value as? String ?: "",
                        offbudget = current.offbudget,
                        closed = current.closed,
                        sort_order = current.sort_order,
                        tombstone = current.tombstone
                    )
                }
            }
            "offbudget" -> {
                val current = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
                if (current != null) {
                    db.actualDatabaseQueries.insertAccount(
                        id = id,
                        name = current.name,
                        offbudget = (value as? Long) ?: 0L,
                        closed = current.closed,
                        sort_order = current.sort_order,
                        tombstone = current.tombstone
                    )
                }
            }
            "closed" -> {
                val current = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
                if (current != null) {
                    db.actualDatabaseQueries.insertAccount(
                        id = id,
                        name = current.name,
                        offbudget = current.offbudget,
                        closed = (value as? Long) ?: 0L,
                        sort_order = current.sort_order,
                        tombstone = current.tombstone
                    )
                }
            }
            "sort_order" -> {
                val current = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
                if (current != null) {
                    db.actualDatabaseQueries.insertAccount(
                        id = id,
                        name = current.name,
                        offbudget = current.offbudget,
                        closed = current.closed,
                        sort_order = (value as? Long)?.toDouble(),
                        tombstone = current.tombstone
                    )
                }
            }
            "tombstone" -> {
                val current = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
                if (current != null) {
                    db.actualDatabaseQueries.insertAccount(
                        id = id,
                        name = current.name,
                        offbudget = current.offbudget,
                        closed = current.closed,
                        sort_order = current.sort_order,
                        tombstone = (value as? Long) ?: 0L
                    )
                }
            }
        }
    }

    // ========== Payee Operations ==========

    private fun ensurePayeeExists(id: String) {
        val existing = db.actualDatabaseQueries.getPayeeById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertPayee(
                id = id,
                name = "",
                category = null,
                tombstone = 0,
                transfer_acct = null
            )
        }
    }

    private fun applyPayeeColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getPayeeById(id).executeAsOneOrNull() ?: return
        when (column) {
            "name" -> db.actualDatabaseQueries.insertPayee(id, value as? String ?: "", current.category, current.tombstone, current.transfer_acct)
            "category" -> db.actualDatabaseQueries.insertPayee(id, current.name, value as? String, current.tombstone, current.transfer_acct)
            "tombstone" -> db.actualDatabaseQueries.insertPayee(id, current.name, current.category, (value as? Long) ?: 0L, current.transfer_acct)
            "transfer_acct" -> db.actualDatabaseQueries.insertPayee(id, current.name, current.category, current.tombstone, value as? String)
        }
    }

    // ========== Payee Mapping Operations ==========
    // NOTE: payee_mapping table has only 'id' and 'targetId' columns (no tombstone)

    private fun ensurePayeeMappingExists(id: String) {
        val existing = db.actualDatabaseQueries.getPayeeMappingById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertPayeeMapping(
                id = id,
                targetId = id  // Default: points to itself
            )
        }
    }

    private fun applyPayeeMappingColumn(id: String, column: String, value: Any?) {
        when (column) {
            "targetId" -> db.actualDatabaseQueries.insertPayeeMapping(id, value as? String ?: id)
        }
    }

    // ========== Category Operations ==========

    private fun ensureCategoryExists(id: String) {
        val existing = db.actualDatabaseQueries.getCategoryById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertCategory(
                id = id,
                name = "",
                cat_group = null,
                is_income = 0,
                sort_order = null,
                hidden = 0,
                tombstone = 0
            )
        }
    }

    private fun applyCategoryColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getCategoryById(id).executeAsOneOrNull() ?: return
        when (column) {
            "name" -> db.actualDatabaseQueries.insertCategory(id, value as? String ?: "", current.cat_group, current.is_income, current.sort_order, current.hidden, current.tombstone)
            "cat_group" -> db.actualDatabaseQueries.insertCategory(id, current.name, value as? String, current.is_income, current.sort_order, current.hidden, current.tombstone)
            "is_income" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, (value as? Long) ?: 0L, current.sort_order, current.hidden, current.tombstone)
            "sort_order" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, (value as? Long)?.toDouble(), current.hidden, current.tombstone)
            "hidden" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, current.sort_order, (value as? Long) ?: 0L, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, current.sort_order, current.hidden, (value as? Long) ?: 0L)
        }
    }

    // ========== Category Group Operations ==========

    private fun ensureCategoryGroupExists(id: String) {
        val existing = db.actualDatabaseQueries.getCategoryGroupById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertCategoryGroup(
                id = id,
                name = "",
                is_income = 0,
                sort_order = null,
                hidden = 0,
                tombstone = 0
            )
        }
    }

    private fun applyCategoryGroupColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getCategoryGroupById(id).executeAsOneOrNull() ?: return
        when (column) {
            "name" -> db.actualDatabaseQueries.insertCategoryGroup(id, value as? String ?: "", current.is_income, current.sort_order, current.hidden, current.tombstone)
            "is_income" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, (value as? Long) ?: 0L, current.sort_order, current.hidden, current.tombstone)
            "sort_order" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, (value as? Long)?.toDouble(), current.hidden, current.tombstone)
            "hidden" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, current.sort_order, (value as? Long) ?: 0L, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, current.sort_order, current.hidden, (value as? Long) ?: 0L)
        }
    }

    // ========== Transaction Operations ==========

    private fun ensureTransactionExists(id: String) {
        val existing = db.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertTransaction(
                id = id,
                acct = null,
                category = null,
                amount = 0,
                description = null,
                notes = null,
                date = null,
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
        }
    }

    private fun applyTransactionColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull() ?: return

        // Build updated transaction (simplified schema)
        // Note: 'description' column stores the payee ID
        val updated = when (column) {
            "acct" -> current.copy(acct = value as? String)
            "account" -> current.copy(acct = value as? String) // alias
            "category" -> current.copy(category = value as? String)
            "amount" -> current.copy(amount = (value as? Long))
            "description", "payee" -> current.copy(description = value as? String)
            "notes" -> current.copy(notes = value as? String)
            "date" -> current.copy(date = (value as? Long))
            "cleared" -> current.copy(cleared = (value as? Long))
            "pending" -> current.copy(pending = (value as? Long))
            "reconciled" -> current.copy(reconciled = (value as? Long))
            "sort_order" -> current.copy(sort_order = (value as? Long)?.toDouble())
            "tombstone" -> current.copy(tombstone = (value as? Long))
            // Split transaction columns
            "isParent", "is_parent" -> current.copy(isParent = (value as? Long))
            "isChild", "is_child" -> current.copy(isChild = (value as? Long))
            "parent_id" -> current.copy(parent_id = value as? String)
            // Transfer transaction column
            "transferred_id" -> current.copy(transferred_id = value as? String)
            // Ignore columns that don't exist in minimal schema
            else -> current
        }

        db.actualDatabaseQueries.insertTransaction(
            id = updated.id,
            acct = updated.acct,
            category = updated.category,
            amount = updated.amount ?: 0,
            description = updated.description,
            notes = updated.notes,
            date = updated.date,
            sort_order = updated.sort_order,
            tombstone = updated.tombstone ?: 0,
            cleared = updated.cleared ?: 1,
            pending = updated.pending ?: 0,
            reconciled = updated.reconciled ?: 0,
            isParent = updated.isParent ?: 0,
            isChild = updated.isChild ?: 0,
            parent_id = updated.parent_id,
            transferred_id = updated.transferred_id
        )
    }

    // ========== Budget Operations ==========

    private fun ensureBudgetExists(id: String) {
        val existing = db.actualDatabaseQueries.getBudgetById(id).executeAsOneOrNull()
        if (existing == null) {
            // Parse month and category from id (format: "YYYYMM-categoryId")
            val parts = id.split("-", limit = 2)
            val month = parts.getOrNull(0)?.toLongOrNull() ?: 0L
            val category = parts.getOrNull(1) ?: ""

            db.actualDatabaseQueries.insertBudget(
                id = id,
                month = month,
                category = category,
                amount = 0,
                carryover = 0,
                goal = null,
                tombstone = 0
            )
        }
    }

    private fun applyBudgetColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getBudgetById(id).executeAsOneOrNull() ?: return

        when (column) {
            "month" -> db.actualDatabaseQueries.insertBudget(
                id, (value as? Long) ?: current.month, current.category,
                current.amount, current.carryover, current.goal, current.tombstone
            )
            "category" -> db.actualDatabaseQueries.insertBudget(
                id, current.month, (value as? String) ?: current.category,
                current.amount, current.carryover, current.goal, current.tombstone
            )
            "amount" -> db.actualDatabaseQueries.insertBudget(
                id, current.month, current.category,
                (value as? Long) ?: 0L, current.carryover, current.goal, current.tombstone
            )
            "carryover" -> db.actualDatabaseQueries.insertBudget(
                id, current.month, current.category,
                current.amount, (value as? Long) ?: 0L, current.goal, current.tombstone
            )
            "goal" -> db.actualDatabaseQueries.insertBudget(
                id, current.month, current.category,
                current.amount, current.carryover, value as? Long, current.tombstone
            )
            "tombstone" -> db.actualDatabaseQueries.insertBudget(
                id, current.month, current.category,
                current.amount, current.carryover, current.goal, (value as? Long) ?: 0L
            )
        }
    }

    // ========== Query Methods ==========

    fun getAccounts() = db.actualDatabaseQueries.getAccounts().executeAsList()

    fun getPayees() = db.actualDatabaseQueries.getPayees().executeAsList()

    fun getCategories() = db.actualDatabaseQueries.getCategories().executeAsList()

    fun getCategoryGroups() = db.actualDatabaseQueries.getCategoryGroups().executeAsList()

    fun getTransactionsByAccount(accountId: String) =
        db.actualDatabaseQueries.getTransactionsByAccount(accountId).executeAsList()

    fun getTransactionsByDateRange(startDate: Long, endDate: Long) =
        db.actualDatabaseQueries.getTransactionsByDateRange(startDate, endDate).executeAsList()

    fun getBudgetForMonth(month: Long) =
        db.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()

    fun getBudgetForCategory(category: String) =
        db.actualDatabaseQueries.getBudgetForCategory(category).executeAsList()

    /**
     * Get spending totals by category for a date range.
     * Only includes transactions from on-budget accounts.
     * @param startDate Start date in YYYYMMDD format
     * @param endDate End date in YYYYMMDD format
     * @return List of (category, spent) pairs where spent is negative for expenses
     */
    fun getSpentByCategory(startDate: Long, endDate: Long) =
        db.actualDatabaseQueries.getSpentByCategory(startDate, endDate).executeAsList()

    /**
     * Get categories with their group information pre-joined.
     * Useful for budget views that need to display categories grouped.
     * Only returns visible (non-hidden, non-tombstoned) categories and groups.
     */
    fun getCategoriesWithGroups() =
        db.actualDatabaseQueries.getCategoriesWithGroups().executeAsList()

    /**
     * Get the balance for a specific account.
     * @param accountId The account ID
     * @return The sum of all transaction amounts for this account
     */
    fun getAccountBalance(accountId: String): Long =
        db.actualDatabaseQueries.getAccountBalance(accountId).executeAsOne().toLong()

    fun getLastSyncTimestamp(): String? =
        db.actualDatabaseQueries.getLastTimestamp().executeAsOneOrNull()?.last_ts

    fun setSyncMetadata(key: String, value: String) =
        db.actualDatabaseQueries.setSyncMetadata(key, value)

    fun getSyncMetadata(key: String): String? =
        db.actualDatabaseQueries.getSyncMetadata(key).executeAsOneOrNull()?.value_

    /**
     * Clear all data from the database.
     */
    fun clearAll() {
        db.actualDatabaseQueries.clearMessages()
        db.actualDatabaseQueries.clearTransactions()
        db.actualDatabaseQueries.clearCategories()
        db.actualDatabaseQueries.clearCategoryGroups()
        db.actualDatabaseQueries.clearPayees()
        db.actualDatabaseQueries.clearAccounts()
        db.actualDatabaseQueries.clearBudgets()
        db.actualDatabaseQueries.clearMetadata()
    }
}
