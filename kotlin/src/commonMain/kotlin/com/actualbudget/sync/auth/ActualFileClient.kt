package com.actualbudget.sync.auth

import com.actualbudget.sync.http.RetryConfig
import com.actualbudget.sync.http.withRetry
import com.actualbudget.sync.sync.BudgetFile
import com.actualbudget.sync.sync.UploadResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Client for file operations on an Actual Budget server.
 *
 * All operations require an [AuthSession] - this client is stateless
 * and does not store any authentication information.
 *
 * Usage:
 * ```
 * val fileClient = ActualFileClient()
 * val budgets = fileClient.listFiles(session)
 * val zipData = fileClient.downloadBudget(session, budgetId)
 * ```
 */
class ActualFileClient {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * List available budget files on the server.
     *
     * Includes automatic retry for transient network failures.
     *
     * @param session Authenticated session
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @return List of available budget files
     * @throws FileClientException if request fails after retries
     */
    @Throws(Exception::class)
    suspend fun listFiles(
        session: AuthSession,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): List<BudgetFile> {
        return withRetry(
            config = retryConfig,
            operation = "list files"
        ) {
            val response = session.httpClient.get("${session.serverUrl}/sync/list-user-files") {
                header("X-ACTUAL-TOKEN", session.token)
            }

            if (!response.status.isSuccess()) {
                throw FileClientException.RequestFailed(response.status.value, "Failed to list files")
            }

            val responseText = response.bodyAsText()
            val listResponse = json.decodeFromString<ListFilesResponse>(responseText)

            listResponse.data?.map { file ->
                BudgetFile(
                    id = file.fileId ?: file.id ?: "",
                    name = file.name ?: "Unnamed Budget",
                    groupId = file.groupId,
                    encryptKeyId = file.encryptKeyId,
                    deleted = (file.deleted ?: 0) != 0
                )
            } ?: emptyList()
        }
    }

    /**
     * Download a budget file from the server.
     *
     * Includes automatic retry for transient network failures.
     *
     * @param session Authenticated session
     * @param fileId The budget file ID to download
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @return Raw bytes of the budget zip file
     * @throws FileClientException if download fails after retries
     */
    @Throws(Exception::class)
    suspend fun downloadBudget(
        session: AuthSession,
        fileId: String,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): ByteArray {
        return withRetry(
            config = retryConfig,
            operation = "download budget $fileId"
        ) {
            val response = session.httpClient.get("${session.serverUrl}/sync/download-user-file") {
                header("X-ACTUAL-TOKEN", session.token)
                header("X-ACTUAL-FILE-ID", fileId)
            }

            if (!response.status.isSuccess()) {
                throw FileClientException.RequestFailed(response.status.value, "Failed to download budget")
            }

            response.readRawBytes()
        }
    }

    /**
     * Upload a budget file to the server.
     *
     * Includes automatic retry for transient network failures.
     * Uploads are idempotent so retrying is safe.
     *
     * @param session Authenticated session
     * @param fileId The budget file ID
     * @param name The budget name
     * @param data The budget zip file data
     * @param groupId Optional group ID (for existing budgets)
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @return Upload response with status and groupId
     * @throws FileClientException if upload fails after retries
     */
    @Throws(Exception::class)
    suspend fun uploadBudget(
        session: AuthSession,
        fileId: String,
        name: String,
        data: ByteArray,
        groupId: String? = null,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): UploadResponse {
        // URL encode the name as the server expects decodeURIComponent()
        val encodedName = name.encodeURLParameter()

        return withRetry(
            config = retryConfig,
            operation = "upload budget $fileId"
        ) {
            val response = session.httpClient.post("${session.serverUrl}/sync/upload-user-file") {
                header("X-ACTUAL-TOKEN", session.token)
                header("X-ACTUAL-FILE-ID", fileId)
                header("X-ACTUAL-NAME", encodedName)
                header("X-ACTUAL-FORMAT", "2")
                groupId?.let { header("X-ACTUAL-GROUP-ID", it) }
                contentType(ContentType("application", "encrypted-file"))
                setBody(data)
            }

            if (!response.status.isSuccess()) {
                throw FileClientException.RequestFailed(response.status.value, "Failed to upload budget")
            }

            val responseText = response.bodyAsText()
            try {
                json.decodeFromString<UploadResponse>(responseText)
            } catch (e: Exception) {
                // Fallback if response parsing fails but upload succeeded
                UploadResponse(status = "ok", groupId = groupId)
            }
        }
    }

    /**
     * Delete a budget file from the server.
     *
     * Includes automatic retry for transient network failures.
     * Deletes are idempotent so retrying is safe.
     *
     * @param session Authenticated session
     * @param fileId The budget file ID to delete
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @throws FileClientException if deletion fails after retries
     */
    @Throws(Exception::class)
    suspend fun deleteBudget(
        session: AuthSession,
        fileId: String,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ) {
        // Use proper JSON serialization to prevent injection attacks
        val requestBody = json.encodeToString(DeleteRequest(fileId))

        withRetry(
            config = retryConfig,
            operation = "delete budget $fileId"
        ) {
            val response = session.httpClient.post("${session.serverUrl}/sync/delete-user-file") {
                header("X-ACTUAL-TOKEN", session.token)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (!response.status.isSuccess()) {
                throw FileClientException.RequestFailed(response.status.value, "Failed to delete budget")
            }
        }
    }
}

// Internal request/response models for JSON parsing

@Serializable
internal data class DeleteRequest(
    val fileId: String
)

@Serializable
internal data class ListFilesResponse(
    val status: String? = null,
    val data: List<FileData>? = null
)

@Serializable
internal data class FileData(
    val id: String? = null,
    val fileId: String? = null,
    val name: String? = null,
    val groupId: String? = null,
    val encryptKeyId: String? = null,
    val deleted: Int? = null
)

/**
 * Sealed class for file client exceptions.
 * Provides structured error handling instead of generic exceptions.
 */
sealed class FileClientException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /**
     * HTTP request failed.
     */
    class RequestFailed(val statusCode: Int, message: String) : FileClientException(message)

    /**
     * Server response was invalid or missing expected data.
     */
    class InvalidResponse(message: String) : FileClientException(message)
}
