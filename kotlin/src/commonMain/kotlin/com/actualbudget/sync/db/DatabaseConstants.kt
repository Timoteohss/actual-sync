package com.actualbudget.sync.db

/**
 * Database schema version constants.
 * These must be kept in sync across all platform implementations.
 */
object DatabaseConstants {
    /**
     * Current database schema version.
     * Increment this when making schema changes that require migration.
     *
     * Version history:
     * - 1: Initial schema
     * - 2: Added tombstone column to zero_budgets
     */
    const val SCHEMA_VERSION = 2
}
