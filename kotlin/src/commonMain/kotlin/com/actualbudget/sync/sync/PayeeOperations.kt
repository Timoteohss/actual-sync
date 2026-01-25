package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Payee operations for the Actual Budget database.
 *
 * Handles:
 * - Payee CRUD (create, update, delete)
 * - Payee merging (combine duplicate payees)
 *
 * All mutation methods use the sync engine to create CRDT changes
 * that propagate to the server.
 *
 * @param engine The change engine for creating sync changes
 * @param database The database for reading payee data
 */
class PayeeOperations(
    private val engine: ChangeEngine,
    private val database: ActualDatabase
) {
    // ========== Payee CRUD ==========

    /**
     * Create a new payee.
     * Also creates the required payee_mapping entry (id -> id) for the payee to be usable.
     *
     * @param id Payee ID
     * @param name Payee name
     * @return The payee ID
     */
    fun createPayee(id: String, name: String): String {
        engine.createChange("payees", id, "name", name)
        engine.createChange("payees", id, "tombstone", 0)
        // Actual Budget requires a payee_mapping entry for each payee
        // The mapping points to itself (id -> id) for regular payees
        // NOTE: payee_mapping table has NO tombstone column in Actual Budget
        engine.createChange("payee_mapping", id, "targetId", id)
        return id
    }

    /**
     * Update a payee field.
     *
     * @param id The payee ID
     * @param field The field name to update
     * @param value The new value
     */
    fun updatePayee(id: String, field: String, value: Any?) {
        engine.createChange("payees", id, field, value)
    }

    /**
     * Delete a payee (set tombstone).
     *
     * @param id The payee ID
     */
    fun deletePayee(id: String) {
        engine.createChange("payees", id, "tombstone", 1)
    }

    // ========== Payee Merge ==========

    /**
     * Merge a source payee into a target payee.
     * All transactions using the source payee will be updated to use the target payee.
     * The source payee will be tombstoned (deleted).
     *
     * @param sourcePayeeId The payee to merge from (will be deleted)
     * @param targetPayeeId The payee to merge into (will receive transactions)
     */
    fun mergePayee(sourcePayeeId: String, targetPayeeId: String) {
        // Get all transactions using the source payee
        val transactions = database.actualDatabaseQueries
            .getTransactionsByPayee(sourcePayeeId)
            .executeAsList()

        // Update each transaction to use the target payee
        for (txn in transactions) {
            engine.createChange("transactions", txn.id, "payee", targetPayeeId)
        }

        // Delete the source payee
        deletePayee(sourcePayeeId)
    }
}
