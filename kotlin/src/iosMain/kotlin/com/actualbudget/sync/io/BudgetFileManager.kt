package com.actualbudget.sync.io

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.memcpy
import platform.posix.remove
import platform.zlib.*

@OptIn(ExperimentalForeignApi::class)
actual class BudgetFileManager actual constructor() {

    actual fun extractBudgetZip(zipData: ByteArray, targetDir: String): String? {
        return try {
            val fileManager = NSFileManager.defaultManager

            // Create target directory if it doesn't exist
            if (!fileManager.fileExistsAtPath(targetDir)) {
                fileManager.createDirectoryAtPath(
                    targetDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            // Write zip data to temp file
            val tempZipPath = "$targetDir/temp_budget.zip"
            val nsData = zipData.toNSData()
            nsData.writeToFile(tempZipPath, atomically = true)

            // Extract using NSFileManager's built-in unzip (via file coordinator)
            // For iOS, we use a simpler approach - directly parse the zip
            val extractedDbPath = extractZipManually(tempZipPath, targetDir)

            // Clean up temp zip
            fileManager.removeItemAtPath(tempZipPath, error = null)

            extractedDbPath
        } catch (e: Exception) {
            println("[BudgetFileManager] Error extracting zip: ${e.message}")
            null
        }
    }

    private fun extractZipManually(zipPath: String, targetDir: String): String? {
        val fileManager = NSFileManager.defaultManager

        // Read the zip file
        val zipData = NSData.dataWithContentsOfFile(zipPath) ?: return null

        // Parse ZIP format manually - extract db.sqlite and metadata.json
        val bytes = zipData.bytes?.reinterpret<ByteVar>() ?: return null
        val length = zipData.length.toInt()

        var dbPath: String? = null
        val filesToExtract = listOf("db.sqlite", "metadata.json")

        // ZIP local file header signature: 0x04034b50 (PK\x03\x04)
        var offset = 0
        while (offset < length - 30) {
            // Check for local file header signature
            val sig = (bytes[offset].toInt() and 0xFF) or
                    ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                    ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                    ((bytes[offset + 3].toInt() and 0xFF) shl 24)

            if (sig != 0x04034b50) {
                // Not a local file header, might be central directory
                break
            }

            // Parse local file header
            val compressionMethod = (bytes[offset + 8].toInt() and 0xFF) or
                    ((bytes[offset + 9].toInt() and 0xFF) shl 8)
            val compressedSize = (bytes[offset + 18].toInt() and 0xFF) or
                    ((bytes[offset + 19].toInt() and 0xFF) shl 8) or
                    ((bytes[offset + 20].toInt() and 0xFF) shl 16) or
                    ((bytes[offset + 21].toInt() and 0xFF) shl 24)
            val uncompressedSize = (bytes[offset + 22].toInt() and 0xFF) or
                    ((bytes[offset + 23].toInt() and 0xFF) shl 8) or
                    ((bytes[offset + 24].toInt() and 0xFF) shl 16) or
                    ((bytes[offset + 25].toInt() and 0xFF) shl 24)
            val fileNameLength = (bytes[offset + 26].toInt() and 0xFF) or
                    ((bytes[offset + 27].toInt() and 0xFF) shl 8)
            val extraFieldLength = (bytes[offset + 28].toInt() and 0xFF) or
                    ((bytes[offset + 29].toInt() and 0xFF) shl 8)

            // Read filename
            val fileNameBytes = ByteArray(fileNameLength)
            for (i in 0 until fileNameLength) {
                fileNameBytes[i] = bytes[offset + 30 + i]
            }
            val fileName = fileNameBytes.decodeToString()

            val dataOffset = offset + 30 + fileNameLength + extraFieldLength

            println("[BudgetFileManager] Found file: $fileName (compression: $compressionMethod, compressed: $compressedSize, uncompressed: $uncompressedSize)")

            // Extract files we care about (db.sqlite and metadata.json)
            if (fileName in filesToExtract) {
                val destPath = "$targetDir/$fileName"

                val extracted = when (compressionMethod) {
                    0 -> {
                        // Stored (uncompressed)
                        val fileData = ByteArray(uncompressedSize)
                        for (i in 0 until uncompressedSize) {
                            fileData[i] = bytes[dataOffset + i]
                        }
                        val nsFileData = fileData.toNSData()
                        if (nsFileData.writeToFile(destPath, atomically = true)) {
                            println("[BudgetFileManager] Extracted $fileName (uncompressed) to $destPath")
                            true
                        } else false
                    }
                    8 -> {
                        // DEFLATE compression - use zlib
                        val compressedData = ByteArray(compressedSize)
                        for (i in 0 until compressedSize) {
                            compressedData[i] = bytes[dataOffset + i]
                        }

                        val decompressed = decompressDeflate(compressedData, uncompressedSize)
                        if (decompressed != null) {
                            val nsFileData = decompressed.toNSData()
                            if (nsFileData.writeToFile(destPath, atomically = true)) {
                                println("[BudgetFileManager] Extracted $fileName (deflate) to $destPath")
                                true
                            } else false
                        } else {
                            println("[BudgetFileManager] Failed to decompress $fileName")
                            false
                        }
                    }
                    else -> {
                        println("[BudgetFileManager] Unsupported compression method: $compressionMethod")
                        false
                    }
                }

                if (extracted && fileName == "db.sqlite") {
                    dbPath = destPath
                }
            }

            // Move to next file entry
            val dataSize = if (compressionMethod == 0) uncompressedSize else compressedSize
            offset = dataOffset + dataSize
        }

        if (dbPath == null) {
            println("[BudgetFileManager] db.sqlite not found in zip")
        }
        return dbPath
    }

    /**
     * Decompress DEFLATE data using zlib.
     * ZIP uses raw DEFLATE (no zlib header), so we use inflateInit2 with -MAX_WBITS.
     */
    private fun decompressDeflate(compressedData: ByteArray, expectedSize: Int): ByteArray? {
        return memScoped {
            try {
                val outputBuffer = ByteArray(expectedSize)

                // Initialize z_stream
                val stream = alloc<z_stream>()
                stream.zalloc = null
                stream.zfree = null
                stream.opaque = null

                compressedData.usePinned { srcPinned ->
                    outputBuffer.usePinned { dstPinned ->
                        stream.next_in = srcPinned.addressOf(0).reinterpret()
                        stream.avail_in = compressedData.size.toUInt()
                        stream.next_out = dstPinned.addressOf(0).reinterpret()
                        stream.avail_out = expectedSize.toUInt()

                        // Use -MAX_WBITS for raw deflate (no zlib/gzip header)
                        val initResult = inflateInit2(stream.ptr, -MAX_WBITS)
                        if (initResult != Z_OK) {
                            println("[BudgetFileManager] inflateInit2 failed: $initResult")
                            return@memScoped null
                        }

                        val inflateResult = inflate(stream.ptr, Z_FINISH)
                        val totalOut = stream.total_out.toInt()

                        inflateEnd(stream.ptr)

                        if (inflateResult == Z_STREAM_END || inflateResult == Z_OK) {
                            println("[BudgetFileManager] Decompressed ${compressedData.size} -> $totalOut bytes")
                            if (totalOut == expectedSize) {
                                outputBuffer
                            } else {
                                outputBuffer.copyOf(totalOut)
                            }
                        } else {
                            println("[BudgetFileManager] inflate failed: $inflateResult")
                            null
                        }
                    }
                }
            } catch (e: Exception) {
                println("[BudgetFileManager] Decompression exception: ${e.message}")
                null
            }
        }
    }

    actual fun getDefaultBudgetDir(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            true
        )
        val appSupport = paths.firstOrNull() as? String ?: NSTemporaryDirectory()
        return "$appSupport/ActualBudget"
    }

    actual fun delete(path: String): Boolean {
        return NSFileManager.defaultManager.removeItemAtPath(path, error = null)
    }

    actual fun exists(path: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path)
    }

    actual fun copy(source: String, destination: String): Boolean {
        // Remove destination if exists
        if (exists(destination)) {
            delete(destination)
        }
        return NSFileManager.defaultManager.copyItemAtPath(source, toPath = destination, error = null)
    }

    actual fun readFile(path: String): ByteArray? {
        return try {
            val nsData = NSData.dataWithContentsOfFile(path) ?: return null
            nsData.toByteArray()
        } catch (e: Exception) {
            println("[BudgetFileManager] Error reading file: ${e.message}")
            null
        }
    }

    actual fun writeFile(path: String, data: ByteArray): Boolean {
        return try {
            val nsData = data.toNSData()
            nsData.writeToFile(path, atomically = true)
        } catch (e: Exception) {
            println("[BudgetFileManager] Error writing file: ${e.message}")
            false
        }
    }

    actual fun getTempDir(): String {
        return NSTemporaryDirectory().trimEnd('/')
    }

    // ============ Multi-Budget Support ============

    actual fun getBudgetsDirectory(): String {
        val paths = NSSearchPathForDirectoriesInDomains(
            NSLibraryDirectory,
            NSUserDomainMask,
            true
        )
        val library = paths.firstOrNull() as? String ?: NSTemporaryDirectory()
        return "$library/ActualBudget"
    }

    actual fun listBudgetFolders(): List<String> {
        val budgetsDir = getBudgetsDirectory()
        val fileManager = NSFileManager.defaultManager

        // Create directory if it doesn't exist
        if (!fileManager.fileExistsAtPath(budgetsDir)) {
            return emptyList()
        }

        return try {
            val contents = fileManager.contentsOfDirectoryAtPath(budgetsDir, error = null)
            contents?.mapNotNull { item ->
                val name = item as? String ?: return@mapNotNull null
                val fullPath = "$budgetsDir/$name"
                // Only include directories that have a metadata.json file
                if (isDirectory(fullPath) && fileManager.fileExistsAtPath("$fullPath/metadata.json")) {
                    name
                } else {
                    null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            println("[BudgetFileManager] Error listing budget folders: ${e.message}")
            emptyList()
        }
    }

    actual fun createBudgetFolder(budgetId: String): String? {
        val budgetsDir = getBudgetsDirectory()
        val budgetPath = "$budgetsDir/$budgetId"
        val fileManager = NSFileManager.defaultManager

        return try {
            // Create budgets directory if needed
            if (!fileManager.fileExistsAtPath(budgetsDir)) {
                fileManager.createDirectoryAtPath(
                    budgetsDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            // Create budget folder
            if (!fileManager.fileExistsAtPath(budgetPath)) {
                fileManager.createDirectoryAtPath(
                    budgetPath,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }

            println("[BudgetFileManager] Created budget folder: $budgetPath")
            budgetPath
        } catch (e: Exception) {
            println("[BudgetFileManager] Error creating budget folder: ${e.message}")
            null
        }
    }

    actual fun isDirectory(path: String): Boolean {
        val fileManager = NSFileManager.defaultManager
        return memScoped {
            val isDir = alloc<BooleanVar>()
            fileManager.fileExistsAtPath(path, isDirectory = isDir.ptr) && isDir.value
        }
    }

    // ============ Private Helpers ============

    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), this.size.toULong())
        }
    }

    private fun NSData.toByteArray(): ByteArray {
        val length = this.length.toInt()
        val bytes = ByteArray(length)
        if (length > 0) {
            bytes.usePinned { pinned ->
                memcpy(pinned.addressOf(0), this.bytes, this.length)
            }
        }
        return bytes
    }
}
