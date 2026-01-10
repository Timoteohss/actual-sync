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

            ZipInputStream(zipData.inputStream()).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    println("[BudgetFileManager] Found file: ${entry.name}")

                    if (entry.name == "db.sqlite") {
                        val destFile = File(targetDir, "db.sqlite")
                        FileOutputStream(destFile).use { fos ->
                            zis.copyTo(fos)
                        }
                        println("[BudgetFileManager] Extracted db.sqlite to ${destFile.absolutePath}")
                        return destFile.absolutePath
                    }

                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }

            println("[BudgetFileManager] db.sqlite not found in zip")
            null
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
}
