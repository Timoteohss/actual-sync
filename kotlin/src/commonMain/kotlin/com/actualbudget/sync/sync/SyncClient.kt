package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Client for syncing with Actual Budget server.
 */
class SyncClient(
    private val serverUrl: String,
    private val httpClient: HttpClient = HttpClient()
) {
    private var authToken: String? = null
    private var fileId: String? = null
    private var groupId: String? = null
    private var keyId: String? = null

    /**
     * Authenticate with the server.
     */
    suspend fun login(password: String): Result<String> {
        return try {
            val response = httpClient.post("$serverUrl/account/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("password" to password))
            }

            if (response.status.isSuccess()) {
                // Parse token from response
                // authToken = ...
                Result.success("Authenticated")
            } else {
                Result.failure(Exception("Login failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * List available budget files.
     */
    suspend fun listFiles(): Result<List<BudgetFile>> {
        return try {
            val response = httpClient.get("$serverUrl/sync/list-user-files") {
                authToken?.let { header("X-ACTUAL-TOKEN", it) }
            }

            if (response.status.isSuccess()) {
                // Parse files from response
                Result.success(emptyList())
            } else {
                Result.failure(Exception("Failed to list files: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Download a budget file.
     */
    suspend fun downloadBudget(syncId: String): Result<ByteArray> {
        return try {
            val response = httpClient.get("$serverUrl/sync/download-user-file") {
                authToken?.let { header("X-ACTUAL-TOKEN", it) }
                header("X-ACTUAL-FILE-ID", syncId)
            }

            if (response.status.isSuccess()) {
                fileId = syncId
                Result.success(response.readRawBytes())
            } else {
                Result.failure(Exception("Failed to download budget: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sync messages with the server.
     *
     * @param messages Local messages to send
     * @param since Timestamp to sync from
     * @return New messages from server and updated merkle trie
     */
    suspend fun sync(
        messages: List<MessageEnvelope>,
        since: String
    ): Result<SyncResponse> {
        // TODO: Implement protobuf serialization
        // 1. Build SyncRequest protobuf
        // 2. POST to /sync/sync with content-type application/actual-sync
        // 3. Parse SyncResponse protobuf
        return Result.failure(NotImplementedError("Sync not yet implemented"))
    }

    /**
     * Upload budget file to server.
     */
    suspend fun uploadBudget(
        fileId: String,
        name: String,
        data: ByteArray,
        groupId: String? = null
    ): Result<String> {
        return try {
            val response = httpClient.post("$serverUrl/sync/upload-user-file") {
                authToken?.let { header("X-ACTUAL-TOKEN", it) }
                header("X-ACTUAL-FILE-ID", fileId)
                header("X-ACTUAL-NAME", name)
                groupId?.let { header("X-ACTUAL-GROUP-ID", it) }
                contentType(ContentType("application", "encrypted-file"))
                setBody(data)
            }

            if (response.status.isSuccess()) {
                // Parse groupId from response
                Result.success(groupId ?: "")
            } else {
                Result.failure(Exception("Failed to upload budget: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Represents a budget file on the server.
 */
data class BudgetFile(
    val id: String,
    val name: String,
    val groupId: String?,
    val encryptKeyId: String?,
    val deleted: Boolean = false
)

/**
 * Envelope for sync messages.
 */
data class MessageEnvelope(
    val timestamp: String,
    val isEncrypted: Boolean,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MessageEnvelope
        return timestamp == other.timestamp &&
                isEncrypted == other.isEncrypted &&
                content.contentEquals(other.content)
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + isEncrypted.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}

/**
 * Response from sync operation.
 */
data class SyncResponse(
    val messages: List<MessageEnvelope>,
    val merkle: TrieNode
)
