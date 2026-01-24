package com.actualbudget.sync

import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.db.DatabaseDriverFactory
import com.actualbudget.sync.io.BudgetFileManager
import com.actualbudget.sync.sync.SyncManager
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import platform.Foundation.*

/**
 * Create an HttpClient configured for iOS with Darwin engine.
 */
fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        engine {
            configureRequest {
                setAllowsCellularAccess(true)
            }
        }
    }
}

/**
 * Create an ActualDatabase with the given name.
 * This will apply schema migrations if needed.
 */
fun createDatabase(dbName: String): ActualDatabase {
    val driverFactory = DatabaseDriverFactory()
    val driver = driverFactory.createDriver(dbName)
    return ActualDatabase(driver)
}

/**
 * Create an ActualDatabase for an existing database file (e.g., downloaded from server).
 * This does NOT apply schema migrations - it opens the database as-is.
 */
fun createDatabaseForExistingFile(dbName: String): ActualDatabase {
    return com.actualbudget.sync.db.createDatabaseForExisting(dbName)
}

/**
 * Create a fully configured SyncManager for iOS.
 * This is a convenience function that creates all dependencies.
 */
fun createSyncManager(serverUrl: String, dbName: String = "actual.db"): SyncManager {
    val httpClient = createHttpClient()
    val database = createDatabase(dbName)
    return SyncManager(serverUrl, httpClient, database)
}

/**
 * Get the full path to the database file.
 * NativeSqliteDriver stores databases in the Library directory.
 *
 * @param dbName The database filename (e.g., "actual.db")
 * @return The full path to the database file
 */
fun getDatabasePath(dbName: String): String {
    val paths = NSSearchPathForDirectoriesInDomains(
        NSLibraryDirectory,
        NSUserDomainMask,
        true
    )
    val libraryPath = paths.firstOrNull() as? String ?: NSTemporaryDirectory()
    return "$libraryPath/$dbName"
}

/**
 * Check if a database file exists at the given path.
 *
 * @param dbName The database filename (e.g., "actual.db")
 * @return true if the database file exists
 */
fun databaseExists(dbName: String): Boolean {
    val path = getDatabasePath(dbName)
    val fileManager = NSFileManager.defaultManager
    return fileManager.fileExistsAtPath(path)
}

/**
 * Create the appropriate database based on whether one already exists.
 * - If database exists: opens it without schema migration (preserves downloaded data)
 * - If database doesn't exist: creates new one with SQLDelight schema
 *
 * @param dbName The database filename (e.g., "actual.db")
 * @return ActualDatabase instance
 */
fun createOrOpenDatabase(dbName: String): ActualDatabase {
    return if (databaseExists(dbName)) {
        println("[Helpers] Opening existing database: $dbName")
        createDatabaseForExistingFile(dbName)
    } else {
        println("[Helpers] Creating new database: $dbName")
        createDatabase(dbName)
    }
}

/**
 * Create a BudgetFileManager instance for file operations.
 */
fun createBudgetFileManager(): BudgetFileManager {
    return BudgetFileManager()
}

/**
 * Create an ActualDatabase for an existing database file at a specific path.
 * This is used for multi-budget support where each budget has its own folder.
 *
 * @param dbPath Full path to the database file (e.g., /Library/ActualBudget/My-Budget-abc123/db.sqlite)
 * @return ActualDatabase instance
 */
fun createDatabaseForExistingFileAtPath(dbPath: String): ActualDatabase {
    // Extract the directory and filename from the full path
    val directory = dbPath.substringBeforeLast("/")
    val fileName = dbPath.substringAfterLast("/")

    println("[Helpers] Opening database at path: $dbPath")
    println("[Helpers] Directory: $directory, Filename: $fileName")

    return com.actualbudget.sync.db.createDatabaseForExistingAtPath(directory, fileName)
}
