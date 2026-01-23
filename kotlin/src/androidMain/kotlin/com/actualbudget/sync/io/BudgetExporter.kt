package com.actualbudget.sync.io

import android.database.sqlite.SQLiteDatabase

actual fun clearCacheTables(dbPath: String) {
    val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    try {
        db.execSQL("DELETE FROM kvcache")
        println("[BudgetExporter] Cleared kvcache table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache table may not exist: ${e.message}")
    }

    try {
        db.execSQL("DELETE FROM kvcache_key")
        println("[BudgetExporter] Cleared kvcache_key table")
    } catch (e: Exception) {
        println("[BudgetExporter] kvcache_key table may not exist: ${e.message}")
    }

    db.close()
}
