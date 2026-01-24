package com.actualbudget.sync.io

import java.io.File
import java.sql.DriverManager

actual fun checkpointDatabase(dbPath: String) {
    // No longer needed - we copy WAL files instead
    println("[BudgetExporter] Checkpoint skipped (copying WAL files instead)")
}

actual fun clearCacheTables(dbPath: String) {
    println("[BudgetExporter] Processing database for webapp compatibility: $dbPath")

    // Delete WAL and SHM files if they exist (needed to switch from WAL mode)
    val walFile = File("$dbPath-wal")
    val shmFile = File("$dbPath-shm")
    if (walFile.exists()) {
        walFile.delete()
        println("[BudgetExporter] Deleted WAL file")
    }
    if (shmFile.exists()) {
        shmFile.delete()
        println("[BudgetExporter] Deleted SHM file")
    }

    // Load SQLite JDBC driver
    Class.forName("org.sqlite.JDBC")

    val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    try {
        connection.createStatement().use { stmt ->
            // Switch from WAL to DELETE journal mode (webapp expects DELETE mode)
            try {
                stmt.execute("PRAGMA journal_mode=DELETE")
                println("[BudgetExporter] Set journal_mode to DELETE")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to set journal_mode: ${e.message}")
            }

            // Clear cache tables (may not exist in all databases)
            try {
                stmt.executeUpdate("DELETE FROM kvcache")
                println("[BudgetExporter] Cleared kvcache table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache table not found: ${e.message}")
            }

            try {
                stmt.executeUpdate("DELETE FROM kvcache_key")
                println("[BudgetExporter] Cleared kvcache_key table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache_key table not found: ${e.message}")
            }

            // DROP ActualSync-specific tables (not used by webapp)
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS sync_metadata")
                println("[BudgetExporter] Dropped sync_metadata table")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to drop sync_metadata: ${e.message}")
            }

            // Clear sync tables but keep them (webapp expects them to exist)
            // The webapp will create fresh clock data when it loads with resetClock=true
            try {
                stmt.executeUpdate("DELETE FROM messages_crdt")
                println("[BudgetExporter] Cleared messages_crdt table")
            } catch (e: Exception) {
                println("[BudgetExporter] messages_crdt table not found: ${e.message}")
            }

            try {
                stmt.executeUpdate("DELETE FROM messages_clock")
                println("[BudgetExporter] Cleared messages_clock table")
            } catch (e: Exception) {
                println("[BudgetExporter] messages_clock table not found: ${e.message}")
            }

            // VACUUM to clean up and ensure consistent file format
            try {
                stmt.execute("VACUUM")
                println("[BudgetExporter] Vacuumed database")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to vacuum: ${e.message}")
            }
        }

        println("[BudgetExporter] Successfully processed database for webapp compatibility")
    } finally {
        connection.close()
    }
}
