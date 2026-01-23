package com.actualbudget.sync.io

import java.sql.DriverManager

actual fun clearCacheTables(dbPath: String) {
    // Load SQLite JDBC driver
    Class.forName("org.sqlite.JDBC")

    val connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
    try {
        connection.createStatement().use { stmt ->
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
        }
    } finally {
        connection.close()
    }
}
