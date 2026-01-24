package com.actualbudget.sync.io

import android.database.sqlite.SQLiteDatabase

actual fun checkpointDatabase(dbPath: String) {
    // No longer needed - we copy WAL files instead
    println("[BudgetExporter] Checkpoint skipped (copying WAL files instead)")
}

actual fun clearCacheTables(dbPath: String) {
    val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)

    // Switch from WAL to DELETE journal mode (webapp expects DELETE mode)
    try {
        db.execSQL("PRAGMA journal_mode=DELETE")
        println("[BudgetExporter] Set journal_mode to DELETE")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to set journal_mode: ${e.message}")
    }

    // Clear cache tables (may not exist in all databases)
    try {
        db.execSQL("DELETE FROM kvcache")
        println("[BudgetExporter] Cleared kvcache table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache table may not exist: ${e.message}")
    }

    try {
        db.execSQL("DELETE FROM kvcache_key")
        println("[BudgetExporter] Cleared kvcache_key table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache_key table may not exist: ${e.message}")
    }

    // DROP ActualSync-specific tables (not used by webapp)
    try {
        db.execSQL("DROP TABLE IF EXISTS sync_metadata")
        println("[BudgetExporter] Dropped sync_metadata table")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to drop sync_metadata: ${e.message}")
    }

    // DROP sync tables - webapp will create fresh ones when it loads
    try {
        db.execSQL("DROP TABLE IF EXISTS messages_crdt")
        println("[BudgetExporter] Dropped messages_crdt table")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to drop messages_crdt: ${e.message}")
    }

    try {
        db.execSQL("DROP TABLE IF EXISTS messages_clock")
        println("[BudgetExporter] Dropped messages_clock table")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to drop messages_clock: ${e.message}")
    }

    // Remove tombstone column from zero_budgets (webapp doesn't have it)
    try {
        db.execSQL("""
            CREATE TABLE zero_budgets_new (
                id TEXT PRIMARY KEY,
                month INTEGER,
                category TEXT,
                amount INTEGER DEFAULT 0,
                carryover INTEGER DEFAULT 0,
                goal INTEGER DEFAULT null,
                long_goal INTEGER DEFAULT null
            )
        """.trimIndent())
        db.execSQL("""
            INSERT INTO zero_budgets_new (id, month, category, amount, carryover, goal, long_goal)
            SELECT id, month, category, amount, carryover, goal, long_goal FROM zero_budgets
        """.trimIndent())
        db.execSQL("DROP TABLE zero_budgets")
        db.execSQL("ALTER TABLE zero_budgets_new RENAME TO zero_budgets")
        println("[BudgetExporter] Recreated zero_budgets without tombstone column")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to recreate zero_budgets: ${e.message}")
    }

    // VACUUM to clean up and ensure consistent file format
    try {
        db.execSQL("VACUUM")
        println("[BudgetExporter] Vacuumed database")
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to vacuum: ${e.message}")
    }

    db.close()
    println("[BudgetExporter] Successfully processed database for webapp compatibility")
}
