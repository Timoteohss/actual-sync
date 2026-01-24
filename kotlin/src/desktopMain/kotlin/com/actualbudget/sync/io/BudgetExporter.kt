package com.actualbudget.sync.io

import java.sql.DriverManager

actual fun checkpointDatabase(dbPath: String) {
    // No longer needed - we copy WAL files instead
    println("[BudgetExporter] Checkpoint skipped (copying WAL files instead)")
}

actual fun clearCacheTables(dbPath: String) {
    // Load SQLite JDBC driver
    Class.forName("org.sqlite.JDBC")

    val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    try {
        connection.createStatement().use { stmt ->
            // Clear cache tables (may not exist in all databases)
            try {
                stmt.executeUpdate("DELETE FROM kvcache")
                println("[BudgetExporter] Cleared kvcache table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache table may not exist: ${e.message}")
            }

            try {
                stmt.executeUpdate("DELETE FROM kvcache_key")
                println("[BudgetExporter] Cleared kvcache_key table")
            } catch (e: Exception) {
                println("[BudgetExporter] kvcache_key table may not exist: ${e.message}")
            }

            try {
                stmt.executeUpdate("DELETE FROM sync_metadata")
                println("[BudgetExporter] Cleared sync_metadata table")
            } catch (e: Exception) {
                println("[BudgetExporter] sync_metadata table may not exist: ${e.message}")
            }
        }

        // DROP sync tables - webapp will create fresh ones when it loads
        connection.createStatement().use { stmt ->
            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS messages_crdt")
                println("[BudgetExporter] Dropped messages_crdt table")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to drop messages_crdt: ${e.message}")
            }

            try {
                stmt.executeUpdate("DROP TABLE IF EXISTS messages_clock")
                println("[BudgetExporter] Dropped messages_clock table")
            } catch (e: Exception) {
                println("[BudgetExporter] Failed to drop messages_clock: ${e.message}")
            }
        }

        println("[BudgetExporter] Successfully processed cache and sync tables")
    } finally {
        connection.close()
    }
}
