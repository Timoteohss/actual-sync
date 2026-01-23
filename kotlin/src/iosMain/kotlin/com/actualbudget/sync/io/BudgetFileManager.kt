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

        // Parse ZIP format manually (simplified - looking for db.sqlite)
        val bytes = zipData.bytes?.reinterpret<ByteVar>() ?: return null
        val length = zipData.length.toInt()

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

            // Extract db.sqlite
            if (fileName == "db.sqlite") {
                val destPath = "$targetDir/db.sqlite"

                when (compressionMethod) {
                    0 -> {
                        // Stored (uncompressed)
                        val fileData = ByteArray(uncompressedSize)
                        for (i in 0 until uncompressedSize) {
                            fileData[i] = bytes[dataOffset + i]
                        }
                        val nsFileData = fileData.toNSData()
                        if (nsFileData.writeToFile(destPath, atomically = true)) {
                            println("[BudgetFileManager] Extracted db.sqlite (uncompressed) to $destPath")
                            return destPath
                        }
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
                                println("[BudgetFileManager] Extracted db.sqlite (deflate) to $destPath")
                                return destPath
                            }
                        } else {
                            println("[BudgetFileManager] Failed to decompress db.sqlite")
                        }
                    }
                    else -> {
                        println("[BudgetFileManager] Unsupported compression method: $compressionMethod")
                    }
                }
            }

            // Move to next file entry
            val dataSize = if (compressionMethod == 0) uncompressedSize else compressedSize
            offset = dataOffset + dataSize
        }

        println("[BudgetFileManager] db.sqlite not found in zip")
        return null
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
