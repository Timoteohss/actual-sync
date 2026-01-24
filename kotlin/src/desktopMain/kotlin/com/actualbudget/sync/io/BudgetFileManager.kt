package com.actualbudget.sync.io

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

actual class BudgetFileManager actual constructor() {

    actual fun extractBudgetZip(zipData: ByteArray, targetDir: String): String? {
        return try {
            val targetDirFile = File(targetDir)
            if (!targetDirFile.exists()) {
                targetDirFile.mkdirs()
            }

            var dbPath: String? = null
            val filesToExtract = listOf("db.sqlite", "metadata.json")

            ZipInputStream(zipData.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    println("[BudgetFileManager] Found file: ${entry.name}")

                    if (entry.name in filesToExtract) {
                        val destFile = File(targetDir, entry.name)
                        FileOutputStream(destFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        println("[BudgetFileManager] Extracted ${entry.name} to ${destFile.absolutePath}")

                        if (entry.name == "db.sqlite") {
                            dbPath = destFile.absolutePath
                        }
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            if (dbPath == null) {
                println("[BudgetFileManager] db.sqlite not found in zip")
            }
            dbPath
        } catch (e: Exception) {
            println("[BudgetFileManager] Error extracting zip: ${e.message}")
            null
        }
    }

    actual fun getDefaultBudgetDir(): String {
        return System.getProperty("user.home") + "/.actual-budget"
    }

    actual fun delete(path: String): Boolean {
        return File(path).deleteRecursively()
    }

    actual fun exists(path: String): Boolean {
        return File(path).exists()
    }

    actual fun copy(source: String, destination: String): Boolean {
        return try {
            File(source).copyTo(File(destination), overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }

    actual fun readFile(path: String): ByteArray? {
        return try {
            File(path).readBytes()
        } catch (e: Exception) {
            println("[BudgetFileManager] Error reading file: ${e.message}")
            null
        }
    }

    actual fun writeFile(path: String, data: ByteArray): Boolean {
        return try {
            File(path).writeBytes(data)
            true
        } catch (e: Exception) {
            println("[BudgetFileManager] Error writing file: ${e.message}")
            false
        }
    }

    actual fun getTempDir(): String {
        return System.getProperty("java.io.tmpdir") ?: "/tmp"
    }

    // ============ Multi-Budget Support ============

    actual fun getBudgetsDirectory(): String {
        return System.getProperty("user.home") + "/.actual-budget/budgets"
    }

    actual fun listBudgetFolders(): List<String> {
        val budgetsDir = File(getBudgetsDirectory())
        if (!budgetsDir.exists()) {
            return emptyList()
        }

        return budgetsDir.listFiles()
            ?.filter { it.isDirectory && File(it, "metadata.json").exists() }
            ?.map { it.name }
            ?: emptyList()
    }

    actual fun createBudgetFolder(budgetId: String): String? {
        return try {
            val budgetDir = File(getBudgetsDirectory(), budgetId)
            budgetDir.mkdirs()
            println("[BudgetFileManager] Created budget folder: ${budgetDir.absolutePath}")
            budgetDir.absolutePath
        } catch (e: Exception) {
            println("[BudgetFileManager] Error creating budget folder: ${e.message}")
            null
        }
    }

    actual fun isDirectory(path: String): Boolean {
        return File(path).isDirectory
    }
}
