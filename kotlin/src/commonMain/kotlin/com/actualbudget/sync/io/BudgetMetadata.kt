package com.actualbudget.sync.io

import kotlinx.serialization.Serializable

/**
 * Metadata for a budget file, used when uploading to the server.
 * The server expects a metadata.json file in the uploaded ZIP.
 */
@Serializable
data class BudgetMetadata(
    /** Unique identifier for the budget file */
    val id: String,
    /** Display name of the budget */
    val budgetName: String,
    /** Whether to reset the clock on upload (typically true for uploads) */
    val resetClock: Boolean = true,
    /** Cloud file ID if previously synced */
    val cloudFileId: String? = null,
    /** Group ID for sync operations */
    val groupId: String? = null,
    /** ISO 8601 timestamp of last upload */
    val lastUploaded: String? = null,
    /** Encryption key ID if the budget is encrypted */
    val encryptKeyId: String? = null
)
