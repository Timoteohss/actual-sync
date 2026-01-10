package com.actualbudget.sync.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Factory for creating platform-specific SQLite drivers.
 */
expect class DatabaseDriverFactory {
    /**
     * Create a driver that will apply schema migrations (for new databases).
     */
    fun createDriver(dbName: String): SqlDriver
}

/**
 * Create the ActualDatabase instance.
 */
fun createDatabase(driverFactory: DatabaseDriverFactory, dbName: String = "actual.db"): ActualDatabase {
    val driver = driverFactory.createDriver(dbName)
    return ActualDatabase(driver)
}
