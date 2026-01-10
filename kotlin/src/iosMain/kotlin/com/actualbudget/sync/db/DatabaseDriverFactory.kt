package com.actualbudget.sync.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import platform.Foundation.*

actual class DatabaseDriverFactory {
    actual fun createDriver(dbName: String): SqlDriver {
        // Use explicit basePath to ensure we know where the database is
        val basePath = getLibraryPath()
        println("[DatabaseDriverFactory] Creating driver for $dbName at basePath: $basePath")
        return NativeSqliteDriver(
            schema = ActualDatabase.Schema,
            name = dbName,
            maxReaderConnections = 1
        )
    }
}

/**
 * Get the Library directory path.
 */
private fun getLibraryPath(): String {
    val paths = NSSearchPathForDirectoriesInDomains(
        NSLibraryDirectory,
        NSUserDomainMask,
        true
    )
    return paths.firstOrNull() as? String ?: NSTemporaryDirectory()
}

/**
 * Create a driver for an existing database without applying schema migrations.
 * Use this when opening a database downloaded from the server.
 * Creates only our custom tables (sync_metadata) if they don't exist.
 */
fun createDriverForExistingDb(dbName: String): SqlDriver {
    val basePath = getLibraryPath()
    println("[DatabaseDriverFactory] Opening existing db: $dbName at basePath: $basePath")

    return NativeSqliteDriver(
        configuration = DatabaseConfiguration(
            name = dbName,
            version = 1,
            create = { connection ->
                // Create only our custom sync_metadata table if it doesn't exist
                // The downloaded database already has Actual's tables (accounts, payees, etc.)
                println("[DatabaseDriverFactory] Creating sync_metadata table if needed")
                connection.rawExecSql("""
                    CREATE TABLE IF NOT EXISTS sync_metadata (
                        key TEXT PRIMARY KEY NOT NULL,
                        value TEXT
                    )
                """.trimIndent())
            },
            upgrade = { _, _, _ -> },  // No-op - don't run migrations
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = basePath  // Explicitly set the base path
            )
        )
    )
}

/**
 * Create the ActualDatabase instance for an existing database.
 * This skips schema creation/migration but adds our custom tables.
 */
fun createDatabaseForExisting(dbName: String): ActualDatabase {
    val driver = createDriverForExistingDb(dbName)
    return ActualDatabase(driver)
}
