package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.Merkle
import com.actualbudget.sync.crdt.MutableClock
import com.actualbudget.sync.crdt.Timestamp
import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.io.BudgetFileManager
import com.actualbudget.sync.io.BudgetMetadata
import kotlinx.serialization.json.Json
import com.actualbudget.sync.proto.SyncRequest
import com.actualbudget.sync.proto.SyncResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Details about a single pending change.
 */
data class PendingChangeDetail(
    val dataset: String,
    val rowId: String,
    val column: String,
    val value: String,
    val timestamp: String
)

/**
 * High-level sync manager that coordinates sync operations.
 */
class SyncManager(
    private val serverUrl: String,
    private val httpClient: HttpClient,
    private val database: ActualDatabase
) {
    private var token: String? = null
    private var fileId: String? = null
    private var groupId: String? = null

    private lateinit var clock: MutableClock
    private lateinit var engine: SyncEngine

    /**
     * Initialize the sync manager with a client ID.
     */
    fun initialize(clientId: String? = null) {
        val nodeId = clientId ?: Timestamp.makeClientId()

        // Try to load clock state from database
        val savedMillis = database.actualDatabaseQueries.getSyncMetadata("clock_millis")
            .executeAsOneOrNull()?.value_?.toLongOrNull() ?: 0L
        val savedCounter = database.actualDatabaseQueries.getSyncMetadata("clock_counter")
            .executeAsOneOrNull()?.value_?.toIntOrNull() ?: 0
        val savedNode = database.actualDatabaseQueries.getSyncMetadata("clock_node")
            .executeAsOneOrNull()?.value_ ?: nodeId

        clock = MutableClock(millis = savedMillis, counter = savedCounter, node = savedNode)
        engine = SyncEngine(database, clock)
        engine.initialize()
    }

    /**
     * Save clock state to database.
     */
    private fun saveClockState() {
        database.actualDatabaseQueries.setSyncMetadata("clock_millis", clock.millis.toString())
        database.actualDatabaseQueries.setSyncMetadata("clock_counter", clock.counter.toString())
        database.actualDatabaseQueries.setSyncMetadata("clock_node", clock.node)
    }

    /**
     * Set authentication token.
     */
    fun setToken(token: String) {
        this.token = token
    }

    /**
     * Set the current budget file.
     */
    fun setBudget(fileId: String, groupId: String) {
        this.fileId = fileId
        this.groupId = groupId
    }

    /**
     * Perform a full sync from scratch.
     * Downloads all messages from the server.
     */
    suspend fun fullSync(): SyncResult {
        requireNotNull(fileId) { "Budget not set. Call setBudget() first." }
        requireNotNull(groupId) { "Budget not set. Call setBudget() first." }

        val request = engine.buildSyncRequest(fileId!!, groupId!!, fullSync = true)
        return performSync(request)
    }

    /**
     * Download the complete budget database from the server.
     * This downloads the full SQLite database, not just sync messages.
     * Use this when first opening a budget or after a sync reset.
     *
     * @param fileId The budget file ID to download
     * @return The raw zip file bytes, or null if download failed
     */
    suspend fun downloadBudgetFile(fileId: String): ByteArray? {
        return try {
            println("[SyncManager] Downloading budget file: $fileId")

            val response = httpClient.get("$serverUrl/sync/download-user-file") {
                token?.let { header("X-ACTUAL-TOKEN", it) }
                header("X-ACTUAL-FILE-ID", fileId)
            }

            if (response.status.isSuccess()) {
                val bytes = response.readRawBytes()
                println("[SyncManager] Downloaded ${bytes.size} bytes")
                bytes
            } else {
                println("[SyncManager] Download failed: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("[SyncManager] Download error: ${e.message}")
            null
        }
    }

    /**
     * Extract a downloaded budget zip and install the database.
     * Also copies metadata.json alongside the database for future exports.
     *
     * @param zipData The raw zip file bytes from downloadBudgetFile()
     * @param targetDbPath The path where the database should be installed
     * @return true if extraction and installation succeeded
     */
    fun extractAndInstallBudget(zipData: ByteArray, targetDbPath: String): Boolean {
        return try {
            val fileManager = BudgetFileManager()
            val tempDir = fileManager.getDefaultBudgetDir() + "/temp_extract"

            println("[SyncManager] Extracting budget to temp dir: $tempDir")
            println("[SyncManager] Zip data size: ${zipData.size} bytes")

            // Extract the zip (extracts both db.sqlite and metadata.json)
            val extractedDbPath = fileManager.extractBudgetZip(zipData, tempDir)
            if (extractedDbPath == null) {
                println("[SyncManager] Failed to extract db.sqlite from zip")
                return false
            }

            println("[SyncManager] Extracted db.sqlite at: $extractedDbPath")
            println("[SyncManager] Installing to: $targetDbPath")

            // Delete existing file first to ensure clean install
            if (fileManager.exists(targetDbPath)) {
                println("[SyncManager] Deleting existing database at: $targetDbPath")
                fileManager.delete(targetDbPath)
                // Also delete WAL and SHM files
                fileManager.delete("$targetDbPath-wal")
                fileManager.delete("$targetDbPath-shm")
            }

            // Copy database to target location
            val success = fileManager.copy(extractedDbPath, targetDbPath)

            if (success) {
                println("[SyncManager] Budget installed successfully")
                // Verify the file exists and check size
                if (fileManager.exists(targetDbPath)) {
                    println("[SyncManager] Verified: target file exists")
                } else {
                    println("[SyncManager] ERROR: target file does not exist after copy!")
                }

                // Also copy metadata.json if it was extracted
                val extractedMetadataPath = "$tempDir/metadata.json"
                if (fileManager.exists(extractedMetadataPath)) {
                    // Derive target metadata path from db path (same directory)
                    val targetDir = targetDbPath.substringBeforeLast("/")
                    val targetMetadataPath = "$targetDir/metadata.json"

                    // Delete existing metadata
                    if (fileManager.exists(targetMetadataPath)) {
                        fileManager.delete(targetMetadataPath)
                    }

                    if (fileManager.copy(extractedMetadataPath, targetMetadataPath)) {
                        println("[SyncManager] Copied metadata.json to $targetMetadataPath")
                    } else {
                        println("[SyncManager] Warning: Failed to copy metadata.json (non-fatal)")
                    }
                } else {
                    println("[SyncManager] No metadata.json found in zip (older format?)")
                }
            } else {
                println("[SyncManager] Failed to copy database to target path")
            }

            // Clean up temp directory
            fileManager.delete(tempDir)

            success
        } catch (e: Exception) {
            println("[SyncManager] Error installing budget: ${e.message}")
            false
        }
    }

    /**
     * Download and install a budget in one step.
     * After calling this, you should reinitialize the database and SyncManager.
     *
     * @param fileId The budget file ID
     * @param targetDbPath The path where the database should be installed
     * @return true if download and installation succeeded
     */
    suspend fun downloadAndInstallBudget(fileId: String, targetDbPath: String): Boolean {
        val zipData = downloadBudgetFile(fileId)
        if (zipData == null) {
            println("[SyncManager] Failed to download budget")
            return false
        }

        return extractAndInstallBudget(zipData, targetDbPath)
    }

    // ============ Multi-Budget Support ============

    /**
     * Download and install a budget to the multi-budget folder structure.
     * The budget will be installed to: {budgetsDirectory}/{budgetId}/db.sqlite
     *
     * @param fileId The server file ID to download
     * @return The budget folder path if successful, null otherwise
     */
    suspend fun downloadAndInstallBudgetToFolder(fileId: String): String? {
        val zipData = downloadBudgetFile(fileId)
        if (zipData == null) {
            println("[SyncManager] Failed to download budget")
            return null
        }

        return extractAndInstallBudgetToFolder(zipData)
    }

    /**
     * Extract a budget zip and install it to the multi-budget folder structure.
     * Reads the budget ID from metadata.json and creates the folder structure.
     *
     * @param zipData The raw zip file bytes
     * @return The budget folder path if successful, null otherwise
     */
    fun extractAndInstallBudgetToFolder(zipData: ByteArray): String? {
        return try {
            val fileManager = BudgetFileManager()
            val tempDir = fileManager.getTempDir() + "/budget_extract_temp"

            println("[SyncManager] Extracting budget to temp dir: $tempDir")

            // Clean up temp dir if it exists
            if (fileManager.exists(tempDir)) {
                fileManager.delete(tempDir)
            }

            // Extract the zip to temp directory
            val extractedDbPath = fileManager.extractBudgetZip(zipData, tempDir)
            if (extractedDbPath == null) {
                println("[SyncManager] Failed to extract db.sqlite from zip")
                return null
            }

            // Read metadata to get budget ID
            val metadataPath = "$tempDir/metadata.json"
            val metadataBytes = fileManager.readFile(metadataPath)
            if (metadataBytes == null) {
                println("[SyncManager] No metadata.json found in zip")
                fileManager.delete(tempDir)
                return null
            }

            var metadataJson = metadataBytes.decodeToString()
            val budgetId = extractBudgetIdFromMetadata(metadataJson)
            if (budgetId == null) {
                println("[SyncManager] Failed to extract budget ID from metadata")
                fileManager.delete(tempDir)
                return null
            }

            println("[SyncManager] Budget ID from metadata: $budgetId")

            // Create budget folder
            val budgetFolder = fileManager.createBudgetFolder(budgetId)
            if (budgetFolder == null) {
                println("[SyncManager] Failed to create budget folder")
                fileManager.delete(tempDir)
                return null
            }

            val targetDbPath = "$budgetFolder/db.sqlite"
            val targetMetadataPath = "$budgetFolder/metadata.json"

            // Delete existing files
            if (fileManager.exists(targetDbPath)) {
                fileManager.delete(targetDbPath)
                fileManager.delete("$targetDbPath-wal")
                fileManager.delete("$targetDbPath-shm")
            }
            if (fileManager.exists(targetMetadataPath)) {
                fileManager.delete(targetMetadataPath)
            }

            // Copy files to budget folder
            val dbCopied = fileManager.copy(extractedDbPath, targetDbPath)
            val metadataCopied = fileManager.copy(metadataPath, targetMetadataPath)

            // Clean up temp directory
            fileManager.delete(tempDir)

            if (dbCopied && metadataCopied) {
                println("[SyncManager] Budget installed to folder: $budgetFolder")
                budgetFolder
            } else {
                println("[SyncManager] Failed to copy files to budget folder")
                null
            }
        } catch (e: Exception) {
            println("[SyncManager] Error installing budget to folder: ${e.message}")
            null
        }
    }

    /**
     * Get the path to a budget's database file.
     *
     * @param budgetId The budget ID (folder name)
     * @return Path to the db.sqlite file
     */
    fun getBudgetDbPath(budgetId: String): String {
        val fileManager = BudgetFileManager()
        return "${fileManager.getBudgetsDirectory()}/$budgetId/db.sqlite"
    }

    /**
     * Get the path to a budget's metadata file.
     *
     * @param budgetId The budget ID (folder name)
     * @return Path to the metadata.json file
     */
    fun getBudgetMetadataPath(budgetId: String): String {
        val fileManager = BudgetFileManager()
        return "${fileManager.getBudgetsDirectory()}/$budgetId/metadata.json"
    }

    /**
     * Extract budget ID from metadata JSON string.
     */
    private fun extractBudgetIdFromMetadata(json: String): String? {
        return try {
            val metadata = Json { ignoreUnknownKeys = true }.decodeFromString<BudgetMetadata>(json)
            metadata.id
        } catch (e: Exception) {
            println("[SyncManager] Failed to parse metadata JSON: ${e.message}")
            null
        }
    }

    /**
     * Perform an incremental sync.
     * Only syncs changes since last sync point.
     */
    suspend fun sync(): SyncResult {
        requireNotNull(fileId) { "Budget not set. Call setBudget() first." }
        requireNotNull(groupId) { "Budget not set. Call setBudget() first." }

        val serverMerkle = engine.getServerMerkle()

        val request = if (serverMerkle != null) {
            engine.buildIncrementalSyncRequest(fileId!!, groupId!!, serverMerkle)
        } else {
            // First sync, do full
            engine.buildSyncRequest(fileId!!, groupId!!, fullSync = true)
        }

        return performSync(request)
    }

    /**
     * Perform the actual sync HTTP request.
     */
    private suspend fun performSync(request: SyncRequest): SyncResult {
        return try {
            val requestBytes = request.encode()

            val response = httpClient.post("$serverUrl/sync/sync") {
                token?.let { header("X-ACTUAL-TOKEN", it) }
                header("X-ACTUAL-FILE-ID", fileId)
                contentType(ContentType("application", "actual-sync"))
                setBody(requestBytes)
            }

            if (response.status.isSuccess()) {
                val responseBytes = response.readRawBytes()
                val syncResponse = SyncResponse.decode(responseBytes)

                val applied = engine.processSyncResponse(syncResponse)
                saveClockState()

                SyncResult.Success(
                    messagesSent = request.messages.size,
                    messagesReceived = syncResponse.messages.size,
                    messagesApplied = applied
                )
            } else {
                SyncResult.Error("Sync failed: ${response.status}")
            }
        } catch (e: Exception) {
            SyncResult.Error("Sync error: ${e.message}")
        }
    }

    // ========== Local Change Methods ==========

    /**
     * Create a new account locally.
     */
    fun createAccount(id: String, name: String, offbudget: Boolean = false): String {
        engine.createChange("accounts", id, "name", name)
        engine.createChange("accounts", id, "offbudget", if (offbudget) 1 else 0)
        engine.createChange("accounts", id, "closed", 0)
        engine.createChange("accounts", id, "tombstone", 0)
        return id
    }

    /**
     * Update an account field.
     */
    fun updateAccount(id: String, field: String, value: Any?) {
        engine.createChange("accounts", id, field, value)
    }

    /**
     * Delete an account (set tombstone).
     */
    fun deleteAccount(id: String) {
        engine.createChange("accounts", id, "tombstone", 1)
    }

    /**
     * Create a new payee locally.
     * Also creates the required payee_mapping entry (id -> id) for the payee to be usable.
     */
    fun createPayee(id: String, name: String): String {
        engine.createChange("payees", id, "name", name)
        engine.createChange("payees", id, "tombstone", 0)
        // Actual Budget requires a payee_mapping entry for each payee
        // The mapping points to itself (id -> id) for regular payees
        // NOTE: payee_mapping table has NO tombstone column in Actual Budget
        engine.createChange("payee_mapping", id, "targetId", id)
        return id
    }

    /**
     * Update a payee field.
     */
    fun updatePayee(id: String, field: String, value: Any?) {
        engine.createChange("payees", id, field, value)
    }

    /**
     * Delete a payee (set tombstone).
     */
    fun deletePayee(id: String) {
        engine.createChange("payees", id, "tombstone", 1)
    }

    /**
     * Create a new category locally.
     *
     * @param id Category ID
     * @param name Category name
     * @param groupId Parent category group ID
     * @param isIncome Whether this is an income category (default: false)
     * @param sortOrder Optional sort order for display ordering
     * @param hidden Whether the category is hidden (default: false)
     */
    fun createCategory(
        id: String,
        name: String,
        groupId: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String {
        engine.createChange("categories", id, "name", name)
        engine.createChange("categories", id, "cat_group", groupId)
        engine.createChange("categories", id, "is_income", if (isIncome) 1 else 0)
        if (sortOrder != null) {
            engine.createChange("categories", id, "sort_order", sortOrder)
        }
        engine.createChange("categories", id, "hidden", if (hidden) 1 else 0)
        engine.createChange("categories", id, "tombstone", 0)
        return id
    }

    /**
     * Update a category field.
     */
    fun updateCategory(id: String, field: String, value: Any?) {
        engine.createChange("categories", id, field, value)
    }

    /**
     * Delete a category (set tombstone).
     */
    fun deleteCategory(id: String) {
        engine.createChange("categories", id, "tombstone", 1)
    }

    /**
     * Create a new category group locally.
     *
     * @param id Category group ID
     * @param name Category group name
     * @param isIncome Whether this is an income group (default: false for expense)
     * @param sortOrder Optional sort order for display ordering
     * @param hidden Whether the group is hidden (default: false)
     */
    fun createCategoryGroup(
        id: String,
        name: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String {
        engine.createChange("category_groups", id, "name", name)
        engine.createChange("category_groups", id, "is_income", if (isIncome) 1 else 0)
        if (sortOrder != null) {
            engine.createChange("category_groups", id, "sort_order", sortOrder)
        }
        engine.createChange("category_groups", id, "hidden", if (hidden) 1 else 0)
        engine.createChange("category_groups", id, "tombstone", 0)
        return id
    }

    /**
     * Update a category group field.
     */
    fun updateCategoryGroup(id: String, field: String, value: Any?) {
        engine.createChange("category_groups", id, field, value)
    }

    /**
     * Delete a category group (set tombstone).
     */
    fun deleteCategoryGroup(id: String) {
        engine.createChange("category_groups", id, "tombstone", 1)
    }

    // ========== Budget Methods ==========

    /**
     * Set the budget amount for a category in a specific month.
     * This is the primary method for setting budget amounts.
     *
     * @param categoryId The category ID to set budget for
     * @param month The month in YYYYMM format (e.g., 202501 for January 2025)
     * @param amount The budget amount in cents (e.g., 50000 for $500.00)
     * @throws IllegalArgumentException if month format is invalid or categoryId is blank
     */
    fun setBudgetAmount(categoryId: String, month: Long, amount: Long) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)

        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "amount", amount)
    }

    /**
     * Set a budget goal for a category in a specific month.
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param goal The goal amount in cents (or null to clear)
     */
    fun setBudgetGoal(categoryId: String, month: Long, goal: Long?) {
        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "goal", goal)
    }

    /**
     * Set the carryover amount for a category in a specific month.
     * Carryover is used for envelope budgeting to carry unused funds to next month.
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param carryover The carryover amount in cents
     */
    fun setBudgetCarryover(categoryId: String, month: Long, carryover: Long) {
        val budgetId = "$month-$categoryId"
        engine.createChange("zero_budgets", budgetId, "month", month)
        engine.createChange("zero_budgets", budgetId, "category", categoryId)
        engine.createChange("zero_budgets", budgetId, "carryover", carryover)
    }

    /**
     * Copy budget amounts from previous month to current month.
     * Useful for quickly setting up a new month's budget.
     *
     * @param targetMonth The month to copy TO in YYYYMM format
     */
    fun copyBudgetFromPreviousMonth(targetMonth: Long) {
        // Calculate previous month
        val year = (targetMonth / 100).toInt()
        val month = (targetMonth % 100).toInt()
        val prevMonth = if (month == 1) {
            ((year - 1) * 100 + 12).toLong()
        } else {
            (year * 100 + month - 1).toLong()
        }

        // Get budgets from previous month
        val prevBudgets = database.actualDatabaseQueries.getBudgetForMonth(prevMonth).executeAsList()

        // Copy each budget to target month
        for (budget in prevBudgets) {
            if (budget.tombstone == 0L) {
                setBudgetAmount(budget.category, targetMonth, budget.amount)
            }
        }
    }

    /**
     * Set all budget amounts for a month to zero.
     *
     * @param month The month in YYYYMM format
     */
    fun zeroBudgetsForMonth(month: Long) {
        val budgets = database.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()
        for (budget in budgets) {
            if (budget.tombstone == 0L) {
                setBudgetAmount(budget.category, month, 0)
            }
        }
    }

    /**
     * Transfer budget amount from one category to another within the same month.
     *
     * @param fromCategoryId Source category ID
     * @param toCategoryId Destination category ID
     * @param month The month in YYYYMM format
     * @param amount Amount to transfer in cents
     */
    fun transferBudget(fromCategoryId: String, toCategoryId: String, month: Long, amount: Long) {
        BudgetUtils.requireValidId(fromCategoryId, "fromCategoryId")
        BudgetUtils.requireValidId(toCategoryId, "toCategoryId")
        BudgetUtils.requireValidMonth(month)

        // Get current budgets
        val fromBudgetId = "$month-$fromCategoryId"
        val toBudgetId = "$month-$toCategoryId"

        val fromBudget = database.actualDatabaseQueries.getBudgetById(fromBudgetId).executeAsOneOrNull()
        val toBudget = database.actualDatabaseQueries.getBudgetById(toBudgetId).executeAsOneOrNull()

        val fromAmount = fromBudget?.amount ?: 0L
        val toAmount = toBudget?.amount ?: 0L

        // Update both categories
        setBudgetAmount(fromCategoryId, month, fromAmount - amount)
        setBudgetAmount(toCategoryId, month, toAmount + amount)
    }

    // ========== Reordering Methods ==========

    /**
     * Move a category to a new position within a group or to a different group.
     *
     * @param categoryId The category to move
     * @param newGroupId The target group (can be same as current)
     * @param targetCategoryId The category to insert before, or null to append at end
     */
    fun moveCategory(categoryId: String, newGroupId: String, targetCategoryId: String?) {
        // Get categories in target group
        val categoriesInGroup = database.actualDatabaseQueries
            .getCategoriesInGroupOrdered(newGroupId)
            .executeAsList()
            .filter { it.id != categoryId } // Exclude the category being moved
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(categoriesInGroup, targetCategoryId)

        // Update the category (keep as Double to preserve precision)
        engine.createChange("categories", categoryId, "sort_order", newSortOrder)
        engine.createChange("categories", categoryId, "cat_group", newGroupId)
    }

    /**
     * Move a category group to a new position.
     *
     * @param groupId The category group to move
     * @param targetGroupId The group to insert before, or null to append at end
     */
    fun moveCategoryGroup(groupId: String, targetGroupId: String?) {
        // Get all category groups
        val groups = database.actualDatabaseQueries
            .getCategoryGroupsOrdered()
            .executeAsList()
            .filter { it.id != groupId } // Exclude the group being moved
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(groups, targetGroupId)

        // Update the group (keep as Double to preserve precision)
        engine.createChange("category_groups", groupId, "sort_order", newSortOrder)
    }

    /**
     * Move an account to a new position within its category (on-budget, off-budget, or closed).
     *
     * @param accountId The account to move
     * @param targetAccountId The account to insert before, or null to append at end
     */
    fun moveAccount(accountId: String, targetAccountId: String?) {
        // Get the account to determine its type
        val account = database.actualDatabaseQueries.getAccountById(accountId).executeAsOneOrNull()
            ?: return

        // Get accounts of same type
        val accounts = when {
            account.closed == 1L -> database.actualDatabaseQueries.getClosedAccountsOrdered().executeAsList()
            account.offbudget == 1L -> database.actualDatabaseQueries.getOffBudgetAccountsOrdered().executeAsList()
            else -> database.actualDatabaseQueries.getOnBudgetAccountsOrdered().executeAsList()
        }
            .filter { it.id != accountId }
            .map { it.id to it.sort_order }

        // Calculate new sort order
        val newSortOrder = BudgetUtils.calculateNewSortOrder(accounts, targetAccountId)

        // Update the account (keep as Double to preserve precision)
        engine.createChange("accounts", accountId, "sort_order", newSortOrder)
    }

    /**
     * Reopen a closed account.
     *
     * @param accountId The account to reopen
     */
    fun reopenAccount(accountId: String) {
        engine.createChange("accounts", accountId, "closed", 0)
    }

    // ========== Advanced Budget Methods ==========

    /**
     * Copy budget from the previous month for a single category.
     *
     * @param categoryId The category to copy budget for
     * @param month The target month in YYYYMM format
     * @throws IllegalArgumentException if parameters are invalid
     */
    fun copySinglePreviousMonth(categoryId: String, month: Long) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)

        val prevMonth = BudgetUtils.calculatePreviousMonth(month)
        val prevBudgetId = "$prevMonth-$categoryId"
        val prevBudget = database.actualDatabaseQueries.getBudgetById(prevBudgetId).executeAsOneOrNull()
        val amount = prevBudget?.amount ?: 0L
        setBudgetAmount(categoryId, month, amount)
    }

    /**
     * Set the budget for a category based on N-month spending average.
     *
     * @param categoryId The category to set budget for
     * @param month The target month in YYYYMM format
     * @param n Number of months to average (must be positive)
     * @throws IllegalArgumentException if parameters are invalid
     */
    fun setNMonthAverage(categoryId: String, month: Long, n: Int) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)
        BudgetUtils.requirePositive(n, "n (months to average)")

        val (startDate, endDate) = BudgetUtils.calculateDateRangeForPreviousMonths(month, n)
        val spentData = database.actualDatabaseQueries
            .getSpentByCategoryForMonths(startDate, endDate)
            .executeAsList()

        val categoryData = spentData.find { it.category == categoryId }
        val totalSpent: Long = categoryData?.total_spent?.toLong() ?: 0L

        // Get category to check if it's income
        val category = database.actualDatabaseQueries.getCategoryById(categoryId).executeAsOneOrNull()

        // Calculate average with proper rounding (not truncation)
        // Using round() ensures we don't systematically lose cents
        var avg: Long = kotlin.math.round(totalSpent.toDouble() / n).toLong()

        // For expense categories, negate to make budget positive
        if (category?.is_income == 0L) {
            avg = -avg
        }

        setBudgetAmount(categoryId, month, avg)
    }

    /**
     * Set 3-month average budget for all visible categories.
     *
     * @param month The target month in YYYYMM format
     */
    fun set3MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 3)

    /**
     * Set 6-month average budget for all visible categories.
     *
     * @param month The target month in YYYYMM format
     */
    fun set6MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 6)

    /**
     * Set 12-month average budget for all visible categories.
     *
     * @param month The target month in YYYYMM format
     */
    fun set12MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 12)

    /**
     * Helper to set N-month average for all visible expense categories.
     */
    private fun setAllCategoriesNMonthAverage(month: Long, n: Int) {
        val categories = database.actualDatabaseQueries.getCategories().executeAsList()
            .filter { it.hidden == 0L && it.tombstone == 0L && it.is_income == 0L }

        for (category in categories) {
            setNMonthAverage(category.id, month, n)
        }
    }

    // ========== Hold Operations ==========

    /**
     * Calculate the "To Budget" amount for a month.
     * This is: total income - total budgeted + previous month carryover - current buffered.
     *
     * @param month The month in YYYYMM format
     * @return The available amount to budget
     */
    fun calculateToBudget(month: Long): Long {
        // Get total income for the month
        val totalIncome = database.actualDatabaseQueries
            .getTotalIncomeForMonth(month)
            .executeAsOne()
            .toLong()

        // Get total budgeted for the month
        val totalBudgeted = database.actualDatabaseQueries
            .getTotalBudgetedForMonth(month)
            .executeAsOne()
            .toLong()

        // Get current buffered amount
        val monthId = month.toString()
        val budgetMonth = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        val buffered = budgetMonth?.buffered ?: 0L

        // Calculate to-budget (simplified - doesn't include previous month carryover for now)
        return totalIncome - totalBudgeted - buffered
    }

    /**
     * Hold money for next month.
     * Sets aside part of the "To Budget" amount to be available next month.
     *
     * @param month The month in YYYYMM format
     * @param amount The amount to hold (will be constrained to available)
     * @return true if successful, false if not enough money to hold
     */
    fun holdForNextMonth(month: Long, amount: Long): Boolean {
        val monthId = month.toString()
        val current = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        val currentBuffered = current?.buffered ?: 0L

        // Calculate available to-budget
        val toBudget = calculateToBudget(month)

        if (toBudget <= 0) return false

        // Constrain amount: not negative, not more than available
        val actualAmount = maxOf(0L, minOf(amount, toBudget))
        val newBuffered = currentBuffered + actualAmount

        engine.createChange("zero_budget_months", monthId, "buffered", newBuffered)
        return true
    }

    /**
     * Reset the hold amount for a month.
     *
     * @param month The month in YYYYMM format
     */
    fun resetHold(month: Long) {
        engine.createChange("zero_budget_months", month.toString(), "buffered", 0)
    }

    // ========== Cover Operations ==========

    /**
     * Calculate the leftover (available) amount for a category in a month.
     * Leftover = budgeted + spent (spent is negative for expenses).
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @return The available/leftover amount (negative if overspent)
     */
    fun calculateCategoryLeftover(categoryId: String, month: Long): Long {
        val budgetId = "$month-$categoryId"
        val budget = database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()
        val budgeted = budget?.amount ?: 0L

        val (startDate, endDate) = BudgetUtils.monthToDateRange(month)
        val spentData = database.actualDatabaseQueries
            .getSpentByCategory(startDate, endDate)
            .executeAsList()

        val spentResult = spentData.find { it.category == categoryId }
        val spent: Long = spentResult?.spent?.toLong() ?: 0L

        return budgeted + spent // spent is negative for expenses
    }

    /**
     * Cover overspending in one category from another.
     * Transfers budget from a category with available funds to cover overspending.
     *
     * @param fromCategoryId Category with available funds
     * @param toCategoryId Overspent category to cover
     * @param month The month in YYYYMM format
     * @param amount Amount to cover (or null to cover full overspending)
     */
    fun coverOverspending(
        fromCategoryId: String,
        toCategoryId: String,
        month: Long,
        amount: Long? = null
    ) {
        val toLeftover = calculateCategoryLeftover(toCategoryId, month)
        val fromLeftover = calculateCategoryLeftover(fromCategoryId, month)

        // Only proceed if 'to' is overspent and 'from' has funds
        if (toLeftover >= 0 || fromLeftover <= 0) return

        val overspentAmount = -toLeftover // Make positive
        val amountToCover = amount ?: overspentAmount
        val coverableAmount = minOf(amountToCover, fromLeftover, overspentAmount)

        // Transfer budget from -> to
        val fromBudgetId = "$month-$fromCategoryId"
        val toBudgetId = "$month-$toCategoryId"
        val fromBudget = database.actualDatabaseQueries.getBudgetById(fromBudgetId).executeAsOneOrNull()?.amount ?: 0L
        val toBudget = database.actualDatabaseQueries.getBudgetById(toBudgetId).executeAsOneOrNull()?.amount ?: 0L

        setBudgetAmount(fromCategoryId, month, fromBudget - coverableAmount)
        setBudgetAmount(toCategoryId, month, toBudget + coverableAmount)
    }

    /**
     * Transfer available "To Budget" money to a category.
     *
     * @param amount Amount to transfer
     * @param toCategoryId Target category
     * @param month The month in YYYYMM format
     */
    fun transferAvailable(amount: Long, toCategoryId: String, month: Long) {
        val toBudget = calculateToBudget(month)
        val actualAmount = maxOf(0L, minOf(amount, toBudget))

        val budgetId = "$month-$toCategoryId"
        val currentBudget = database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()?.amount ?: 0L
        setBudgetAmount(toCategoryId, month, currentBudget + actualAmount)
    }

    /**
     * Create a new transaction locally.
     *
     * @param id Transaction ID
     * @param accountId Account ID
     * @param date Date as YYYYMMDD integer
     * @param amount Amount in cents (negative for expenses)
     * @param payeeId Optional payee ID
     * @param categoryId Optional category ID
     * @param notes Optional notes
     */
    fun createTransaction(
        id: String,
        accountId: String,
        date: Int,
        amount: Long,
        payeeId: String? = null,
        categoryId: String? = null,
        notes: String? = null
    ): String {
        engine.createChange("transactions", id, "acct", accountId)
        engine.createChange("transactions", id, "date", date)
        engine.createChange("transactions", id, "amount", amount)
        // Database column is 'description' which maps to 'payee' in the public API
        if (payeeId != null) engine.createChange("transactions", id, "description", payeeId)
        if (categoryId != null) engine.createChange("transactions", id, "category", categoryId)
        if (notes != null) engine.createChange("transactions", id, "notes", notes)
        engine.createChange("transactions", id, "cleared", 0)
        engine.createChange("transactions", id, "tombstone", 0)
        return id
    }

    /**
     * Update a transaction field.
     * Field names are normalized to match database schema:
     * - "payee" -> "description"
     * - "account" -> "acct"
     */
    fun updateTransaction(id: String, field: String, value: Any?) {
        val normalizedField = normalizeTransactionField(field)
        engine.createChange("transactions", id, normalizedField, value)
    }

    /**
     * Normalize transaction field names to match database schema.
     * The public API uses friendly names but the database uses different column names.
     */
    private fun normalizeTransactionField(field: String): String {
        return when (field) {
            "payee" -> "description"  // Database stores payee ID in 'description' column
            "account" -> "acct"       // Database uses 'acct' for account ID
            else -> field
        }
    }

    /**
     * Delete a transaction (set tombstone).
     */
    fun deleteTransaction(id: String) {
        engine.createChange("transactions", id, "tombstone", 1)
    }

    /**
     * Get the number of pending changes.
     */
    fun getPendingChangeCount(): Int = engine.getPendingMessages().size

    /**
     * Get a summary of pending changes grouped by type.
     * Returns a list of human-readable descriptions.
     */
    fun getPendingChangeSummary(): List<String> {
        val messages = engine.getPendingMessages()
        if (messages.isEmpty()) return emptyList()

        // Group by dataset and row to get unique entities changed
        val changesByDataset = mutableMapOf<String, MutableSet<String>>()

        for (envelope in messages) {
            if (!envelope.isEncrypted) {
                try {
                    val message = envelope.decodeMessage()
                    val dataset = message.dataset
                    val rowId = message.row
                    changesByDataset.getOrPut(dataset) { mutableSetOf() }.add(rowId)
                } catch (_: Exception) {
                    // Skip malformed messages
                }
            }
        }

        // Build human-readable summary
        val summary = mutableListOf<String>()
        for ((dataset, rowIds) in changesByDataset) {
            val count = rowIds.size
            val entityName = when (dataset) {
                "transactions" -> if (count == 1) "transaction" else "transactions"
                "accounts" -> if (count == 1) "account" else "accounts"
                "payees" -> if (count == 1) "payee" else "payees"
                "payee_mapping" -> if (count == 1) "payee mapping" else "payee mappings"
                "categories" -> if (count == 1) "category" else "categories"
                "category_groups" -> if (count == 1) "category group" else "category groups"
                "zero_budgets" -> if (count == 1) "budget" else "budgets"
                else -> dataset
            }
            summary.add("$count $entityName")
        }

        return summary
    }

    /**
     * Get detailed pending changes for debugging/display.
     * Returns a list of change descriptions with dataset, column, and value info.
     */
    fun getPendingChangeDetails(): List<PendingChangeDetail> {
        val messages = engine.getPendingMessages()
        val details = mutableListOf<PendingChangeDetail>()

        for (envelope in messages) {
            if (!envelope.isEncrypted) {
                try {
                    val message = envelope.decodeMessage()
                    details.add(
                        PendingChangeDetail(
                            dataset = message.dataset,
                            rowId = message.row,
                            column = message.column,
                            value = parseDisplayValue(message.value),
                            timestamp = envelope.timestamp
                        )
                    )
                } catch (_: Exception) {
                    // Skip malformed messages
                }
            }
        }

        return details
    }

    private fun parseDisplayValue(value: String): String {
        return when {
            value.startsWith("S:") -> value.substring(2)
            value.startsWith("N:") -> value.substring(2)
            value.startsWith("0:") -> "(empty)"
            value == "null" -> "(empty)"
            else -> value
        }
    }

    /**
     * Check if local and server are in sync.
     */
    fun isInSync(): Boolean = engine.isInSync()

    /**
     * Get the sync engine for advanced operations.
     */
    fun getEngine(): SyncEngine = engine

    // ========== Safe Query Methods with @Throws for Swift interop ==========

    /**
     * Safely get accounts - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getAccountsSafe() = database.actualDatabaseQueries.getAccounts().executeAsList()

    /**
     * Safely get payees - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getPayeesSafe() = database.actualDatabaseQueries.getPayees().executeAsList()

    /**
     * Safely get categories - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getCategoriesSafe() = database.actualDatabaseQueries.getCategories().executeAsList()

    /**
     * Safely get category groups - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getCategoryGroupsSafe() = database.actualDatabaseQueries.getCategoryGroups().executeAsList()

    /**
     * Safely get transactions by account - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getTransactionsByAccountSafe(accountId: String) =
        database.actualDatabaseQueries.getTransactionsByAccount(accountId).executeAsList()

    /**
     * Get transactions with payee and category names pre-joined.
     * Optimized for list views - eliminates N dictionary lookups in the UI layer.
     *
     * @param accountId The account ID
     * @return List of transactions with payee_name and category_name included
     */
    @Throws(Exception::class)
    fun getTransactionsWithDetailsSafe(accountId: String) =
        database.actualDatabaseQueries.getTransactionsWithDetailsForAccount(accountId).executeAsList()

    /**
     * Get all transactions for display including parents (for accordion grouping).
     * Use this for UI display, then group parents with their children in Swift.
     *
     * @param accountId The account ID
     * @return List of all transactions with payee and category names
     */
    @Throws(Exception::class)
    fun getTransactionsForDisplaySafe(accountId: String) =
        database.actualDatabaseQueries.getTransactionsForDisplayByAccount(accountId).executeAsList()

    /**
     * Get transactions with details, paginated for large accounts.
     *
     * @param accountId The account ID
     * @param limit Maximum number of transactions to return
     * @param offset Number of transactions to skip
     * @return List of transactions with payee_name and category_name included
     */
    @Throws(Exception::class)
    fun getTransactionsWithDetailsPaginatedSafe(accountId: String, limit: Long, offset: Long) =
        database.actualDatabaseQueries.getTransactionsWithDetailsPaginated(accountId, limit, offset).executeAsList()

    /**
     * Search transactions with server-side text matching.
     * Searches payee name, notes, and amount.
     *
     * @param accountId The account ID
     * @param searchQuery The search text (will be wrapped with % for LIKE)
     * @return List of matching transactions with payee_name and category_name included
     */
    @Throws(Exception::class)
    fun searchTransactionsSafe(accountId: String, searchQuery: String) = run {
        val pattern = "%$searchQuery%"
        database.actualDatabaseQueries.searchTransactionsForAccount(
            accountId, pattern, pattern, pattern
        ).executeAsList()
    }

    /**
     * Safely get budget for month - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getBudgetForMonthSafe(month: Long) =
        database.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()

    /**
     * Safely get budget for a specific category - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getBudgetForCategorySafe(category: String) =
        database.actualDatabaseQueries.getBudgetForCategory(category).executeAsList()

    /**
     * Safely get a specific budget entry by ID - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getBudgetByIdSafe(budgetId: String) =
        database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()

    /**
     * Set budget amount (Swift-safe wrapper).
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param amount The budget amount in cents
     */
    @Throws(Exception::class)
    fun setBudgetAmountSafe(categoryId: String, month: Long, amount: Long) {
        setBudgetAmount(categoryId, month, amount)
    }

    /**
     * Set budget goal (Swift-safe wrapper).
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param goal The goal amount in cents (or null to clear)
     */
    @Throws(Exception::class)
    fun setBudgetGoalSafe(categoryId: String, month: Long, goal: Long?) {
        setBudgetGoal(categoryId, month, goal)
    }

    /**
     * Set budget carryover (Swift-safe wrapper).
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @param carryover The carryover amount in cents
     */
    @Throws(Exception::class)
    fun setBudgetCarryoverSafe(categoryId: String, month: Long, carryover: Long) {
        setBudgetCarryover(categoryId, month, carryover)
    }

    /**
     * Copy budgets from previous month (Swift-safe wrapper).
     * @param targetMonth The month to copy TO in YYYYMM format
     */
    @Throws(Exception::class)
    fun copyBudgetFromPreviousMonthSafe(targetMonth: Long) {
        copyBudgetFromPreviousMonth(targetMonth)
    }

    /**
     * Zero all budgets for a month (Swift-safe wrapper).
     * @param month The month in YYYYMM format
     */
    @Throws(Exception::class)
    fun zeroBudgetsForMonthSafe(month: Long) {
        zeroBudgetsForMonth(month)
    }

    /**
     * Transfer budget between categories (Swift-safe wrapper).
     * @param fromCategoryId Source category
     * @param toCategoryId Destination category
     * @param month The month in YYYYMM format
     * @param amount Amount to transfer in cents
     */
    @Throws(Exception::class)
    fun transferBudgetSafe(fromCategoryId: String, toCategoryId: String, month: Long, amount: Long) {
        transferBudget(fromCategoryId, toCategoryId, month, amount)
    }

    /**
     * Get spending totals by category for a date range (on-budget accounts only).
     * This is an optimized query that performs aggregation in SQL rather than Swift.
     *
     * @param startDate Start date in YYYYMMDD format
     * @param endDate End date in YYYYMMDD format
     * @return List of (category, spent) pairs where spent is negative for expenses
     */
    @Throws(Exception::class)
    fun getSpentByCategorySafe(startDate: Long, endDate: Long) =
        database.actualDatabaseQueries.getSpentByCategory(startDate, endDate).executeAsList()

    /**
     * Get categories with their group information pre-joined.
     * This is an optimized query that performs the JOIN in SQL rather than Swift.
     * Only returns visible (non-hidden, non-tombstoned) categories and groups.
     */
    @Throws(Exception::class)
    fun getCategoriesWithGroupsSafe() =
        database.actualDatabaseQueries.getCategoriesWithGroups().executeAsList()

    /**
     * Get the balance for a specific account.
     * This is an optimized query that performs SUM aggregation in SQL.
     *
     * @param accountId The account ID
     * @return The sum of all transaction amounts for this account
     */
    @Throws(Exception::class)
    fun getAccountBalanceSafe(accountId: String): Long =
        database.actualDatabaseQueries.getAccountBalance(accountId).executeAsOne().toLong()

    // ========== Reconciliation Methods ==========

    /**
     * Get the cleared balance for an account (only cleared transactions).
     * Used for reconciliation to compare against bank statement balance.
     *
     * @param accountId The account ID
     * @return The sum of cleared transaction amounts
     */
    @Throws(Exception::class)
    fun getClearedBalanceSafe(accountId: String): Long =
        database.actualDatabaseQueries.getClearedBalance(accountId).executeAsOne().toLong()

    /**
     * Get uncleared transactions for an account.
     * These are transactions that haven't been verified against bank statement.
     *
     * @param accountId The account ID
     * @return List of uncleared transactions
     */
    @Throws(Exception::class)
    fun getUnclearedTransactionsSafe(accountId: String) =
        database.actualDatabaseQueries.getUnclearedTransactions(accountId).executeAsList()

    /**
     * Get the count of uncleared transactions for an account.
     *
     * @param accountId The account ID
     * @return Number of uncleared transactions
     */
    @Throws(Exception::class)
    fun getUnclearedCountSafe(accountId: String): Long =
        database.actualDatabaseQueries.getUnclearedCount(accountId).executeAsOne()

    /**
     * Get reconciled (locked) transactions for an account.
     * These are transactions that have been locked after reconciliation.
     *
     * @param accountId The account ID
     * @return List of reconciled transactions
     */
    @Throws(Exception::class)
    fun getReconciledTransactionsSafe(accountId: String) =
        database.actualDatabaseQueries.getReconciledTransactions(accountId).executeAsList()

    /**
     * Get pending transactions for an account.
     * These are transactions pending bank confirmation.
     *
     * @param accountId The account ID
     * @return List of pending transactions
     */
    @Throws(Exception::class)
    fun getPendingTransactionsSafe(accountId: String) =
        database.actualDatabaseQueries.getPendingTransactions(accountId).executeAsList()

    /**
     * Get reconciliation summary for an account.
     * Returns total balance, cleared balance, uncleared balance, and uncleared count.
     *
     * @param accountId The account ID
     * @return Reconciliation summary with all balances
     */
    @Throws(Exception::class)
    fun getReconciliationSummarySafe(accountId: String) =
        database.actualDatabaseQueries.getReconciliationSummary(accountId).executeAsOne()

    /**
     * Toggle the cleared status of a transaction.
     * Creates a sync change so it propagates to other devices.
     *
     * @param transactionId The transaction ID
     * @param cleared The new cleared status (true = cleared, false = uncleared)
     */
    fun setTransactionCleared(transactionId: String, cleared: Boolean) {
        engine.createChange("transactions", transactionId, "cleared", if (cleared) 1 else 0)
    }

    /**
     * Toggle the reconciled (locked) status of a transaction.
     * Creates a sync change so it propagates to other devices.
     *
     * @param transactionId The transaction ID
     * @param reconciled The new reconciled status (true = locked, false = unlocked)
     */
    fun setTransactionReconciled(transactionId: String, reconciled: Boolean) {
        engine.createChange("transactions", transactionId, "reconciled", if (reconciled) 1 else 0)
    }

    /**
     * Get transactions that are cleared but not yet reconciled.
     * These are transactions ready to be locked during reconciliation.
     *
     * @param accountId The account ID
     * @return List of cleared but unreconciled transactions
     */
    @Throws(Exception::class)
    fun getClearedUnreconciledTransactionsSafe(accountId: String) =
        database.actualDatabaseQueries.getClearedUnreconciledTransactions(accountId).executeAsList()

    /**
     * Get the count of cleared but not yet reconciled transactions.
     *
     * @param accountId The account ID
     * @return Number of transactions ready to be reconciled
     */
    @Throws(Exception::class)
    fun getClearedUnreconciledCountSafe(accountId: String): Long =
        database.actualDatabaseQueries.getClearedUnreconciledCount(accountId).executeAsOne()

    /**
     * Mark multiple transactions as reconciled (locked) in one operation.
     * Creates sync changes for each transaction so they propagate to other devices.
     * Also reconciles parent transactions when children are reconciled.
     *
     * @param transactionIds List of transaction IDs to reconcile
     */
    fun reconcileTransactions(transactionIds: List<String>) {
        val parentIdsToReconcile = mutableSetOf<String>()

        for (id in transactionIds) {
            engine.createChange("transactions", id, "reconciled", 1)

            // Check if this is a child transaction with a parent
            val tx = database.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull()
            tx?.parent_id?.let { parentId ->
                parentIdsToReconcile.add(parentId)
            }
        }

        // Also reconcile any parent transactions
        for (parentId in parentIdsToReconcile) {
            engine.createChange("transactions", parentId, "reconciled", 1)
        }
    }

    // ========== Split Transaction Methods ==========

    /**
     * Get child transactions for a parent (split) transaction.
     *
     * @param parentId The parent transaction ID
     * @return List of child transactions
     */
    @Throws(Exception::class)
    fun getChildTransactionsSafe(parentId: String) =
        database.actualDatabaseQueries.getChildTransactions(parentId).executeAsList()

    /**
     * Get child transactions with payee and category names pre-joined.
     *
     * @param parentId The parent transaction ID
     * @return List of child transactions with payee_name and category_name
     */
    @Throws(Exception::class)
    fun getChildTransactionsWithDetailsSafe(parentId: String) =
        database.actualDatabaseQueries.getChildTransactionsWithDetails(parentId).executeAsList()

    /**
     * Check if a transaction has child transactions (is a split parent).
     *
     * @param transactionId The transaction ID to check
     * @return true if the transaction has children
     */
    @Throws(Exception::class)
    fun hasChildTransactionsSafe(transactionId: String): Boolean =
        database.actualDatabaseQueries.hasChildTransactions(transactionId).executeAsOne() > 0

    /**
     * Convert an existing transaction to a split parent.
     * Sets isParent = 1 and clears the category (parent shouldn't have a category).
     *
     * @param transactionId The transaction ID to convert
     */
    fun convertToSplitParent(transactionId: String) {
        engine.createChange("transactions", transactionId, "isParent", 1)
        engine.createChange("transactions", transactionId, "category", null)
    }

    /**
     * Create a child transaction for a split.
     * The child inherits account, date, payee, cleared, reconciled from parent.
     *
     * @param id The new child transaction ID
     * @param parentId The parent transaction ID
     * @param amount The amount for this split (in cents)
     * @param categoryId The category for this split
     * @param accountId The account ID (inherited from parent)
     * @param date The date (inherited from parent)
     * @param payeeId The payee ID (inherited from parent, optional)
     * @param cleared Whether the transaction is cleared (inherited from parent)
     * @param reconciled Whether the transaction is reconciled (inherited from parent)
     * @return The child transaction ID
     */
    fun createChildTransaction(
        id: String,
        parentId: String,
        amount: Long,
        categoryId: String?,
        accountId: String,
        date: Int,
        payeeId: String? = null,
        cleared: Boolean = true,
        reconciled: Boolean = false
    ): String {
        engine.createChange("transactions", id, "acct", accountId)
        engine.createChange("transactions", id, "date", date)
        engine.createChange("transactions", id, "amount", amount)
        engine.createChange("transactions", id, "description", payeeId)
        engine.createChange("transactions", id, "category", categoryId)
        engine.createChange("transactions", id, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", id, "reconciled", if (reconciled) 1 else 0)
        engine.createChange("transactions", id, "tombstone", 0)
        engine.createChange("transactions", id, "isChild", 1)
        engine.createChange("transactions", id, "parent_id", parentId)
        return id
    }

    /**
     * Delete a child transaction from a split.
     * If this is the last child, also converts the parent back to a regular transaction.
     *
     * @param childId The child transaction ID to delete
     */
    fun deleteChildTransaction(childId: String) {
        // Get the child to find its parent
        val child = database.actualDatabaseQueries.getTransactionById(childId).executeAsOneOrNull()
        val parentId = child?.parent_id

        // Delete the child (tombstone it)
        engine.createChange("transactions", childId, "tombstone", 1)

        // Check if parent has any remaining children
        if (parentId != null) {
            val remainingChildren = database.actualDatabaseQueries.hasChildTransactions(parentId).executeAsOne()
            if (remainingChildren <= 1) {
                // This was the last child (or will be after tombstone), convert parent back to normal
                engine.createChange("transactions", parentId, "isParent", 0)
            }
        }
    }

    /**
     * Update a child transaction's amount or category.
     *
     * @param childId The child transaction ID
     * @param amount New amount (optional)
     * @param categoryId New category (optional)
     */
    fun updateChildTransaction(childId: String, amount: Long? = null, categoryId: String? = null) {
        if (amount != null) {
            engine.createChange("transactions", childId, "amount", amount)
        }
        if (categoryId != null) {
            engine.createChange("transactions", childId, "category", categoryId)
        }
    }

    /**
     * Delete a split parent transaction along with all its children.
     * This ensures orphaned children don't affect balance calculations.
     *
     * @param parentId The parent transaction ID to delete
     */
    fun deleteSplitParent(parentId: String) {
        // First, delete all children
        val children = database.actualDatabaseQueries.getChildTransactions(parentId).executeAsList()
        for (child in children) {
            engine.createChange("transactions", child.id, "tombstone", 1)
        }

        // Then delete the parent
        engine.createChange("transactions", parentId, "tombstone", 1)
    }

    // ========== Transfer Methods ==========

    /**
     * Get or create a transfer payee for an account.
     * Transfer payees have an empty name and transfer_acct set to the account ID.
     * They are used to represent transfers to/from that account.
     *
     * @param accountId The account ID this transfer payee represents
     * @return The payee ID for transfers to this account
     */
    fun getOrCreateTransferPayee(accountId: String): String {
        // Check if transfer payee already exists for this account
        val existing = database.actualDatabaseQueries.getTransferPayeeForAccount(accountId).executeAsOneOrNull()
        if (existing != null) {
            return existing.id
        }

        // Create a new transfer payee
        // Transfer payees have empty name and transfer_acct pointing to the account
        val payeeId = generateUUID()
        engine.createChange("payees", payeeId, "name", "")
        engine.createChange("payees", payeeId, "transfer_acct", accountId)
        engine.createChange("payees", payeeId, "tombstone", 0)
        // Create payee_mapping entry (required for payee to be usable)
        engine.createChange("payee_mapping", payeeId, "targetId", payeeId)
        return payeeId
    }

    /**
     * Check if a payee is a transfer payee.
     *
     * @param payeeId The payee ID to check
     * @return true if this payee has transfer_acct set
     */
    fun isTransferPayee(payeeId: String): Boolean {
        val payee = database.actualDatabaseQueries.getPayeeById(payeeId).executeAsOneOrNull()
        return payee?.transfer_acct != null
    }

    /**
     * Create a transfer between two accounts.
     * Creates two linked transactions: one in each account with opposite amounts.
     *
     * @param fromAccountId Account money is leaving
     * @param toAccountId Account money is going to
     * @param amount Positive amount in cents (money going TO toAccount)
     * @param date Date as YYYYMMDD integer
     * @param notes Optional notes for the transfer
     * @param cleared Whether the transactions should be cleared (default: true)
     * @return Pair of (fromTxId, toTxId)
     */
    fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Long,
        date: Int,
        notes: String? = null,
        cleared: Boolean = true
    ): Pair<String, String> {
        // Get or create transfer payees for both accounts
        val toAccountPayeeId = getOrCreateTransferPayee(toAccountId)
        val fromAccountPayeeId = getOrCreateTransferPayee(fromAccountId)

        // Generate IDs for both transactions
        val fromTxId = generateUUID()
        val toTxId = generateUUID()

        // Create "from" transaction (negative amount - money leaving)
        engine.createChange("transactions", fromTxId, "acct", fromAccountId)
        engine.createChange("transactions", fromTxId, "date", date)
        engine.createChange("transactions", fromTxId, "amount", -amount)  // Negative: money leaving
        engine.createChange("transactions", fromTxId, "description", toAccountPayeeId)  // Payee points to destination
        engine.createChange("transactions", fromTxId, "category", null)  // Transfers typically don't have categories
        if (notes != null) engine.createChange("transactions", fromTxId, "notes", notes)
        engine.createChange("transactions", fromTxId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", fromTxId, "tombstone", 0)
        engine.createChange("transactions", fromTxId, "transferred_id", toTxId)  // Link to paired transaction

        // Create "to" transaction (positive amount - money arriving)
        engine.createChange("transactions", toTxId, "acct", toAccountId)
        engine.createChange("transactions", toTxId, "date", date)
        engine.createChange("transactions", toTxId, "amount", amount)  // Positive: money arriving
        engine.createChange("transactions", toTxId, "description", fromAccountPayeeId)  // Payee points to source
        engine.createChange("transactions", toTxId, "category", null)
        if (notes != null) engine.createChange("transactions", toTxId, "notes", notes)
        engine.createChange("transactions", toTxId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", toTxId, "tombstone", 0)
        engine.createChange("transactions", toTxId, "transferred_id", fromTxId)  // Link to paired transaction

        return Pair(fromTxId, toTxId)
    }

    /**
     * Update the amount of a transfer.
     * Automatically updates both sides with opposite amounts.
     *
     * @param transactionId Either side of the transfer
     * @param newAmount New absolute amount in cents (will be positive on one side, negative on other)
     */
    fun updateTransferAmount(transactionId: String, newAmount: Long) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Determine which side this is based on current amount sign
        val isSourceSide = (tx.amount ?: 0) < 0

        if (isSourceSide) {
            // This is the "from" side (negative), linked is "to" side (positive)
            engine.createChange("transactions", transactionId, "amount", -newAmount)
            engine.createChange("transactions", linkedId, "amount", newAmount)
        } else {
            // This is the "to" side (positive), linked is "from" side (negative)
            engine.createChange("transactions", transactionId, "amount", newAmount)
            engine.createChange("transactions", linkedId, "amount", -newAmount)
        }
    }

    /**
     * Update the date of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param newDate New date as YYYYMMDD integer
     */
    fun updateTransferDate(transactionId: String, newDate: Int) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "date", newDate)
        engine.createChange("transactions", linkedId, "date", newDate)
    }

    /**
     * Update the notes of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param newNotes New notes (can be null to clear)
     */
    fun updateTransferNotes(transactionId: String, newNotes: String?) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "notes", newNotes)
        engine.createChange("transactions", linkedId, "notes", newNotes)
    }

    /**
     * Update the cleared status of a transfer.
     * Automatically updates both sides.
     *
     * @param transactionId Either side of the transfer
     * @param cleared New cleared status
     */
    fun updateTransferCleared(transactionId: String, cleared: Boolean) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id ?: return

        // Update both sides
        engine.createChange("transactions", transactionId, "cleared", if (cleared) 1 else 0)
        engine.createChange("transactions", linkedId, "cleared", if (cleared) 1 else 0)
    }

    /**
     * Delete a transfer (both sides).
     * Sets tombstone=1 on both linked transactions.
     *
     * @param transactionId Either side of the transfer
     */
    fun deleteTransfer(transactionId: String) {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
            ?: return

        val linkedId = tx.transferred_id

        // Clear the link on both sides first
        engine.createChange("transactions", transactionId, "transferred_id", null)
        if (linkedId != null) {
            engine.createChange("transactions", linkedId, "transferred_id", null)
        }

        // Delete this transaction
        engine.createChange("transactions", transactionId, "tombstone", 1)

        // Delete the linked transaction
        if (linkedId != null) {
            engine.createChange("transactions", linkedId, "tombstone", 1)
        }
    }

    /**
     * Get the linked (paired) transaction ID for a transfer.
     *
     * @param transactionId The transaction to check
     * @return The ID of the linked transaction, or null if not a transfer
     */
    fun getLinkedTransactionId(transactionId: String): String? {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
        return tx?.transferred_id
    }

    /**
     * Check if a transaction is a transfer.
     *
     * @param transactionId The transaction to check
     * @return true if the transaction has a transferred_id
     */
    fun isTransferTransaction(transactionId: String): Boolean {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
        return tx?.transferred_id != null
    }

    /**
     * Generate a UUID for new entities.
     */
    private fun generateUUID(): String {
        // Simple UUID generation - uses random hex characters
        val chars = "0123456789abcdef"
        val segments = listOf(8, 4, 4, 4, 12)
        return segments.joinToString("-") { length ->
            (1..length).map { chars.random() }.joinToString("")
        }
    }

    // ========== Transfer Safe Methods for Swift Interop ==========

    /**
     * Get the transfer payee for an account (Swift-safe).
     *
     * @param accountId The account ID
     * @return The transfer payee or null if not found
     */
    @Throws(Exception::class)
    fun getTransferPayeeForAccountSafe(accountId: String) =
        database.actualDatabaseQueries.getTransferPayeeForAccount(accountId).executeAsOneOrNull()

    /**
     * Check if a transaction is a transfer (Swift-safe).
     *
     * @param transactionId The transaction ID
     * @return true if the transaction is a transfer
     */
    @Throws(Exception::class)
    fun isTransferTransactionSafe(transactionId: String): Boolean {
        val tx = database.actualDatabaseQueries.getTransactionById(transactionId).executeAsOneOrNull()
        return tx?.transferred_id != null
    }

    /**
     * Get the linked transaction for a transfer (Swift-safe).
     *
     * @param transactionId The transaction ID
     * @return The linked transaction or null if not a transfer
     */
    @Throws(Exception::class)
    fun getLinkedTransactionSafe(transactionId: String) =
        database.actualDatabaseQueries.getLinkedTransaction(transactionId).executeAsOneOrNull()

    /**
     * Get all transfer payees (Swift-safe).
     *
     * @return List of payees that have transfer_acct set
     */
    @Throws(Exception::class)
    fun getTransferPayeesSafe() =
        database.actualDatabaseQueries.getPayeesWithTransferAcct().executeAsList()

    // ========== Reordering Safe Methods ==========

    /**
     * Move a category to a new position (Swift-safe).
     *
     * @param categoryId The category to move
     * @param newGroupId The target group
     * @param targetCategoryId The category to insert before, or null to append
     */
    @Throws(Exception::class)
    fun moveCategorySafe(categoryId: String, newGroupId: String, targetCategoryId: String?) {
        moveCategory(categoryId, newGroupId, targetCategoryId)
    }

    /**
     * Move a category group to a new position (Swift-safe).
     *
     * @param groupId The category group to move
     * @param targetGroupId The group to insert before, or null to append
     */
    @Throws(Exception::class)
    fun moveCategoryGroupSafe(groupId: String, targetGroupId: String?) {
        moveCategoryGroup(groupId, targetGroupId)
    }

    /**
     * Move an account to a new position (Swift-safe).
     *
     * @param accountId The account to move
     * @param targetAccountId The account to insert before, or null to append
     */
    @Throws(Exception::class)
    fun moveAccountSafe(accountId: String, targetAccountId: String?) {
        moveAccount(accountId, targetAccountId)
    }

    /**
     * Reopen a closed account (Swift-safe).
     *
     * @param accountId The account to reopen
     */
    @Throws(Exception::class)
    fun reopenAccountSafe(accountId: String) {
        reopenAccount(accountId)
    }

    // ========== Advanced Budget Safe Methods ==========

    /**
     * Copy budget from previous month for a single category (Swift-safe).
     *
     * @param categoryId The category to copy budget for
     * @param month The target month in YYYYMM format
     */
    @Throws(Exception::class)
    fun copySinglePreviousMonthSafe(categoryId: String, month: Long) {
        copySinglePreviousMonth(categoryId, month)
    }

    /**
     * Set budget based on N-month spending average (Swift-safe).
     *
     * @param categoryId The category to set budget for
     * @param month The target month in YYYYMM format
     * @param n Number of months to average
     */
    @Throws(Exception::class)
    fun setNMonthAverageSafe(categoryId: String, month: Long, n: Int) {
        setNMonthAverage(categoryId, month, n)
    }

    /**
     * Set 3-month average budget for all visible categories (Swift-safe).
     *
     * @param month The target month in YYYYMM format
     */
    @Throws(Exception::class)
    fun set3MonthAverageSafe(month: Long) {
        set3MonthAverage(month)
    }

    /**
     * Set 6-month average budget for all visible categories (Swift-safe).
     *
     * @param month The target month in YYYYMM format
     */
    @Throws(Exception::class)
    fun set6MonthAverageSafe(month: Long) {
        set6MonthAverage(month)
    }

    /**
     * Set 12-month average budget for all visible categories (Swift-safe).
     *
     * @param month The target month in YYYYMM format
     */
    @Throws(Exception::class)
    fun set12MonthAverageSafe(month: Long) {
        set12MonthAverage(month)
    }

    // ========== Hold Operations Safe Methods ==========

    /**
     * Calculate the "To Budget" amount for a month (Swift-safe).
     *
     * @param month The month in YYYYMM format
     * @return The available amount to budget
     */
    @Throws(Exception::class)
    fun calculateToBudgetSafe(month: Long): Long {
        return calculateToBudget(month)
    }

    /**
     * Hold money for next month (Swift-safe).
     *
     * @param month The month in YYYYMM format
     * @param amount The amount to hold
     * @return true if successful, false if not enough money
     */
    @Throws(Exception::class)
    fun holdForNextMonthSafe(month: Long, amount: Long): Boolean {
        return holdForNextMonth(month, amount)
    }

    /**
     * Reset the hold amount for a month (Swift-safe).
     *
     * @param month The month in YYYYMM format
     */
    @Throws(Exception::class)
    fun resetHoldSafe(month: Long) {
        resetHold(month)
    }

    /**
     * Get the buffered (held) amount for a month (Swift-safe).
     *
     * @param month The month in YYYYMM format
     * @return The buffered amount
     */
    @Throws(Exception::class)
    fun getBufferedAmountSafe(month: Long): Long {
        val monthId = month.toString()
        val budgetMonth = database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()
        return budgetMonth?.buffered ?: 0L
    }

    // ========== Cover Operations Safe Methods ==========

    /**
     * Calculate the leftover for a category (Swift-safe).
     *
     * @param categoryId The category ID
     * @param month The month in YYYYMM format
     * @return The available/leftover amount
     */
    @Throws(Exception::class)
    fun calculateCategoryLeftoverSafe(categoryId: String, month: Long): Long {
        return calculateCategoryLeftover(categoryId, month)
    }

    /**
     * Cover overspending from one category to another (Swift-safe).
     *
     * @param fromCategoryId Category with available funds
     * @param toCategoryId Overspent category to cover
     * @param month The month in YYYYMM format
     * @param amount Amount to cover (or null to cover full overspending)
     */
    @Throws(Exception::class)
    fun coverOverspendingSafe(
        fromCategoryId: String,
        toCategoryId: String,
        month: Long,
        amount: Long?
    ) {
        coverOverspending(fromCategoryId, toCategoryId, month, amount)
    }

    /**
     * Transfer available "To Budget" money to a category (Swift-safe).
     *
     * @param amount Amount to transfer
     * @param toCategoryId Target category
     * @param month The month in YYYYMM format
     */
    @Throws(Exception::class)
    fun transferAvailableSafe(amount: Long, toCategoryId: String, month: Long) {
        transferAvailable(amount, toCategoryId, month)
    }

    // ========== Diagnostic Methods ==========

    /**
     * Get diagnostic info about what's in the database.
     * Returns a string with table names and row counts.
     */
    @Throws(Exception::class)
    fun getDatabaseDiagnostics(): String {
        val sb = StringBuilder()
        sb.appendLine("=== Database Diagnostics ===")

        try {
            val queries = database.actualDatabaseQueries

            // Count ALL rows (including tombstoned)
            sb.appendLine("\n--- Total Row Counts (ALL rows) ---")
            sb.appendLine("Accounts: ${queries.countAllAccounts().executeAsOne()}")
            sb.appendLine("Payees: ${queries.countAllPayees().executeAsOne()}")
            sb.appendLine("Categories: ${queries.countAllCategories().executeAsOne()}")
            sb.appendLine("Category Groups: ${queries.countAllCategoryGroups().executeAsOne()}")
            sb.appendLine("Transactions: ${queries.countAllTransactions().executeAsOne()}")

            // Count filtered rows (tombstone=0)
            sb.appendLine("\n--- Filtered Row Counts (tombstone=0) ---")
            sb.appendLine("Accounts: ${queries.getAccounts().executeAsList().size}")
            sb.appendLine("Payees: ${queries.getPayees().executeAsList().size}")
            sb.appendLine("Categories: ${queries.getCategories().executeAsList().size}")
            sb.appendLine("Category Groups: ${queries.getCategoryGroups().executeAsList().size}")

            // List ALL accounts
            sb.appendLine("\n--- All Accounts ---")
            val allAccounts = queries.getAllAccounts().executeAsList()
            for (acc in allAccounts) {
                sb.appendLine("  ${acc.id}: name='${acc.name}', tombstone=${acc.tombstone}, closed=${acc.closed}, offbudget=${acc.offbudget}")
            }

            // List ALL payees (first 10)
            sb.appendLine("\n--- All Payees (first 10) ---")
            val allPayees = queries.getAllPayees().executeAsList().take(10)
            for (p in allPayees) {
                sb.appendLine("  ${p.id}: name='${p.name}', tombstone=${p.tombstone}")
            }

            // List ALL categories (first 10)
            sb.appendLine("\n--- All Categories (first 10) ---")
            val allCategories = queries.getAllCategories().executeAsList().take(10)
            for (c in allCategories) {
                sb.appendLine("  ${c.id}: name='${c.name}', group=${c.cat_group}, tombstone=${c.tombstone}")
            }

            // List ALL category groups
            sb.appendLine("\n--- All Category Groups ---")
            val allGroups = queries.getAllCategoryGroups().executeAsList()
            for (g in allGroups) {
                sb.appendLine("  ${g.id}: name='${g.name}', tombstone=${g.tombstone}")
            }

            // List ALL transactions (first 10)
            sb.appendLine("\n--- All Transactions (first 10) ---")
            val allTx = queries.getAllTransactions().executeAsList().take(10)
            for (tx in allTx) {
                sb.appendLine("  ${tx.id}: acct=${tx.acct}, amount=${tx.amount}, date=${tx.date}, tombstone=${tx.tombstone}")
            }

            // Check messages_crdt
            sb.appendLine("\n--- CRDT Messages ---")
            val messages = queries.getMessagesSince("").executeAsList()
            sb.appendLine("Total messages in crdt log: ${messages.size}")

            // Show unique datasets
            val datasets = messages.map { it.dataset }.distinct()
            sb.appendLine("Datasets: $datasets")

            // Count messages per dataset
            for (ds in datasets) {
                val count = messages.count { it.dataset == ds }
                sb.appendLine("  $ds: $count messages")
            }

        } catch (e: Exception) {
            sb.appendLine("Error during diagnostics: ${e.message}")
            sb.appendLine("Stack trace: ${e.stackTraceToString()}")
        }

        return sb.toString()
    }

    /**
     * Run a raw SQL query and return results as string (for debugging).
     */
    @Throws(Exception::class)
    fun rawQuery(sql: String): String {
        val sb = StringBuilder()
        sb.appendLine("Query: $sql")
        sb.appendLine("---")

        // Note: SQLDelight doesn't expose raw query execution easily
        // This is a placeholder - we'd need to use the driver directly
        sb.appendLine("(Raw queries not implemented - use specific diagnostic methods)")

        return sb.toString()
    }
}

/**
 * Result of a sync operation.
 */
sealed class SyncResult {
    data class Success(
        val messagesSent: Int,
        val messagesReceived: Int,
        val messagesApplied: Int
    ) : SyncResult()

    data class Error(val message: String) : SyncResult()
}
