package com.actualbudget.sync.http

import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow

/**
 * Configuration for retry behavior.
 *
 * @property maxRetries Maximum number of retry attempts (0 = no retries)
 * @property initialDelayMs Initial delay before first retry
 * @property maxDelayMs Maximum delay between retries
 * @property backoffMultiplier Multiplier for exponential backoff
 */
data class RetryConfig(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1000L,
    val maxDelayMs: Long = 30_000L,
    val backoffMultiplier: Double = 2.0
) {
    companion object {
        /** Default retry configuration for most operations */
        val DEFAULT = RetryConfig()

        /** No retries - use for non-idempotent operations */
        val NONE = RetryConfig(maxRetries = 0)

        /** Aggressive retry for critical operations */
        val AGGRESSIVE = RetryConfig(
            maxRetries = 5,
            initialDelayMs = 500L,
            maxDelayMs = 60_000L
        )
    }
}

/**
 * Determines if an exception is retryable (transient).
 */
fun Throwable.isRetryable(): Boolean {
    // Check for Ktor timeout exceptions
    if (this is HttpRequestTimeoutException) return true

    // Check for common network exception patterns in message
    val message = this.message?.lowercase() ?: ""
    val className = this::class.simpleName?.lowercase() ?: ""

    return message.contains("timeout") ||
           message.contains("connection") ||
           message.contains("network") ||
           message.contains("socket") ||
           message.contains("reset") ||
           message.contains("refused") ||
           message.contains("unreachable") ||
           className.contains("timeout") ||
           className.contains("connect")
}

/**
 * Determines if an HTTP status code is retryable.
 *
 * Retryable codes:
 * - 408 Request Timeout
 * - 429 Too Many Requests
 * - 500 Internal Server Error
 * - 502 Bad Gateway
 * - 503 Service Unavailable
 * - 504 Gateway Timeout
 */
fun HttpStatusCode.isRetryable(): Boolean {
    return value in listOf(408, 429, 500, 502, 503, 504)
}

/**
 * Exception thrown when all retries are exhausted.
 */
class RetryExhaustedException(
    val attempts: Int,
    val lastException: Throwable
) : Exception("All $attempts retry attempts exhausted", lastException)

/**
 * Execute a suspending block with retry logic.
 *
 * @param config Retry configuration
 * @param operation Description of the operation (for logging)
 * @param shouldRetry Optional predicate to determine if an exception should trigger retry
 * @param block The suspending block to execute
 * @return Result of the block
 * @throws RetryExhaustedException if all retries are exhausted
 */
suspend fun <T> withRetry(
    config: RetryConfig = RetryConfig.DEFAULT,
    operation: String = "operation",
    shouldRetry: (Throwable) -> Boolean = { it.isRetryable() },
    block: suspend () -> T
): T {
    var lastException: Throwable? = null
    var currentDelay = config.initialDelayMs

    repeat(config.maxRetries + 1) { attempt ->
        try {
            return block()
        } catch (e: Throwable) {
            lastException = e

            // Don't retry if we've exhausted attempts or error isn't retryable
            val isLastAttempt = attempt >= config.maxRetries
            if (isLastAttempt || !shouldRetry(e)) {
                throw e
            }

            // Log retry attempt
            println("[Retry] $operation failed (attempt ${attempt + 1}/${config.maxRetries + 1}): ${e.message}")
            println("[Retry] Retrying in ${currentDelay}ms...")

            // Wait before retry with exponential backoff
            delay(currentDelay)
            currentDelay = min(
                (currentDelay * config.backoffMultiplier).toLong(),
                config.maxDelayMs
            )
        }
    }

    // Should never reach here, but just in case
    throw RetryExhaustedException(config.maxRetries + 1, lastException!!)
}

/**
 * Execute a suspending block with retry logic for HTTP operations.
 * Automatically handles both exceptions and retryable HTTP status codes.
 *
 * @param config Retry configuration
 * @param operation Description of the operation (for logging)
 * @param block The suspending block that returns an HTTP response
 * @return Result of the block
 */
suspend fun <T> withHttpRetry(
    config: RetryConfig = RetryConfig.DEFAULT,
    operation: String = "HTTP request",
    isRetryableStatus: (HttpStatusCode) -> Boolean = { it.isRetryable() },
    block: suspend () -> T
): T {
    return withRetry(
        config = config,
        operation = operation,
        shouldRetry = { e ->
            e.isRetryable() || (e is HttpStatusException && isRetryableStatus(e.statusCode))
        },
        block = block
    )
}

/**
 * Custom exception for HTTP status errors that need special handling.
 */
class HttpStatusException(
    val statusCode: HttpStatusCode,
    message: String
) : Exception(message)
