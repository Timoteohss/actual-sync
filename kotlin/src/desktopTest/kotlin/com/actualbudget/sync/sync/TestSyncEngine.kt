package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.proto.MessageEnvelope

/**
 * Represents a single change captured by the test sync engine.
 */
data class CapturedChange(
    val dataset: String,
    val row: String,
    val column: String,
    val value: Any?
)

/**
 * A test sync engine that captures changes for verification in tests.
 *
 * This replaces the real SyncEngine for unit testing, allowing us to
 * verify that the correct changes are created without needing a full
 * sync infrastructure.
 *
 * Implements [ChangeEngine] so it can be used with operation classes.
 */
class TestSyncEngine(
    private val database: ActualDatabase
) : ChangeEngine {
    private val changes = mutableListOf<CapturedChange>()

    /**
     * Record a change. This is called by operation classes.
     * Returns null (test implementation doesn't need to create actual envelopes).
     */
    override fun createChange(dataset: String, row: String, column: String, value: Any?): MessageEnvelope? {
        changes.add(CapturedChange(dataset, row, column, value))
        return null
    }

    /**
     * Get all captured changes for verification.
     */
    fun getChanges(): List<CapturedChange> = changes.toList()

    /**
     * Clear all captured changes.
     */
    fun clearChanges() {
        changes.clear()
    }

    /**
     * Get changes for a specific dataset.
     */
    fun getChangesForDataset(dataset: String): List<CapturedChange> =
        changes.filter { it.dataset == dataset }

    /**
     * Get changes for a specific row.
     */
    fun getChangesForRow(dataset: String, row: String): List<CapturedChange> =
        changes.filter { it.dataset == dataset && it.row == row }

    /**
     * Check if a specific change was made.
     */
    fun hasChange(dataset: String, row: String, column: String, value: Any?): Boolean =
        changes.any { it.dataset == dataset && it.row == row && it.column == column && it.value == value }
}
