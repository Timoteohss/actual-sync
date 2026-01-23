package com.actualbudget.sync.io

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration

actual fun clearCacheTables(dbPath: String) {
    // Open database directly and clear cache tables
    val driver = NativeSqliteDriver(
        configuration = DatabaseConfiguration(
            name = dbPath.substringAfterLast("/"),
            version = 1,
            create = { },
            upgrade = { _, _, _ -> },
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = dbPath.substringBeforeLast("/")
            )
        )
    )

    try {
        driver.execute(null, "DELETE FROM kvcache", 0)
        println("[BudgetExporter] Cleared kvcache table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache table may not exist: ${e.message}")
    }

    try {
        driver.execute(null, "DELETE FROM kvcache_key", 0)
        println("[BudgetExporter] Cleared kvcache_key table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache_key table may not exist: ${e.message}")
    }

    driver.close()
}
