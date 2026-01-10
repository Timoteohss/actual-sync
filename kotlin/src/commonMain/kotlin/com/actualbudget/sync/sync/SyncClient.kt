package com.actualbudget.sync.sync

import com.actualbudget.sync.crdt.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Set the authentication token manually.
     */
    fun setToken(token: String) {
        this.authToken = token
    }

    /**
     * Get the current authentication token.
     */
    fun getToken(): String? = authToken

    /**
     * Authenticate with the server.
     * @throws Exception if login fails
     */
    @Throws(Exception::class)
    suspend fun login(password: String): String {
        println("[SyncClient] Login attempt to: $serverUrl/account/login")
        val response = httpClient.post("$serverUrl/account/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"password":"$password"}""")
        }

        println("[SyncClient] Login response status: ${response.status}")
        if (!response.status.isSuccess()) {
            throw Exception("Login failed: ${response.status}")
        }

        val responseText = response.bodyAsText()
        println("[SyncClient] Login response body: $responseText")
        val loginResponse = json.decodeFromString<LoginResponse>(responseText)
        authToken = loginResponse.data?.token
        println("[SyncClient] Parsed token: ${authToken?.take(20)}...")
        return authToken ?: throw Exception("No token in response")
    }

    /**
     * List available budget files.
     * @throws Exception if request fails
     */
    @Throws(Exception::class)
    suspend fun listFiles(): List<BudgetFile> {
        println("[SyncClient] listFiles - token: ${authToken?.take(20)}...")
        println("[SyncClient] listFiles - GET $serverUrl/sync/list-user-files")
        val response = httpClient.get("$serverUrl/sync/list-user-files") {
            authToken?.let {
                println("[SyncClient] Adding X-ACTUAL-TOKEN header")
                header("X-ACTUAL-TOKEN", it)
            }
        }

        println("[SyncClient] listFiles response status: ${response.status}")
        if (!response.status.isSuccess()) {
            throw Exception("Failed to list files: ${response.status}")
        }

        val responseText = response.bodyAsText()
        println("[SyncClient] listFiles response body: $responseText")
        val listResponse = json.decodeFromString<ListFilesResponse>(responseText)
        println("[SyncClient] listFiles parsed data: ${listResponse.data?.size ?: 0} items")
        val files = listResponse.data?.map { file ->
            println("[SyncClient]   file: id=${file.id}, fileId=${file.fileId}, name=${file.name}, deleted=${file.deleted}, encryptKeyId=${file.encryptKeyId}")
            BudgetFile(
                id = file.fileId ?: file.id ?: "",
                name = file.name ?: "Unnamed Budget",
                groupId = file.groupId,
                encryptKeyId = file.encryptKeyId,
                deleted = (file.deleted ?: 0) != 0  // Convert 0/1 to boolean
            )
        } ?: emptyList()
        println("[SyncClient] listFiles returning ${files.size} files")
        return files
    }

    /**
     * Download a budget file.
     * @throws Exception if download fails
     */
    @Throws(Exception::class)
    suspend fun downloadBudget(syncId: String): ByteArray {
        val response = httpClient.get("$serverUrl/sync/download-user-file") {
            authToken?.let { header("X-ACTUAL-TOKEN", it) }
            header("X-ACTUAL-FILE-ID", syncId)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to download budget: ${response.status}")
        }

        fileId = syncId
        return response.readRawBytes()
    }

    /**
     * Sync messages with the server.
     *
     * @param messages Local messages to send
     * @param since Timestamp to sync from
     * @return New messages from server and updated merkle trie
     * @throws NotImplementedError sync not yet implemented
     */
    @Throws(Exception::class)
    suspend fun sync(
        messages: List<MessageEnvelope>,
        since: String
    ): SyncResponse {
        // TODO: Implement protobuf serialization
        // 1. Build SyncRequest protobuf
        // 2. POST to /sync/sync with content-type application/actual-sync
        // 3. Parse SyncResponse protobuf
        throw NotImplementedError("Sync not yet implemented")
    }

    /**
     * Upload budget file to server.
     * @throws Exception if upload fails
     */
    @Throws(Exception::class)
    suspend fun uploadBudget(
        fileId: String,
        name: String,
        data: ByteArray,
        groupId: String? = null
    ): String {
        val response = httpClient.post("$serverUrl/sync/upload-user-file") {
            authToken?.let { header("X-ACTUAL-TOKEN", it) }
            header("X-ACTUAL-FILE-ID", fileId)
            header("X-ACTUAL-NAME", name)
            groupId?.let { header("X-ACTUAL-GROUP-ID", it) }
            contentType(ContentType("application", "encrypted-file"))
            setBody(data)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to upload budget: ${response.status}")
        }

        // Parse groupId from response
        return groupId ?: ""
    }
}

// Response models for JSON parsing

@Serializable
data class LoginResponse(
    val status: String? = null,
    val data: LoginData? = null
)

@Serializable
data class LoginData(
    val token: String? = null
)

@Serializable
data class ListFilesResponse(
    val status: String? = null,
    val data: List<FileData>? = null
)

@Serializable
data class FileData(
    val id: String? = null,
    val fileId: String? = null,
    val name: String? = null,
    val groupId: String? = null,
    val encryptKeyId: String? = null,
    val deleted: Int? = null  // Server sends 0/1 instead of boolean
)

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
