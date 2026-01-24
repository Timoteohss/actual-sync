package com.actualbudget.sync.http

import io.ktor.client.*
import io.ktor.client.plugins.*

/**
 * Default timeout configuration for HTTP requests.
 *
 * These values are tuned for typical mobile network conditions:
 * - Connect: 15s allows for slow initial connections
 * - Request: 30s allows for large budget uploads
 * - Socket: 60s allows for slow server processing
 */
object HttpTimeouts {
    /** Time to establish connection (milliseconds) */
    const val CONNECT_TIMEOUT_MS = 15_000L

    /** Time for entire request/response cycle (milliseconds) */
    const val REQUEST_TIMEOUT_MS = 30_000L

    /** Time between socket read/write operations (milliseconds) */
    const val SOCKET_TIMEOUT_MS = 60_000L

    /** Shorter timeout for quick operations like auth validation */
    const val QUICK_TIMEOUT_MS = 10_000L

    /** Longer timeout for large file operations */
    const val UPLOAD_TIMEOUT_MS = 120_000L
}

/**
 * Configure an HttpClient with default timeouts.
 *
 * Usage:
 * ```
 * val client = HttpClient {
 *     configureTimeouts()
 * }
 * ```
 */
fun HttpClientConfig<*>.configureTimeouts(
    connectTimeoutMs: Long = HttpTimeouts.CONNECT_TIMEOUT_MS,
    requestTimeoutMs: Long = HttpTimeouts.REQUEST_TIMEOUT_MS,
    socketTimeoutMs: Long = HttpTimeouts.SOCKET_TIMEOUT_MS
) {
    install(HttpTimeout) {
        connectTimeoutMillis = connectTimeoutMs
        requestTimeoutMillis = requestTimeoutMs
        socketTimeoutMillis = socketTimeoutMs
    }
}

/**
 * Create an HttpClient with default timeout configuration.
 *
 * @param block Additional configuration for the client
 * @return Configured HttpClient
 */
fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient {
    return HttpClient {
        configureTimeouts()
        block()
    }
}
