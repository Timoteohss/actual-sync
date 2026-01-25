package com.actualbudget.sync.sync

import com.actualbudget.sync.auth.AuthSession
import com.actualbudget.sync.crdt.Merkle
import com.actualbudget.sync.crdt.MutableClock
import com.actualbudget.sync.crdt.Timestamp
import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.http.RetryConfig
import com.actualbudget.sync.http.withRetry
import com.actualbudget.sync.io.BudgetFileManager
import com.actualbudget.sync.io.BudgetMetadata
import kotlinx.serialization.json.Json
import com.actualbudget.sync.proto.SyncRequest
import com.actualbudget.sync.proto.SyncResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlin.concurrent.Volatile

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
 *
 * Can operate in two modes:
 * 1. Connected mode: Pass an [AuthSession] to enable server sync
 * 2. Local-only mode: Pass null session for offline-only operation
 *
 * Thread-safety note: Uses @Volatile for visibility. In typical usage
 * (single sync operation at a time), this is sufficient. For concurrent
 * access from multiple threads, external synchronization should be used.
 *
 * IMPORTANT: Call initialize() before using any sync operations.
 *
 * Usage:
 * ```
 * // Connected mode
 * val session = authClient.login(serverUrl, password)
 * val manager = SyncManager(session, database)
 * manager.initialize()
 *
 * // Local-only mode
 * val manager = SyncManager(database)
 * manager.initialize()
 * ```
 */
class SyncManager(
    session: AuthSession?,
    private val database: ActualDatabase
) {
    /**
     * Query operations - all read-only database queries.
     * Use this for direct access to optimized queries.
     */
    val queries: QueryOperations = QueryOperations(database)

    // Mutable state with @Volatile for visibility across threads
    @Volatile
    private var _session: AuthSession? = session

    @Volatile
    private var _fileId: String? = null

    @Volatile
    private var _groupId: String? = null

    @Volatile
    private var _initialized: Boolean = false

    // These are only accessed after initialization
    private lateinit var clock: MutableClock
    private lateinit var engine: SyncEngine

    // Operation classes - initialized after engine is created
    private lateinit var _transactions: TransactionOperations
    private lateinit var _budgets: BudgetOperations
    private lateinit var _categories: CategoryOperations
    private lateinit var _accounts: AccountOperations
    private lateinit var _payees: PayeeOperations

    /**
     * Transaction operations - create, update, delete transactions, splits, and transfers.
     * Requires initialize() to be called first.
     */
    val transactions: TransactionOperations
        get() {
            requireInitialized()
            return _transactions
        }

    /**
     * Budget operations - set amounts, goals, hold, cover overspending.
     * Requires initialize() to be called first.
     */
    val budgets: BudgetOperations
        get() {
            requireInitialized()
            return _budgets
        }

    /**
     * Category operations - create, update, delete, and reorder categories and groups.
     * Requires initialize() to be called first.
     */
    val categories: CategoryOperations
        get() {
            requireInitialized()
            return _categories
        }

    /**
     * Account operations - create, update, delete, close, reopen, and reorder accounts.
     * Requires initialize() to be called first.
     */
    val accounts: AccountOperations
        get() {
            requireInitialized()
            return _accounts
        }

    /**
     * Payee operations - create, update, delete, and merge payees.
     * Requires initialize() to be called first.
     */
    val payees: PayeeOperations
        get() {
            requireInitialized()
            return _payees
        }

    // Accessors for mutable state
    private var session: AuthSession?
        get() = _session
        set(value) { _session = value }

    private var fileId: String?
        get() = _fileId
        set(value) { _fileId = value }

    private var groupId: String?
        get() = _groupId
        set(value) { _groupId = value }

    /**
     * Check if the manager has been initialized.
     * Must be true before calling any sync operations.
     */
    val isInitialized: Boolean get() = _initialized

    /**
     * Require that the manager is initialized, throwing if not.
     */
    private fun requireInitialized() {
        if (!_initialized) {
            throw IllegalStateException("SyncManager not initialized. Call initialize() first.")
        }
    }

    /**
     * Create a SyncManager in local-only mode (no server connection).
     *
     * @param database The local database
     */
    constructor(database: ActualDatabase) : this(null, database)

    /**
     * Legacy constructor for backward compatibility.
     *
     * @param serverUrl The server URL
     * @param httpClient The HTTP client
     * @param database The local database
     * @deprecated Use constructor with AuthSession instead
     */
    @Deprecated(
        message = "Use constructor with AuthSession instead",
        replaceWith = ReplaceWith("SyncManager(session, database)")
    )
    constructor(
        serverUrl: String,
        httpClient: HttpClient,
        database: ActualDatabase
    ) : this(
        if (serverUrl.isNotBlank()) AuthSession(serverUrl, "", httpClient) else null,
        database
    )

    // Helper properties for accessing session data
    private val serverUrl: String get() = session?.serverUrl ?: ""
    private val httpClient: HttpClient? get() = session?.httpClient
    private val token: String? get() = session?.token?.takeIf { it.isNotBlank() }

    /**
     * Check if this manager is connected to a server.
     */
    val isConnected: Boolean get() = session != null && token != null

    /**
     * Update the session (e.g., after re-authentication).
     * Thread-safe.
     */
    fun updateSession(newSession: AuthSession) {
        session = newSession
    }

    /**
     * Initialize the sync manager with a client ID.
     * Must be called before any sync operations.
     *
     * Note: Can be called multiple times safely (subsequent calls are no-ops).
     * For thread-safe initialization from multiple threads, use external synchronization.
     */
    fun initialize(clientId: String? = null) {
        if (_initialized) return

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

        // Initialize operation classes with the engine
        _transactions = TransactionOperations(engine, database)
        _budgets = BudgetOperations(engine, database)
        _categories = CategoryOperations(engine, database)
        _accounts = AccountOperations(engine, database)
        _payees = PayeeOperations(engine, database)

        _initialized = true
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
     *
     * @deprecated Use constructor with AuthSession instead, or call updateSession()
     */
    @Deprecated(
        message = "Use constructor with AuthSession or updateSession() instead",
        replaceWith = ReplaceWith("updateSession(AuthSession(serverUrl, token, httpClient))")
    )
    fun setToken(token: String) {
        // Update session with new token while preserving serverUrl and httpClient
        val currentSession = this.session
        if (currentSession != null) {
            this.session = AuthSession(
                serverUrl = currentSession.serverUrl,
                token = token,
                httpClient = currentSession.httpClient
            )
        }
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
     *
     * @throws IllegalStateException if not initialized
     */
    suspend fun fullSync(): SyncResult {
        requireInitialized()
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
     * Includes automatic retry for transient network failures.
     * Requires a connected session with valid token.
     *
     * @param fileId The budget file ID to download
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @return The raw zip file bytes, or null if download failed or not connected
     */
    suspend fun downloadBudgetFile(
        fileId: String,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): ByteArray? {
        val client = httpClient ?: run {
            println("[SyncManager] Cannot download: no HTTP client (local-only mode)")
            return null
        }

        return try {
            println("[SyncManager] Downloading budget file: $fileId")

            withRetry(
                config = retryConfig,
                operation = "download budget file $fileId"
            ) {
                val response = client.get("$serverUrl/sync/download-user-file") {
                    token?.let { header("X-ACTUAL-TOKEN", it) }
                    header("X-ACTUAL-FILE-ID", fileId)
                }

                if (response.status.isSuccess()) {
                    val bytes = response.readRawBytes()
                    println("[SyncManager] Downloaded ${bytes.size} bytes")
                    bytes
                } else {
                    throw Exception("Download failed: ${response.status}")
                }
            }
        } catch (e: Exception) {
            println("[SyncManager] Download error after retries: ${e.message}")
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
     *
     * @throws IllegalStateException if not initialized
     */
    suspend fun sync(): SyncResult {
        requireInitialized()
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
     *
     * Includes automatic retry for transient network failures.
     * CRDT sync is idempotent so retrying is safe.
     * Requires a connected session with valid token.
     *
     * @param request The sync request to send
     * @param retryConfig Retry configuration (default: up to 3 retries)
     */
    private suspend fun performSync(
        request: SyncRequest,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): SyncResult {
        val client = httpClient ?: return SyncResult.Error("Not connected: no HTTP client (local-only mode)")

        return try {
            val requestBytes = request.encode()

            withRetry(
                config = retryConfig,
                operation = "sync with server"
            ) {
                val response = client.post("$serverUrl/sync/sync") {
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
                    throw Exception("Sync failed: ${response.status}")
                }
            }
        } catch (e: Exception) {
            SyncResult.Error("Sync error after retries: ${e.message}")
        }
    }

    // ========== Local Change Methods (delegate to operation classes) ==========

    /** @see AccountOperations.createAccount */
    fun createAccount(id: String, name: String, offbudget: Boolean = false): String =
        accounts.createAccount(id, name, offbudget)

    /** @see AccountOperations.updateAccount */
    fun updateAccount(id: String, field: String, value: Any?) =
        accounts.updateAccount(id, field, value)

    /** @see AccountOperations.deleteAccount */
    fun deleteAccount(id: String) = accounts.deleteAccount(id)

    /** @see PayeeOperations.createPayee */
    fun createPayee(id: String, name: String): String = payees.createPayee(id, name)

    /** @see PayeeOperations.updatePayee */
    fun updatePayee(id: String, field: String, value: Any?) = payees.updatePayee(id, field, value)

    /** @see PayeeOperations.deletePayee */
    fun deletePayee(id: String) = payees.deletePayee(id)

    /** @see CategoryOperations.createCategory */
    fun createCategory(
        id: String,
        name: String,
        groupId: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String = categories.createCategory(id, name, groupId, isIncome, sortOrder, hidden)

    /** @see CategoryOperations.updateCategory */
    fun updateCategory(id: String, field: String, value: Any?) = categories.updateCategory(id, field, value)

    /** @see CategoryOperations.deleteCategory */
    fun deleteCategory(id: String) = categories.deleteCategory(id)

    /** @see CategoryOperations.createCategoryGroup */
    fun createCategoryGroup(
        id: String,
        name: String,
        isIncome: Boolean = false,
        sortOrder: Double? = null,
        hidden: Boolean = false
    ): String = categories.createCategoryGroup(id, name, isIncome, sortOrder, hidden)

    /** @see CategoryOperations.updateCategoryGroup */
    fun updateCategoryGroup(id: String, field: String, value: Any?) = categories.updateCategoryGroup(id, field, value)

    /** @see CategoryOperations.deleteCategoryGroup */
    fun deleteCategoryGroup(id: String) = categories.deleteCategoryGroup(id)

    // ========== Budget Methods (delegate to BudgetOperations) ==========

    /** @see BudgetOperations.setBudgetAmount */
    fun setBudgetAmount(categoryId: String, month: Long, amount: Long) =
        budgets.setBudgetAmount(categoryId, month, amount)

    /** @see BudgetOperations.setBudgetGoal */
    fun setBudgetGoal(categoryId: String, month: Long, goal: Long?) =
        budgets.setBudgetGoal(categoryId, month, goal)

    /** @see BudgetOperations.setBudgetCarryover */
    fun setBudgetCarryover(categoryId: String, month: Long, carryover: Long) =
        budgets.setBudgetCarryover(categoryId, month, carryover)

    /** @see BudgetOperations.copyBudgetFromPreviousMonth */
    fun copyBudgetFromPreviousMonth(targetMonth: Long) =
        budgets.copyBudgetFromPreviousMonth(targetMonth)

    /** @see BudgetOperations.zeroBudgetsForMonth */
    fun zeroBudgetsForMonth(month: Long) = budgets.zeroBudgetsForMonth(month)

    /** @see BudgetOperations.transferBudget */
    fun transferBudget(fromCategoryId: String, toCategoryId: String, month: Long, amount: Long) =
        budgets.transferBudget(fromCategoryId, toCategoryId, month, amount)

    // ========== Reordering Methods (delegate to operation classes) ==========

    /** @see CategoryOperations.moveCategory */
    fun moveCategory(categoryId: String, newGroupId: String, targetCategoryId: String?) =
        categories.moveCategory(categoryId, newGroupId, targetCategoryId)

    /** @see CategoryOperations.moveCategoryGroup */
    fun moveCategoryGroup(groupId: String, targetGroupId: String?) =
        categories.moveCategoryGroup(groupId, targetGroupId)

    /** @see AccountOperations.moveAccount */
    fun moveAccount(accountId: String, targetAccountId: String?) =
        accounts.moveAccount(accountId, targetAccountId)

    /** @see AccountOperations.reopenAccount */
    fun reopenAccount(accountId: String) = accounts.reopenAccount(accountId)

    // ========== Advanced Budget Methods (convenience wrappers) ==========

    /**
     * Copy budget from the previous month for a single category.
     * This is a convenience method - for bulk operations use BudgetOperations directly.
     */
    fun copySinglePreviousMonth(categoryId: String, month: Long) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)
        val prevMonth = BudgetUtils.calculatePreviousMonth(month)
        val prevBudgetId = "$prevMonth-$categoryId"
        val prevBudget = database.actualDatabaseQueries.getBudgetById(prevBudgetId).executeAsOneOrNull()
        budgets.setBudgetAmount(categoryId, month, prevBudget?.amount ?: 0L)
    }

    /**
     * Set the budget for a category based on N-month spending average.
     */
    fun setNMonthAverage(categoryId: String, month: Long, n: Int) {
        BudgetUtils.requireValidId(categoryId, "categoryId")
        BudgetUtils.requireValidMonth(month)
        BudgetUtils.requirePositive(n, "n (months to average)")

        val (startDate, endDate) = BudgetUtils.calculateDateRangeForPreviousMonths(month, n)
        val spentData = database.actualDatabaseQueries.getSpentByCategoryForMonths(startDate, endDate).executeAsList()
        val categoryData = spentData.find { it.category == categoryId }
        val totalSpent: Long = categoryData?.total_spent?.toLong() ?: 0L
        val category = database.actualDatabaseQueries.getCategoryById(categoryId).executeAsOneOrNull()
        var avg: Long = kotlin.math.round(totalSpent.toDouble() / n).toLong()
        if (category?.is_income == 0L) avg = -avg
        budgets.setBudgetAmount(categoryId, month, avg)
    }

    /** Set 3-month average budget for all visible categories. */
    fun set3MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 3)

    /** Set 6-month average budget for all visible categories. */
    fun set6MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 6)

    /** Set 12-month average budget for all visible categories. */
    fun set12MonthAverage(month: Long) = setAllCategoriesNMonthAverage(month, 12)

    private fun setAllCategoriesNMonthAverage(month: Long, n: Int) {
        database.actualDatabaseQueries.getCategories().executeAsList()
            .filter { it.hidden == 0L && it.tombstone == 0L && it.is_income == 0L }
            .forEach { setNMonthAverage(it.id, month, n) }
    }

    // ========== Hold Operations (delegate to BudgetOperations) ==========

    /** @see BudgetOperations.calculateToBudget */
    fun calculateToBudget(month: Long): Long = budgets.calculateToBudget(month)

    /** @see BudgetOperations.holdForNextMonth */
    fun holdForNextMonth(month: Long, amount: Long): Boolean = budgets.holdForNextMonth(month, amount)

    /** @see BudgetOperations.resetHold */
    fun resetHold(month: Long) = budgets.resetHold(month)

    // ========== Cover Operations (delegate to BudgetOperations) ==========

    /** @see BudgetOperations.calculateCategoryLeftover */
    fun calculateCategoryLeftover(categoryId: String, month: Long): Long =
        budgets.calculateCategoryLeftover(categoryId, month)

    /** @see BudgetOperations.coverOverspending */
    fun coverOverspending(fromCategoryId: String, toCategoryId: String, month: Long, amount: Long? = null) =
        budgets.coverOverspending(fromCategoryId, toCategoryId, month, amount)

    /** @see BudgetOperations.transferAvailable */
    fun transferAvailable(amount: Long, toCategoryId: String, month: Long) =
        budgets.transferAvailable(amount, toCategoryId, month)

    /** @see TransactionOperations.createTransaction */
    fun createTransaction(
        id: String,
        accountId: String,
        date: Int,
        amount: Long,
        payeeId: String? = null,
        categoryId: String? = null,
        notes: String? = null
    ): String = transactions.createTransaction(id, accountId, date, amount, payeeId, categoryId, notes)

    /** @see TransactionOperations.updateTransaction */
    fun updateTransaction(id: String, field: String, value: Any?) =
        transactions.updateTransaction(id, field, value)

    /** @see TransactionOperations.deleteTransaction */
    fun deleteTransaction(id: String) = transactions.deleteTransaction(id)

    /**
     * Get the number of pending changes.
     *
     * @throws IllegalStateException if not initialized
     */
    fun getPendingChangeCount(): Int {
        requireInitialized()
        return engine.getPendingMessages().size
    }

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
     *
     * @throws IllegalStateException if not initialized
     */
    fun isInSync(): Boolean {
        requireInitialized()
        return engine.isInSync()
    }

    /**
     * Get the sync engine for advanced operations.
     */
    fun getEngine(): SyncEngine = engine

    // ========== Reconciliation Methods ==========

    /** @see TransactionOperations.setTransactionCleared */
    fun setTransactionCleared(transactionId: String, cleared: Boolean) =
        transactions.setTransactionCleared(transactionId, cleared)

    /** @see TransactionOperations.setTransactionReconciled */
    fun setTransactionReconciled(transactionId: String, reconciled: Boolean) =
        transactions.setTransactionReconciled(transactionId, reconciled)

    /** @see TransactionOperations.reconcileTransactions */
    fun reconcileTransactions(transactionIds: List<String>) = transactions.reconcileTransactions(transactionIds)

    /** @see TransactionOperations.convertToSplitParent */
    fun convertToSplitParent(transactionId: String) = transactions.convertToSplitParent(transactionId)

    /** @see TransactionOperations.createChildTransaction */
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
    ): String = transactions.createChildTransaction(id, parentId, amount, categoryId, accountId, date, payeeId, cleared, reconciled)

    /** @see TransactionOperations.deleteChildTransaction */
    fun deleteChildTransaction(childId: String) = transactions.deleteChildTransaction(childId)

    /** @see TransactionOperations.updateChildTransaction */
    fun updateChildTransaction(childId: String, amount: Long? = null, categoryId: String? = null) =
        transactions.updateChildTransaction(childId, amount, categoryId)

    /** @see TransactionOperations.deleteSplitParent */
    fun deleteSplitParent(parentId: String) = transactions.deleteSplitParent(parentId)

    // ========== Transfer Methods (delegate to TransactionOperations) ==========

    /** @see TransactionOperations.getOrCreateTransferPayee */
    fun getOrCreateTransferPayee(accountId: String): String = transactions.getOrCreateTransferPayee(accountId)

    /** @see TransactionOperations.isTransferPayee */
    fun isTransferPayee(payeeId: String): Boolean = transactions.isTransferPayee(payeeId)

    /** @see TransactionOperations.createTransfer */
    fun createTransfer(
        fromAccountId: String,
        toAccountId: String,
        amount: Long,
        date: Int,
        notes: String? = null,
        cleared: Boolean = true
    ): Pair<String, String> = transactions.createTransfer(fromAccountId, toAccountId, amount, date, notes, cleared)

    /** @see TransactionOperations.updateTransferAmount */
    fun updateTransferAmount(transactionId: String, newAmount: Long) =
        transactions.updateTransferAmount(transactionId, newAmount)

    /** @see TransactionOperations.updateTransferDate */
    fun updateTransferDate(transactionId: String, newDate: Int) =
        transactions.updateTransferDate(transactionId, newDate)

    /** @see TransactionOperations.updateTransferNotes */
    fun updateTransferNotes(transactionId: String, newNotes: String?) =
        transactions.updateTransferNotes(transactionId, newNotes)

    /** @see TransactionOperations.updateTransferCleared */
    fun updateTransferCleared(transactionId: String, cleared: Boolean) =
        transactions.updateTransferCleared(transactionId, cleared)

    /** @see TransactionOperations.deleteTransfer */
    fun deleteTransfer(transactionId: String) = transactions.deleteTransfer(transactionId)

    /** @see TransactionOperations.getLinkedTransactionId */
    fun getLinkedTransactionId(transactionId: String): String? = transactions.getLinkedTransactionId(transactionId)

    /** @see TransactionOperations.isTransferTransaction */
    fun isTransferTransaction(transactionId: String): Boolean = transactions.isTransferTransaction(transactionId)

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
