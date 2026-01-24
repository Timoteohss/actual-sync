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

    // Clear sync tables but keep them (webapp expects them to exist)
    // The webapp will create fresh clock data when it loads with resetClock=true
    try {
        db.execSQL("DELETE FROM messages_crdt")
        println("[BudgetExporter] Cleared messages_crdt table")
    } catch (e: Exception) {
        println("[BudgetExporter] messages_crdt table not found: ${e.message}")
    }

    try {
        db.execSQL("DELETE FROM messages_clock")
        println("[BudgetExporter] Cleared messages_clock table")
    } catch (e: Exception) {
        println("[BudgetExporter] messages_clock table not found: ${e.message}")
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
