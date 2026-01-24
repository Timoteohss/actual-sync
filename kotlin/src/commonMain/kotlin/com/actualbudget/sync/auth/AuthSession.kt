package com.actualbudget.sync.auth

import io.ktor.client.*

/**
 * Holds authenticated session state for communicating with an Actual Budget server.
 *
 * This is the single source of truth for authentication - pass this to any
 * client or manager that needs to make authenticated requests.
 *
 * @property serverUrl The base URL of the Actual Budget server
 * @property token The authentication token obtained from login
 * @property httpClient The HTTP client to use for requests
 */
data class AuthSession(
    val serverUrl: String,
    val token: String,
    val httpClient: HttpClient
) {
    /**
     * Check if this session appears valid (has non-empty token and URL).
     */
    val isValid: Boolean
        get() = token.isNotBlank() && serverUrl.isNotBlank()
}
