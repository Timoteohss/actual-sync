package com.actualbudget.sync.io

import com.oldguy.common.io.File
import com.oldguy.common.io.FileMode
import com.oldguy.common.io.ZipFile
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
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
     * Generate a budget ID in Actual's format: [Budget-Name-Slug]-[7-char-UUID]
     *
     * Examples:
     * - "My Finances" -> "My-Finances-da5fda6"
     * - "Budget 2024!" -> "Budget-2024--abc1234"
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generateBudgetId(budgetName: String): String {
        // Replace spaces and non-alphanumeric characters with hyphens
        val slug = budgetName.replace(Regex("[^A-Za-z0-9]"), "-")
        // Get first 7 characters of a UUID (lowercase like Actual does)
        val uuidSuffix = Uuid.random().toString().take(7)
        return "$slug-$uuidSuffix"
    }

    /**
     * Try to read existing metadata.json file.
     * Returns null if file doesn't exist or can't be parsed.
     */
    private fun readExistingMetadata(metadataPath: String): BudgetMetadata? {
        return try {
            val metadataBytes = fileManager.readFile(metadataPath)
            if (metadataBytes != null) {
                val metadataJson = metadataBytes.decodeToString()
                println("[BudgetExporter] Found existing metadata.json at $metadataPath")
                json.decodeFromString<BudgetMetadata>(metadataJson)
            } else {
                println("[BudgetExporter] No existing metadata.json at $metadataPath")
                null
            }
        } catch (e: Exception) {
            println("[BudgetExporter] Failed to read existing metadata: ${e.message}")
            null
        }
    }

    /**
     * Prepare a budget for upload to server.
     *
     * If a metadata.json file exists next to the database, its `id` will be preserved
     * to maintain consistency with the server. Otherwise, a new ID is generated.
     *
     * @param budgetId Cloud file ID (UUID) for the budget
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
        // Try to read existing metadata.json to preserve the original ID
        val existingMetadataPath = dbPath.substringBeforeLast("/") + "/metadata.json"
        val existingMetadata = readExistingMetadata(existingMetadataPath)

        // Use existing ID if available, otherwise generate a new one
        val budgetLocalId = existingMetadata?.id ?: generateBudgetId(budgetName)
        println("[BudgetExporter] Using local ID: $budgetLocalId (from existing: ${existingMetadata != null})")
        val tempDir = fileManager.getTempDir()
        val workDirName = "budget_export_$budgetId"
        val workDir = "$tempDir/$workDirName"
        val tempDbPath = "$workDir/db.sqlite"
        val metadataPath = "$workDir/metadata.json"
        val zipPath = "$workDir/budget.zip"

        try {
            println("[BudgetExporter] Preparing budget for upload: $budgetId")
            println("[BudgetExporter] Source database path: $dbPath")
            println("[BudgetExporter] Work directory: $workDir")

            // Step 1: Create work directory and copy database
            if (fileManager.exists(workDir)) {
                fileManager.delete(workDir)
            }

            // Create work directory using KmpIO
            val workDirFile = File(workDir)
            workDirFile.makeDirectory()
            println("[BudgetExporter] Created work directory")

            // Verify source exists before copy
            if (!fileManager.exists(dbPath)) {
                throw Exception("Source database does not exist at: $dbPath")
            }

            // Copy the database file
            // Note: iOS now uses DELETE journal mode (not WAL) so no extra files needed
            if (!fileManager.copy(dbPath, tempDbPath)) {
                throw Exception("Failed to copy database to temp location")
            }
            println("[BudgetExporter] Copied database to $tempDbPath")

            // Step 2: Clear cache tables in the temp database (if they exist)
            // These tables are used by Actual's web app but may not exist in ActualSync
            clearCacheTables(tempDbPath)
            println("[BudgetExporter] Processed cache tables")

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
                id = budgetLocalId,
                budgetName = budgetName,
                resetClock = true,
                cloudFileId = budgetId,
                groupId = groupId,
                lastUploaded = timestamp,
                encryptKeyId = encryptKeyId
            )
            println("[BudgetExporter] Metadata: id=$budgetLocalId, cloudFileId=$budgetId, groupId=$groupId")

            val metadataJson = json.encodeToString(metadata)
            if (!fileManager.writeFile(metadataPath, metadataJson.encodeToByteArray())) {
                throw Exception("Failed to write metadata.json")
            }
            println("[BudgetExporter] Created metadata.json")

            // Step 4: Create ZIP with both files using KmpIO
            val zipFile = File(zipPath)
            val dbFile = File(tempDbPath)
            val metadataFile = File(metadataPath)

            // Debug: check file sizes before zipping
            println("[BudgetExporter] DB file exists: ${dbFile.exists}, size: ${if (dbFile.exists) dbFile.size else 0}")
            println("[BudgetExporter] Metadata file exists: ${metadataFile.exists}, size: ${if (metadataFile.exists) metadataFile.size else 0}")

            ZipFile(zipFile, FileMode.Write).use { zip ->
                // Explicitly specify entry names to ensure they're at ZIP root
                zip.zipFile(dbFile, "db.sqlite")
                zip.zipFile(metadataFile, "metadata.json")
            }
            println("[BudgetExporter] Created ZIP file at $zipPath")

            // Step 5: Read ZIP as ByteArray and validate
            val zipData = fileManager.readFile(zipPath)
                ?: throw Exception("Failed to read ZIP file")
            println("[BudgetExporter] Read ZIP file (${zipData.size} bytes)")

            // Sanity check: ZIP should be at least as big as the db file
            val expectedMinSize = if (dbFile.exists) dbFile.size.toLong() else 0L
            if (zipData.size < expectedMinSize / 2) {
                println("[BudgetExporter] WARNING: ZIP size (${zipData.size}) is much smaller than DB size ($expectedMinSize)")
                println("[BudgetExporter] ZIP may not contain the database properly!")
            }

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
 * Checkpoint WAL to flush all data from WAL file to main database file.
 * This is needed before copying the database because iOS uses WAL mode.
 * Platform-specific implementation due to different SQLite drivers.
 */
expect fun checkpointDatabase(dbPath: String)

/**
 * Clear kvcache and kvcache_key tables in the database.
 * Platform-specific implementation due to different SQLite drivers.
 */
expect fun clearCacheTables(dbPath: String)
