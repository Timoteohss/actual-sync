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
        // Store in CRDT log
        db.actualDatabaseQueries.insertMessage(
            timestamp = timestamp,
            dataset = message.dataset,
            row_id = message.row,
            column_name = message.column,
            value_ = message.value,
            applied = 0
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
                // Handle payee mapping
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
            "zero_budgets", "zero_budget_months" -> {
                // Handle budget data
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

        // Mark as applied
        db.actualDatabaseQueries.markMessageApplied(timestamp)
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
                sort_order = 0,
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
                        offbudget = (value as? Long)?.toInt()?.toLong() ?: 0L,
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
                        closed = (value as? Long)?.toInt()?.toLong() ?: 0L,
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
                        sort_order = (value as? Long) ?: 0L,
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
                        tombstone = (value as? Long)?.toInt()?.toLong() ?: 0L
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
                tombstone = 0
            )
        }
    }

    private fun applyPayeeColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getPayeeById(id).executeAsOneOrNull() ?: return
        when (column) {
            "name" -> db.actualDatabaseQueries.insertPayee(id, value as? String ?: "", current.category, current.tombstone)
            "category" -> db.actualDatabaseQueries.insertPayee(id, current.name, value as? String, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertPayee(id, current.name, current.category, (value as? Long) ?: 0L)
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
                sort_order = 0,
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
            "sort_order" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, (value as? Long) ?: 0L, current.hidden, current.tombstone)
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
                sort_order = 0,
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
            "sort_order" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, (value as? Long) ?: 0L, current.hidden, current.tombstone)
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
                payee = null,
                notes = null,
                date = null,
                financial_id = null,
                type = null,
                cleared = 0,
                reconciled = 0,
                error = null,
                starting_balance_flag = 0,
                transferred_id = null,
                sort_order = 0,
                tombstone = 0,
                schedule = null,
                parent_id = null,
                is_parent = 0,
                is_child = 0
            )
        }
    }

    private fun applyTransactionColumn(id: String, column: String, value: Any?) {
        val current = db.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull() ?: return

        // Build updated transaction
        val updated = when (column) {
            "acct" -> current.copy(acct = value as? String)
            "account" -> current.copy(acct = value as? String) // alias
            "category" -> current.copy(category = value as? String)
            "amount" -> current.copy(amount = (value as? Long) ?: 0L)
            "payee" -> current.copy(payee = value as? String)
            "notes" -> current.copy(notes = value as? String)
            "date" -> current.copy(date = (value as? Long))
            "financial_id" -> current.copy(financial_id = value as? String)
            "type" -> current.copy(type = value as? String)
            "cleared" -> current.copy(cleared = (value as? Long) ?: 0L)
            "reconciled" -> current.copy(reconciled = (value as? Long) ?: 0L)
            "error" -> current.copy(error = value as? String)
            "starting_balance_flag" -> current.copy(starting_balance_flag = (value as? Long) ?: 0L)
            "transferred_id" -> current.copy(transferred_id = value as? String)
            "sort_order" -> current.copy(sort_order = (value as? Long) ?: 0L)
            "tombstone" -> current.copy(tombstone = (value as? Long) ?: 0L)
            "schedule" -> current.copy(schedule = value as? String)
            "parent_id" -> current.copy(parent_id = value as? String)
            "is_parent" -> current.copy(is_parent = (value as? Long) ?: 0L)
            "is_child" -> current.copy(is_child = (value as? Long) ?: 0L)
            else -> current
        }

        db.actualDatabaseQueries.insertTransaction(
            id = updated.id,
            acct = updated.acct,
            category = updated.category,
            amount = updated.amount,
            payee = updated.payee,
            notes = updated.notes,
            date = updated.date,
            financial_id = updated.financial_id,
            type = updated.type,
            cleared = updated.cleared,
            reconciled = updated.reconciled,
            error = updated.error,
            starting_balance_flag = updated.starting_balance_flag,
            transferred_id = updated.transferred_id,
            sort_order = updated.sort_order,
            tombstone = updated.tombstone,
            schedule = updated.schedule,
            parent_id = updated.parent_id,
            is_parent = updated.is_parent,
            is_child = updated.is_child
        )
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
