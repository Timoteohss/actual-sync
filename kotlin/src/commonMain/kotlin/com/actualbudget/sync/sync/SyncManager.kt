package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.Merkle
import com.actualbudget.sync.crdt.MutableClock
import com.actualbudget.sync.crdt.Timestamp
import com.actualbudget.sync.db.ActualDatabase
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
        if (payeeId != null) engine.createChange("transactions", id, "payee", payeeId)
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
