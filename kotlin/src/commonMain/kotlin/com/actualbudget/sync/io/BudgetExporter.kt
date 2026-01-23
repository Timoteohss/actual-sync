package com.actualbudget.sync.io

import com.oldguy.common.io.File
import com.oldguy.common.io.FileMode
import com.oldguy.common.io.ZipFile
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * High-level class for preparing budgets for upload to Actual Budget server.
 *
 * This class handles the full cycle of:
 * 1. Copying the database to a temp location
 * 2. Clearing cache tables (kvcache, kvcache_key)
 * 3. Creating metadata.json with resetClock=true
 * 4. Packaging both files into a ZIP
 * 5. Cleaning up temp files
 */
class BudgetExporter(
    private val fileManager: BudgetFileManager
) {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    /**
     * Prepare a budget for upload to server.
     *
     * @param budgetId Unique identifier for the budget
     * @param budgetName Display name of the budget
     * @param dbPath Path to the budget's db.sqlite file
     * @param groupId Optional group ID for sync operations
     * @param encryptKeyId Optional encryption key ID
     * @return ZIP file as ByteArray ready for upload
     * @throws Exception if preparation fails
     */
    @Throws(Exception::class)
    suspend fun prepareBudgetForUpload(
        budgetId: String,
        budgetName: String,
        dbPath: String,
        groupId: String? = null,
        encryptKeyId: String? = null
    ): ByteArray {
        val tempDir = fileManager.getTempDir()
        val workDirName = "budget_export_$budgetId"
        val workDir = "$tempDir/$workDirName"
        val tempDbPath = "$workDir/db.sqlite"
        val metadataPath = "$workDir/metadata.json"
        val zipPath = "$workDir/budget.zip"

        try {
            println("[BudgetExporter] Preparing budget for upload: $budgetId")

            // Step 1: Create work directory using KmpIO and copy database
            if (fileManager.exists(workDir)) {
                fileManager.delete(workDir)
            }
            // KmpIO File.resolve() creates directory if it doesn't exist
            val tempDirFile = File(tempDir)
            tempDirFile.resolve(workDirName)

            if (!fileManager.copy(dbPath, tempDbPath)) {
                throw Exception("Failed to copy database to temp location")
            }
            println("[BudgetExporter] Copied database to $tempDbPath")

            // Step 2: Clear cache tables in the temp database
            clearCacheTables(tempDbPath)
            println("[BudgetExporter] Cleared cache tables")

            // Step 3: Create metadata.json
            val now = Clock.System.now()
            val localDateTime = now.toLocalDateTime(TimeZone.UTC)
            val timestamp = buildString {
                append(localDateTime.year)
                append("-")
                append(localDateTime.month.ordinal.plus(1).toString().padStart(2, '0'))
                append("-")
                append(localDateTime.day.toString().padStart(2, '0'))
                append("T")
                append(localDateTime.hour.toString().padStart(2, '0'))
                append(":")
                append(localDateTime.minute.toString().padStart(2, '0'))
                append(":")
                append(localDateTime.second.toString().padStart(2, '0'))
                append(".000Z")
            }

            val metadata = BudgetMetadata(
                id = budgetId,
                budgetName = budgetName,
                resetClock = true,
                cloudFileId = budgetId,
                groupId = groupId,
                lastUploaded = timestamp,
                encryptKeyId = encryptKeyId
            )

            val metadataJson = json.encodeToString(metadata)
            if (!fileManager.writeFile(metadataPath, metadataJson.encodeToByteArray())) {
                throw Exception("Failed to write metadata.json")
            }
            println("[BudgetExporter] Created metadata.json")

            // Step 4: Create ZIP with both files using KmpIO
            val zipFile = File(zipPath)
            val dbFile = File(tempDbPath)
            val metadataFile = File(metadataPath)

            ZipFile(zipFile, FileMode.Write).use { zip ->
                zip.zipFile(dbFile)
                zip.zipFile(metadataFile)
            }
            println("[BudgetExporter] Created ZIP file at $zipPath")

            // Step 5: Read ZIP as ByteArray
            val zipData = fileManager.readFile(zipPath)
                ?: throw Exception("Failed to read ZIP file")
            println("[BudgetExporter] Read ZIP file (${zipData.size} bytes)")

            // Step 6: Clean up temp files
            fileManager.delete(workDir)
            println("[BudgetExporter] Cleaned up temp files")

            return zipData
        } catch (e: Exception) {
            // Clean up on failure
            fileManager.delete(workDir)
            throw e
        }
    }
}

/**
 * Clear kvcache and kvcache_key tables in the database.
 * Platform-specific implementation due to different SQLite drivers.
 */
expect fun clearCacheTables(dbPath: String)
