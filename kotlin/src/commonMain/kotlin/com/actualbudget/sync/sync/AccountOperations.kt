package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Account operations for the Actual Budget database.
 *
 * Handles:
 * - Account CRUD (create, update, delete)
 * - Account lifecycle (close, reopen)
 * - Account reordering within category (on-budget, off-budget, closed)
 *
 * All mutation methods use the sync engine to create CRDT changes
 * that propagate to the server.
 *
 * @param engine The change engine for creating sync changes
 * @param database The database for reading account data
 */
class AccountOperations(
    private val engine: ChangeEngine,
    private val database: ActualDatabase
) {
    // ========== Account CRUD ==========

    /**
     * Create a new account.
     *
     * @param id Account ID
     * @param name Account name
     * @param offbudget Whether this is an off-budget account (default: false)
     * @param sortOrder Optional sort order for display ordering
     * @return The account ID
     */
    fun createAccount(
        id: String,
        name: String,
        offbudget: Boolean = false,
        sortOrder: Double? = null
    ): String {
        engine.createChange("accounts", id, "name", name)
        engine.createChange("accounts", id, "offbudget", if (offbudget) 1 else 0)
        engine.createChange("accounts", id, "closed", 0)
        if (sortOrder != null) {
            engine.createChange("accounts", id, "sort_order", sortOrder)
        }
        engine.createChange("accounts", id, "tombstone", 0)
        return id
    }

    /**
     * Update an account field.
     *
     * @param id The account ID
     * @param field The field name to update
     * @param value The new value
     */
    fun updateAccount(id: String, field: String, value: Any?) {
        engine.createChange("accounts", id, field, value)
    }

    /**
     * Delete an account (set tombstone).
     *
     * @param id The account ID
     */
    fun deleteAccount(id: String) {
        engine.createChange("accounts", id, "tombstone", 1)
    }

    // ========== Account Lifecycle ==========

    /**
     * Close an account.
     * Closed accounts are hidden from the main view but retain their data.
     *
     * @param accountId The account to close
     */
    fun closeAccount(accountId: String) {
        engine.createChange("accounts", accountId, "closed", 1)
    }

    /**
     * Reopen a closed account.
     *
     * @param accountId The account to reopen
     */
    fun reopenAccount(accountId: String) {
        engine.createChange("accounts", accountId, "closed", 0)
    }

    // ========== Reordering ==========

    /**
     * Move an account to a new position within its category (on-budget, off-budget, or closed).
     * Accounts are grouped by their status and reordering happens within each group.
     *
     * @param accountId The account to move
     * @param targetAccountId The account to insert before, or null to append at end
     */
    fun moveAccount(accountId: String, targetAccountId: String?) {
        // Get the account to determine its type
        val account = database.actualDatabaseQueries.getAccountById(accountId).executeAsOneOrNull()
            ?: return

        // Get accounts of same type
        val accounts = when {
            account.closed == 1L -> database.actualDatabaseQueries.getClosedAccountsOrdered().executeAsList()
            account.offbudget == 1L -> database.actualDatabaseQueries.getOffBudgetAccountsOrdered().executeAsList()
            else -> database.actualDatabaseQueries.getOnBudgetAccountsOrdered().executeAsList()
        }
            .filter { it.id != accountId }
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(accounts, targetAccountId)

        // Update the account
        engine.createChange("accounts", accountId, "sort_order", newSortOrder)
    }
}
