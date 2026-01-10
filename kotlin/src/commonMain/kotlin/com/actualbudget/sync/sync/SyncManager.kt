package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.Merkle
import com.actualbudget.sync.crdt.MutableClock
import com.actualbudget.sync.crdt.Timestamp
import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.io.BudgetFileManager
import com.actualbudget.sync.proto.SyncRequest
import com.actualbudget.sync.proto.SyncResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

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

            // Extract the zip
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

            // Copy to target location
            val success = fileManager.copy(extractedDbPath, targetDbPath)

            if (success) {
                println("[SyncManager] Budget installed successfully")
                // Verify the file exists and check size
                if (fileManager.exists(targetDbPath)) {
                    println("[SyncManager] Verified: target file exists")
                } else {
                    println("[SyncManager] ERROR: target file does not exist after copy!")
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
     */
    fun createPayee(id: String, name: String): String {
        engine.createChange("payees", id, "name", name)
        engine.createChange("payees", id, "tombstone", 0)
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
     */
    fun createCategory(id: String, name: String, groupId: String): String {
        engine.createChange("categories", id, "name", name)
        engine.createChange("categories", id, "cat_group", groupId)
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
     * Create a new transaction locally.
     *
     * @param id Transaction ID
     * @param accountId Account ID
     * @param date Date as YYYYMMDD integer
     * @param amount Amount in cents (negative for expenses)
     * @param payeeId Optional payee ID (stored in 'description' column in Actual)
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
        // Actual stores payee ID in 'description' column
        if (payeeId != null) engine.createChange("transactions", id, "description", payeeId)
        if (categoryId != null) engine.createChange("transactions", id, "category", categoryId)
        if (notes != null) engine.createChange("transactions", id, "notes", notes)
        engine.createChange("transactions", id, "cleared", 0)
        engine.createChange("transactions", id, "tombstone", 0)
        return id
    }

    /**
     * Update a transaction field.
     */
    fun updateTransaction(id: String, field: String, value: Any?) {
        engine.createChange("transactions", id, field, value)
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
     * Safely get budget for month - exceptions propagate to Swift as NSError.
     */
    @Throws(Exception::class)
    fun getBudgetForMonthSafe(month: Long) =
        database.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()

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
