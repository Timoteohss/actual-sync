package com.actualbudget.sync.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory(private val dbPath: String? = null) {
    actual fun createDriver(dbName: String): SqlDriver {
        val dbFile = if (dbPath != null) {
            val dir = File(dbPath)
            if (!dir.exists()) dir.mkdirs()
            File(dir, dbName)
        } else {
            File(dbName)
        }

        val isNew = !dbFile.exists()
        val url = "jdbc:sqlite:${dbFile.absolutePath}"

        val driver = JdbcSqliteDriver(url)

        if (isNew) {
            ActualDatabase.Schema.create(driver)
        }

        return driver
    }
}
