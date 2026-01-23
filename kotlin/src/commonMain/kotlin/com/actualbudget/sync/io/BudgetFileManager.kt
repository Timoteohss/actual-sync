package com.actualbudget.sync.io

/**
 * Platform-specific file manager for budget operations.
 */
expect class BudgetFileManager() {
    /**
     * Extract a budget zip file and return the path to the extracted db.sqlite.
     *
     * @param zipData The raw bytes of the zip file
     * @param targetDir The directory to extract to
     * @return Path to the extracted database file, or null if extraction failed
     */
    fun extractBudgetZip(zipData: ByteArray, targetDir: String): String?

    /**
     * Get the default directory for storing budget data.
     */
    fun getDefaultBudgetDir(): String

    /**
     * Delete a file or directory.
     */
    fun delete(path: String): Boolean

    /**
     * Check if a file exists.
     */
    fun exists(path: String): Boolean

    /**
     * Copy a file from source to destination.
     */
    fun copy(source: String, destination: String): Boolean

    /**
     * Read a file and return its contents as a ByteArray.
     *
     * @param path The absolute path to the file
     * @return File contents as ByteArray, or null if read failed
     */
    fun readFile(path: String): ByteArray?

    /**
     * Write data to a file.
     *
     * @param path The absolute path to the file
     * @param data The data to write
     * @return true if write succeeded, false otherwise
     */
    fun writeFile(path: String, data: ByteArray): Boolean

    /**
     * Get the platform-specific temporary directory.
     *
     * @return Path to temporary directory
     */
    fun getTempDir(): String
}
