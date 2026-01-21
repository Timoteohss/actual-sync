package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.*
import com.actualbudget.sync.db.ActualDatabase
import com.actualbudget.sync.proto.Message
import com.actualbudget.sync.proto.MessageEnvelope
import com.actualbudget.sync.proto.SyncRequest
import com.actualbudget.sync.proto.SyncResponse

/**
 * Engine for bidirectional CRDT sync with the Actual Budget server.
 *
 * Handles:
 * - Generating messages for local changes
 * - Tracking pending messages to send
 * - Merkle trie comparison for efficient sync
 * - Applying remote messages locally
 */
class SyncEngine(
    private val db: ActualDatabase,
    private val clock: MutableClock
) {
    private var localMerkle: TrieNode = Merkle.emptyTrie()
    private val pendingMessages = mutableListOf<MessageEnvelope>()

    /**
     * Initialize the sync engine by loading local merkle state.
     */
    fun initialize() {
        // Load merkle from database
        val merkleJson = db.actualDatabaseQueries.getSyncMetadata("merkle").executeAsOneOrNull()?.value_
        if (merkleJson != null) {
            localMerkle = Merkle.deserialize(merkleJson)
        }

        // Rebuild merkle from stored messages to ensure consistency
        rebuildMerkleFromMessages()
    }

    /**
     * Rebuild the local merkle trie from stored CRDT messages.
     */
    private fun rebuildMerkleFromMessages() {
        val messages = db.actualDatabaseQueries.getMessagesSince("").executeAsList()
        var trie = Merkle.emptyTrie()

        for (msg in messages) {
            val ts = Timestamp.parse(msg.timestamp)
            if (ts != null) {
                trie = Merkle.insert(trie, ts)
            }
        }

        localMerkle = trie
    }

    /**
     * Create a local change and generate a CRDT message.
     * The message is added to pending queue for next sync.
     */
    fun createChange(dataset: String, row: String, column: String, value: Any?): MessageEnvelope {
        val ts = clock.send()
        val encodedValue = encodeValue(value)

        val message = Message(
            dataset = dataset,
            row = row,
            column = column,
            value = encodedValue
        )

        val envelope = MessageEnvelope.create(ts.toString(), message)

        // Store in local database (value stored as BLOB)
        db.actualDatabaseQueries.insertMessage(
            timestamp = ts.toString(),
            dataset = dataset,
            row = row,
            column = column,
            value_ = encodedValue.encodeToByteArray()
        )

        // Apply the change to entity tables immediately
        val parsedValue = parseValue(encodedValue)
        when (dataset) {
            "accounts" -> applyToAccount(row, column, parsedValue)
            "payees" -> applyToPayee(row, column, parsedValue)
            "payee_mapping" -> applyToPayeeMapping(row, column, parsedValue)
            "categories" -> applyToCategory(row, column, parsedValue)
            "category_groups" -> applyToCategoryGroup(row, column, parsedValue)
            "transactions" -> applyToTransaction(row, column, parsedValue)
        }

        // Update local merkle
        localMerkle = Merkle.insert(localMerkle, ts)

        // Add to pending queue
        pendingMessages.add(envelope)

        return envelope
    }

    /**
     * Encode a value in Actual's typed format.
     */
    private fun encodeValue(value: Any?): String {
        return when (value) {
            null -> "0:"
            is String -> "S:$value"
            is Number -> "N:$value"
            is Boolean -> "N:${if (value) 1 else 0}"
            else -> "S:$value"
        }
    }

    /**
     * Get pending messages that need to be sent to server.
     */
    fun getPendingMessages(): List<MessageEnvelope> = pendingMessages.toList()

    /**
     * Clear pending messages after successful sync.
     */
    fun clearPendingMessages() {
        pendingMessages.clear()
    }

    /**
     * Build a sync request for the server.
     *
     * @param fileId The budget file ID
     * @param groupId The budget group ID
     * @param fullSync If true, request all messages from beginning
     */
    fun buildSyncRequest(fileId: String, groupId: String, fullSync: Boolean = false): SyncRequest {
        val since = if (fullSync) {
            "1970-01-01T00:00:00.000Z-0000-0000000000000000"
        } else {
            // Get last sync timestamp
            db.actualDatabaseQueries.getLastTimestamp().executeAsOneOrNull()?.last_ts
                ?: "1970-01-01T00:00:00.000Z-0000-0000000000000000"
        }

        return SyncRequest(
            messages = pendingMessages.toList(),
            fileId = fileId,
            groupId = groupId,
            since = since
        )
    }

    /**
     * Build an incremental sync request based on merkle comparison.
     *
     * @param fileId The budget file ID
     * @param groupId The budget group ID
     * @param serverMerkle The server's merkle trie (from previous sync response)
     */
    fun buildIncrementalSyncRequest(
        fileId: String,
        groupId: String,
        serverMerkle: TrieNode
    ): SyncRequest {
        // Find divergence point
        val diffTimestamp = Merkle.diff(localMerkle, serverMerkle)

        val since = if (diffTimestamp != null) {
            // Convert milliseconds to timestamp string
            val ts = Timestamp(millis = diffTimestamp, counter = 0, node = "0000000000000000")
            ts.toString()
        } else {
            // In sync, just send pending messages
            db.actualDatabaseQueries.getLastTimestamp().executeAsOneOrNull()?.last_ts
                ?: "1970-01-01T00:00:00.000Z-0000-0000000000000000"
        }

        return SyncRequest(
            messages = pendingMessages.toList(),
            fileId = fileId,
            groupId = groupId,
            since = since
        )
    }

    /**
     * Process a sync response from the server.
     * Applies remote messages and updates local merkle.
     *
     * @return Number of new messages applied
     */
    fun processSyncResponse(response: SyncResponse): Int {
        var applied = 0
        var skippedEncrypted = 0

        println("[SyncEngine] Processing sync response with ${response.messages.size} messages")

        // Apply remote messages
        for (envelope in response.messages) {
            if (envelope.isEncrypted) {
                skippedEncrypted++
                if (skippedEncrypted <= 5) {
                    println("[SyncEngine] SKIPPING encrypted message: ${envelope.timestamp}")
                }
            }
            if (!envelope.isEncrypted) {
                try {
                    val message = envelope.decodeMessage()
                    val ts = Timestamp.parse(envelope.timestamp)

                    // Check if we already have this message
                    val existingCount = db.actualDatabaseQueries.messageExists(envelope.timestamp)
                        .executeAsOne()

                    if (existingCount == 0L) {
                        // Store and apply
                        applyMessage(envelope.timestamp, message)
                        applied++
                    } else {
                        // Skip - already have this message
                        println("[SyncEngine] SKIP (exists): ${message.dataset}.${message.column} = '${message.value.take(50)}' (row: ${message.row.take(20)})")

                        // Update local merkle
                        if (ts != null) {
                            localMerkle = Merkle.insert(localMerkle, ts)
                        }
                    }
                } catch (e: Exception) {
                    println("Failed to apply message: ${e.message}")
                }
            }
        }

        // Update server merkle
        if (response.merkle.isNotEmpty()) {
            val serverMerkle = Merkle.deserialize(response.merkle)
            // Store for future incremental syncs
            db.actualDatabaseQueries.setSyncMetadata("server_merkle", response.merkle)
        }

        // Save local merkle
        db.actualDatabaseQueries.setSyncMetadata("merkle", Merkle.serialize(localMerkle))

        // Clear pending messages that were successfully sent
        clearPendingMessages()

        println("[SyncEngine] Sync complete: applied=$applied, skippedEncrypted=$skippedEncrypted, total=${response.messages.size}")

        return applied
    }

    /**
     * Apply a CRDT message to the database.
     */
    private fun applyMessage(timestamp: String, message: Message) {
        // Debug logging for all messages
        println("[SyncEngine] MSG: ${message.dataset}.${message.column} = '${message.value.take(50)}' (row: ${message.row.take(20)})")

        // Store in CRDT log (value stored as BLOB)
        db.actualDatabaseQueries.insertMessage(
            timestamp = timestamp,
            dataset = message.dataset,
            row = message.row,
            column = message.column,
            value_ = message.value.encodeToByteArray()
        )

        // Parse the value
        val parsedValue = parseValue(message.value)

        // Apply to appropriate table
        when (message.dataset) {
            "accounts" -> applyToAccount(message.row, message.column, parsedValue)
            "payees" -> applyToPayee(message.row, message.column, parsedValue)
            "payee_mapping" -> applyToPayeeMapping(message.row, message.column, parsedValue)
            "categories" -> applyToCategory(message.row, message.column, parsedValue)
            "category_groups" -> applyToCategoryGroup(message.row, message.column, parsedValue)
            "transactions" -> applyToTransaction(message.row, message.column, parsedValue)
        }
    }

    /**
     * Parse Actual's typed value format.
     */
    private fun parseValue(value: String): Any? {
        return when {
            value.startsWith("S:") -> value.substring(2)
            value.startsWith("N:") -> value.substring(2).toLongOrNull() ?: 0L
            value.startsWith("0:") -> null
            value == "null" -> null
            else -> value
        }
    }

    // ========== Apply methods for each table ==========

    private fun applyToAccount(id: String, column: String, value: Any?) {
        // Ensure exists
        val existing = db.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertAccount(id, "", 0, 0, null, 0)
        }

        val current = db.actualDatabaseQueries.getAccountById(id).executeAsOne()
        when (column) {
            "name" -> db.actualDatabaseQueries.insertAccount(id, value as? String ?: "", current.offbudget, current.closed, current.sort_order, current.tombstone)
            "offbudget" -> db.actualDatabaseQueries.insertAccount(id, current.name, (value as? Long) ?: 0L, current.closed, current.sort_order, current.tombstone)
            "closed" -> db.actualDatabaseQueries.insertAccount(id, current.name, current.offbudget, (value as? Long) ?: 0L, current.sort_order, current.tombstone)
            "sort_order" -> db.actualDatabaseQueries.insertAccount(id, current.name, current.offbudget, current.closed, (value as? Long)?.toDouble(), current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertAccount(id, current.name, current.offbudget, current.closed, current.sort_order, (value as? Long) ?: 0L)
        }
    }

    private fun applyToPayee(id: String, column: String, value: Any?) {
        val existing = db.actualDatabaseQueries.getPayeeById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertPayee(id, "", null, 0)
        }

        val current = db.actualDatabaseQueries.getPayeeById(id).executeAsOne()
        when (column) {
            "name" -> db.actualDatabaseQueries.insertPayee(id, value as? String ?: "", current.category, current.tombstone)
            "category" -> db.actualDatabaseQueries.insertPayee(id, current.name, value as? String, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertPayee(id, current.name, current.category, (value as? Long) ?: 0L)
        }
    }

    private fun applyToPayeeMapping(id: String, column: String, value: Any?) {
        val existing = db.actualDatabaseQueries.getPayeeMappingById(id).executeAsOneOrNull()
        if (existing == null) {
            // Default: mapping points to itself
            db.actualDatabaseQueries.insertPayeeMapping(id, id)
        }

        // NOTE: payee_mapping has only 'id' and 'targetId' columns (no tombstone)
        when (column) {
            "targetId" -> db.actualDatabaseQueries.insertPayeeMapping(id, value as? String ?: id)
        }
    }

    private fun applyToCategory(id: String, column: String, value: Any?) {
        val existing = db.actualDatabaseQueries.getCategoryById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertCategory(id, "", null, 0, null, 0, 0)
        }

        val current = db.actualDatabaseQueries.getCategoryById(id).executeAsOne()
        when (column) {
            "name" -> db.actualDatabaseQueries.insertCategory(id, value as? String ?: "", current.cat_group, current.is_income, current.sort_order, current.hidden, current.tombstone)
            "cat_group" -> db.actualDatabaseQueries.insertCategory(id, current.name, value as? String, current.is_income, current.sort_order, current.hidden, current.tombstone)
            "is_income" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, (value as? Long) ?: 0L, current.sort_order, current.hidden, current.tombstone)
            "sort_order" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, (value as? Long)?.toDouble(), current.hidden, current.tombstone)
            "hidden" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, current.sort_order, (value as? Long) ?: 0L, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertCategory(id, current.name, current.cat_group, current.is_income, current.sort_order, current.hidden, (value as? Long) ?: 0L)
        }
    }

    private fun applyToCategoryGroup(id: String, column: String, value: Any?) {
        val existing = db.actualDatabaseQueries.getCategoryGroupById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertCategoryGroup(id, "", 0, null, 0, 0)
        }

        val current = db.actualDatabaseQueries.getCategoryGroupById(id).executeAsOne()
        when (column) {
            "name" -> db.actualDatabaseQueries.insertCategoryGroup(id, value as? String ?: "", current.is_income, current.sort_order, current.hidden, current.tombstone)
            "is_income" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, (value as? Long) ?: 0L, current.sort_order, current.hidden, current.tombstone)
            "sort_order" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, (value as? Long)?.toDouble(), current.hidden, current.tombstone)
            "hidden" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, current.sort_order, (value as? Long) ?: 0L, current.tombstone)
            "tombstone" -> db.actualDatabaseQueries.insertCategoryGroup(id, current.name, current.is_income, current.sort_order, current.hidden, (value as? Long) ?: 0L)
        }
    }

    private fun applyToTransaction(id: String, column: String, value: Any?) {
        val existing = db.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull()
        if (existing == null) {
            db.actualDatabaseQueries.insertTransaction(id, null, null, 0, null, null, null, null, 0, 1, 0, 0)
        }

        val current = db.actualDatabaseQueries.getTransactionById(id).executeAsOne()
        val updated = when (column) {
            "acct", "account" -> current.copy(acct = value as? String)
            "category" -> current.copy(category = value as? String)
            "amount" -> current.copy(amount = (value as? Long))
            // CRITICAL: Actual server uses "description" column to store payee ID
            "description", "payee" -> current.copy(description = value as? String)
            "notes" -> current.copy(notes = value as? String)
            "date" -> current.copy(date = value as? Long)
            "cleared" -> current.copy(cleared = (value as? Long))
            "pending" -> current.copy(pending = (value as? Long))
            "reconciled" -> current.copy(reconciled = (value as? Long))
            "sort_order" -> current.copy(sort_order = (value as? Long)?.toDouble())
            "tombstone" -> current.copy(tombstone = (value as? Long))
            // Ignore columns that don't exist in minimal schema
            else -> current
        }

        db.actualDatabaseQueries.insertTransaction(
            updated.id, updated.acct, updated.category, updated.amount ?: 0, updated.description,
            updated.notes, updated.date, updated.sort_order, updated.tombstone ?: 0, updated.cleared ?: 1,
            updated.pending ?: 0, updated.reconciled ?: 0
        )
    }

    /**
     * Get the local merkle trie.
     */
    fun getLocalMerkle(): TrieNode = localMerkle

    /**
     * Get the stored server merkle trie.
     */
    fun getServerMerkle(): TrieNode? {
        val json = db.actualDatabaseQueries.getSyncMetadata("server_merkle").executeAsOneOrNull()?.value_
        return json?.let { Merkle.deserialize(it) }
    }

    /**
     * Check if local and server are in sync.
     */
    fun isInSync(): Boolean {
        val serverMerkle = getServerMerkle() ?: return false
        return Merkle.diff(localMerkle, serverMerkle) == null
    }
}
