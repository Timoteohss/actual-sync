package com.actualbudget.sync.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import co.touchlab.sqliter.JournalMode
import platform.Foundation.*

actual class DatabaseDriverFactory {
    actual fun createDriver(dbName: String): SqlDriver {
        // Use explicit basePath to ensure database is at a known location
        val basePath = getLibraryPath()
        println("[DatabaseDriverFactory] Creating driver for $dbName at basePath: $basePath")
        return NativeSqliteDriver(
            schema = ActualDatabase.Schema,
            name = dbName,
            maxReaderConnections = 1,
            onConfiguration = { config ->
                config.copy(
                    // Use DELETE journal mode like Actual Budget does
                    // This avoids WAL files which complicate database export
                    journalMode = JournalMode.DELETE,
                    extendedConfig = DatabaseConfiguration.Extended(
                        basePath = basePath
                    )
                )
            }
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
            version = DatabaseConstants.SCHEMA_VERSION,
            // Use DELETE journal mode like Actual Budget does
            journalMode = JournalMode.DELETE,
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
                // Ensure zero_budgets has tombstone column
                addMissingColumns(connection)
            },
            upgrade = { connection, oldVersion, newVersion ->
                println("[DatabaseDriverFactory] Upgrading from $oldVersion to $newVersion")
                if (oldVersion < 2) {
                    addMissingColumns(connection)
                }
            },
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = basePath  // Explicitly set the base path
            )
        )
    )
}

/**
 * Add missing columns to existing tables for compatibility.
 */
private fun addMissingColumns(connection: co.touchlab.sqliter.DatabaseConnection) {
    // Add tombstone to zero_budgets if missing
    try {
        connection.rawExecSql("ALTER TABLE zero_budgets ADD COLUMN tombstone INTEGER NOT NULL DEFAULT 0")
        println("[DatabaseDriverFactory] Added tombstone column to zero_budgets")
    } catch (e: Exception) {
        // Column likely already exists
        println("[DatabaseDriverFactory] tombstone column already exists or table missing: ${e.message}")
    }
}

/**
 * Create the ActualDatabase instance for an existing database.
 * This skips schema creation/migration but adds our custom tables.
 */
fun createDatabaseForExisting(dbName: String): ActualDatabase {
    val driver = createDriverForExistingDb(dbName)
    return ActualDatabase(driver)
}

/**
 * Create a driver for an existing database at a specific path.
 * Used for multi-budget support where each budget has its own folder.
 *
 * @param basePath The directory containing the database (e.g., /Library/ActualBudget/My-Budget-abc123)
 * @param dbName The database filename (e.g., db.sqlite)
 */
fun createDriverForExistingDbAtPath(basePath: String, dbName: String): SqlDriver {
    println("[DatabaseDriverFactory] Opening existing db: $dbName at basePath: $basePath")

    return NativeSqliteDriver(
        configuration = DatabaseConfiguration(
            name = dbName,
            version = DatabaseConstants.SCHEMA_VERSION,
            journalMode = JournalMode.DELETE,
            create = { connection ->
                println("[DatabaseDriverFactory] Creating sync_metadata table if needed")
                connection.rawExecSql("""
                    CREATE TABLE IF NOT EXISTS sync_metadata (
                        key TEXT PRIMARY KEY NOT NULL,
                        value TEXT
                    )
                """.trimIndent())
                addMissingColumns(connection)
            },
            upgrade = { connection, oldVersion, newVersion ->
                println("[DatabaseDriverFactory] Upgrading from $oldVersion to $newVersion")
                if (oldVersion < 2) {
                    addMissingColumns(connection)
                }
            },
            extendedConfig = DatabaseConfiguration.Extended(
                basePath = basePath
            )
        )
    )
}

/**
 * Create the ActualDatabase instance for an existing database at a specific path.
 * Used for multi-budget support where each budget has its own folder.
 *
 * @param basePath The directory containing the database
 * @param dbName The database filename
 */
fun createDatabaseForExistingAtPath(basePath: String, dbName: String): ActualDatabase {
    val driver = createDriverForExistingDbAtPath(basePath, dbName)
    return ActualDatabase(driver)
}
