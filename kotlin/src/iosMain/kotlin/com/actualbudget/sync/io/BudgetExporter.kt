package com.actualbudget.sync.io

import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.JournalMode
import co.touchlab.sqliter.createDatabaseManager
import com.actualbudget.sync.db.DatabaseConstants
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager

actual fun checkpointDatabase(dbPath: String) {
    // Not needed - iOS now uses DELETE journal mode (no WAL)
    println("[BudgetExporter] Checkpoint not needed (using DELETE journal mode)")
}

@OptIn(ExperimentalForeignApi::class)
actual fun clearCacheTables(dbPath: String) {
    // Clear cache tables and sync tables to ensure a clean slate for upload
    // The webapp will create a fresh clock when it loads the budget
    println("[BudgetExporter] Processing database for webapp compatibility: $dbPath")

    try {
        // Extract directory and filename from full path
        val lastSlash = dbPath.lastIndexOf('/')
        val basePath = if (lastSlash >= 0) dbPath.substring(0, lastSlash) else ""
        val dbName = if (lastSlash >= 0) dbPath.substring(lastSlash + 1) else dbPath

        // Delete WAL and SHM files if they exist (needed to switch from WAL mode)
        val fileManager = NSFileManager.defaultManager
        val walPath = "$dbPath-wal"
        val shmPath = "$dbPath-shm"
        if (fileManager.fileExistsAtPath(walPath)) {
            fileManager.removeItemAtPath(walPath, null)
            println("[BudgetExporter] Deleted WAL file")
        }
        if (fileManager.fileExistsAtPath(shmPath)) {
            fileManager.removeItemAtPath(shmPath, null)
            println("[BudgetExporter] Deleted SHM file")
        }

        // Open database with DELETE journal mode explicitly
        // Use same version as DatabaseDriverFactory to avoid version mismatch errors
        val dbManager = createDatabaseManager(
            DatabaseConfiguration(
                name = dbName,
                version = DatabaseConstants.SCHEMA_VERSION,
                journalMode = JournalMode.DELETE,
                create = { },
                upgrade = { _, _, _ -> },
                extendedConfig = DatabaseConfiguration.Extended(basePath = basePath)
            )
        )

        val connection = dbManager.createMultiThreadedConnection()
        try {
            // CRITICAL: Explicitly set journal mode to DELETE
            // The configuration option only works for NEW databases
            // This PRAGMA actually converts the file header from WAL to DELETE
            try {
                connection.rawExecSql("PRAGMA journal_mode=DELETE")
                println("[BudgetExporter] Set journal_mode to DELETE")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to set journal_mode: ${e.message}")
            }

            // Clear cache tables (may not exist in all databases)
            try {
                connection.rawExecSql("DELETE FROM kvcache")
                println("[BudgetExporter] Cleared kvcache table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache table not found (normal for ActualSync dbs)")
            }

            try {
                connection.rawExecSql("DELETE FROM kvcache_key")
                println("[BudgetExporter] Cleared kvcache_key table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache_key table not found (normal for ActualSync dbs)")
            }

            // DROP ActualSync-specific tables (not used by webapp)
            try {
                connection.rawExecSql("DROP TABLE IF EXISTS sync_metadata")
                println("[BudgetExporter] Dropped sync_metadata table")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to drop sync_metadata: ${e.message}")
            }

            // Clear sync tables but keep them (webapp expects them to exist)
            // The webapp will create fresh clock data when it loads with resetClock=true
            try {
                connection.rawExecSql("DELETE FROM messages_crdt")
                println("[BudgetExporter] Cleared messages_crdt table")
            } catch (e: Exception) {
                println("[BudgetExporter] messages_crdt table not found: ${e.message}")
            }

            try {
                connection.rawExecSql("DELETE FROM messages_clock")
                println("[BudgetExporter] Cleared messages_clock table")
            } catch (e: Exception) {
                println("[BudgetExporter] messages_clock table not found: ${e.message}")
            }

            // VACUUM to clean up and ensure consistent file format
            try {
                connection.rawExecSql("VACUUM")
                println("[BudgetExporter] Vacuumed database")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to vacuum: ${e.message}")
            }

            println("[BudgetExporter] Successfully processed database for webapp compatibility")
        } finally {
            connection.close()
        }
    } catch (e: Exception) {
        println("[BudgetExporter] Failed to process tables: ${e.message}")
        throw IllegalStateException("Failed to process database for webapp compatibility: ${e.message}", e)
    }
}
