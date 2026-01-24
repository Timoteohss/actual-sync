package com.actualbudget.sync.auth

import com.actualbudget.sync.http.RetryConfig
import com.actualbudget.sync.http.withRetry
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Stateless authentication client for Actual Budget servers.
 *
 * This client handles only authentication - it has no state and returns
 * an [AuthSession] that can be used with other clients.
 *
 * Usage:
 * ```
 * val authClient = ActualAuthClient(httpClient)
 * val session = authClient.login(serverUrl, password)
 * // Use session with ActualFileClient, SyncManager, etc.
 * ```
 *
 * IMPORTANT: The caller is responsible for managing the HttpClient lifecycle.
 * Call httpClient.close() when done to prevent resource leaks.
 */
class ActualAuthClient(
    private val httpClient: HttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Authenticate with an Actual Budget server.
     *
     * Includes automatic retry for transient network failures.
     *
     * @param serverUrl The base URL of the server (e.g., "https://actual.example.com")
     * @param password The server password
     * @param retryConfig Retry configuration (default: up to 3 retries with exponential backoff)
     * @return An [AuthSession] containing the authentication token
     * @throws AuthException.LoginFailed if login fails with HTTP error
     * @throws AuthException.InvalidResponse if response is malformed
     * @throws AuthException.NetworkError if network error persists after retries
     */
    @Throws(Exception::class)
    suspend fun login(
        serverUrl: String,
        password: String,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): AuthSession {
        val normalizedUrl = serverUrl.trimEnd('/')

        // Use proper JSON serialization to prevent injection attacks
        val requestBody = json.encodeToString(LoginRequest(password))

        return withRetry(
            config = retryConfig,
            operation = "login to $normalizedUrl"
        ) {
            val response = httpClient.post("$normalizedUrl/account/login") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (!response.status.isSuccess()) {
                // Don't retry auth failures (401, 403) - only transient errors
                if (response.status.value in listOf(401, 403)) {
                    throw AuthException.LoginFailed(response.status.value, "Login failed: ${response.status}")
                }
                throw AuthException.LoginFailed(response.status.value, "Login failed: ${response.status}")
            }

            val responseText = response.bodyAsText()
            val loginResponse = json.decodeFromString<LoginResponse>(responseText)
            val token = loginResponse.data?.token
                ?: throw AuthException.InvalidResponse("No token in login response")

            AuthSession(
                serverUrl = normalizedUrl,
                token = token,
                httpClient = httpClient
            )
        }
    }

    /**
     * Create a session from a previously saved token.
     *
     * Use this to restore a session without re-authenticating.
     *
     * @param serverUrl The base URL of the server
     * @param token A previously obtained authentication token
     * @return An [AuthSession] that can be used for authenticated requests
     */
    fun createSession(serverUrl: String, token: String): AuthSession {
        return AuthSession(
            serverUrl = serverUrl.trimEnd('/'),
            token = token,
            httpClient = httpClient
        )
    }

    /**
     * Validate that a session's token is still valid by making a test request.
     *
     * Includes automatic retry for transient network failures.
     *
     * @param session The session to validate
     * @param retryConfig Retry configuration (default: up to 3 retries)
     * @return true if the session is still valid, false if invalid or network fails after retries
     */
    @Throws(Exception::class)
    suspend fun validateSession(
        session: AuthSession,
        retryConfig: RetryConfig = RetryConfig.DEFAULT
    ): Boolean {
        return try {
            withRetry(
                config = retryConfig,
                operation = "validate session"
            ) {
                val response = session.httpClient.get("${session.serverUrl}/sync/list-user-files") {
                    header("X-ACTUAL-TOKEN", session.token)
                }
                response.status.isSuccess()
            }
        } catch (e: Exception) {
            // Network failed after all retries, or auth is invalid
            false
        }
    }

    /**
     * Close the underlying HTTP client.
     * Call this when you're done using the auth client to prevent resource leaks.
     */
    fun close() {
        httpClient.close()
    }
}

// Request/Response models for JSON parsing

@Serializable
internal data class LoginRequest(
    val password: String
)

@Serializable
internal data class LoginResponse(
    val status: String? = null,
    val data: LoginData? = null
)

@Serializable
internal data class LoginData(
    val token: String? = null
)

/**
 * Sealed class for authentication-related exceptions.
 * Provides structured error handling instead of generic exceptions.
 */
sealed class AuthException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    /**
     * Login request failed with an HTTP error.
     */
    class LoginFailed(val statusCode: Int, message: String) : AuthException(message)

    /**
     * Server response was invalid or missing expected data.
     */
    class InvalidResponse(message: String) : AuthException(message)

    /**
     * Network error occurred during authentication.
     */
    class NetworkError(message: String, cause: Throwable) : AuthException(message, cause)
}
