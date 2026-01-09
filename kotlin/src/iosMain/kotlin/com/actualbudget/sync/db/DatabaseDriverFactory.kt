package com.actualbudget.sync.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(dbName: String): SqlDriver {
        return NativeSqliteDriver(ActualDatabase.Schema, dbName)
    }
}
