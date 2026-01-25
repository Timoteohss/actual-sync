package com.actualbudget.sync.sync

import com.actualbudget.sync.proto.MessageEnvelope

/**
 * Interface for creating CRDT changes.
 * Implementations are responsible for creating, storing, and syncing changes.
 *
 * This abstraction allows domain operations (TransactionOperations, AccountOperations, etc.)
 * to be testable with a mock implementation while using the real SyncEngine in production.
 */
interface ChangeEngine {
    /**
     * Create a change record for a field in a dataset.
     *
     * @param dataset The table/dataset name (e.g., "transactions", "accounts")
     * @param row The row ID (primary key)
     * @param column The column/field name
     * @param value The new value (null to clear)
     * @return The message envelope (null for test implementations)
     */
    fun createChange(dataset: String, row: String, column: String, value: Any?): MessageEnvelope?
}
