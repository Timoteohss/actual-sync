package com.actualbudget.sync.proto

/**
 * Encrypted data envelope.
 *
 * Proto definition:
 * message EncryptedData {
 *     bytes iv = 1;
 *     bytes authTag = 2;
 *     bytes data = 3;
 * }
 */
data class EncryptedData(
    val iv: ByteArray,
    val authTag: ByteArray,
    val data: ByteArray
) {
    fun encode(): ByteArray {
        val writer = ProtobufWriter()
        writer.writeBytes(1, iv)
        writer.writeBytes(2, authTag)
        writer.writeBytes(3, data)
        return writer.toByteArray()
    }

    companion object {
        fun decode(data: ByteArray): EncryptedData {
            val reader = ProtobufReader(data)
            var iv = byteArrayOf()
            var authTag = byteArrayOf()
            var content = byteArrayOf()

            while (!reader.isAtEnd) {
                val (fieldNumber, wireType) = reader.readTag()
                when (fieldNumber) {
                    1 -> iv = reader.readBytes()
                    2 -> authTag = reader.readBytes()
                    3 -> content = reader.readBytes()
                    else -> reader.skipField(wireType)
                }
            }

            return EncryptedData(iv, authTag, content)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EncryptedData
        return iv.contentEquals(other.iv) &&
               authTag.contentEquals(other.authTag) &&
               data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = iv.contentHashCode()
        result = 31 * result + authTag.contentHashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

/**
 * CRDT message content.
 *
 * Proto definition:
 * message Message {
 *     string dataset = 1;
 *     string row = 2;
 *     string column = 3;
 *     string value = 4;
 * }
 */
data class Message(
    val dataset: String,
    val row: String,
    val column: String,
    val value: String
) {
    fun encode(): ByteArray {
        val writer = ProtobufWriter()
        writer.writeString(1, dataset)
        writer.writeString(2, row)
        writer.writeString(3, column)
        writer.writeString(4, value)
        return writer.toByteArray()
    }

    companion object {
        fun decode(data: ByteArray): Message {
            val reader = ProtobufReader(data)
            var dataset = ""
            var row = ""
            var column = ""
            var value = ""

            while (!reader.isAtEnd) {
                val (fieldNumber, wireType) = reader.readTag()
                when (fieldNumber) {
                    1 -> dataset = reader.readString()
                    2 -> row = reader.readString()
                    3 -> column = reader.readString()
                    4 -> value = reader.readString()
                    else -> reader.skipField(wireType)
                }
            }

            return Message(dataset, row, column, value)
        }
    }
}

/**
 * Message envelope containing timestamp and content.
 *
 * Proto definition:
 * message MessageEnvelope {
 *     string timestamp = 1;
 *     bool isEncrypted = 2;
 *     bytes content = 3;
 * }
 */
data class MessageEnvelope(
    val timestamp: String,
    val isEncrypted: Boolean = false,
    val content: ByteArray
) {
    fun encode(): ByteArray {
        val writer = ProtobufWriter()
        writer.writeString(1, timestamp)
        writer.writeBool(2, isEncrypted)
        writer.writeBytes(3, content)
        return writer.toByteArray()
    }

    /**
     * Decode the content as a Message (if not encrypted).
     */
    fun decodeMessage(): Message {
        require(!isEncrypted) { "Cannot decode encrypted message without key" }
        return Message.decode(content)
    }

    companion object {
        fun decode(data: ByteArray): MessageEnvelope {
            val reader = ProtobufReader(data)
            var timestamp = ""
            var isEncrypted = false
            var content = byteArrayOf()

            while (!reader.isAtEnd) {
                val (fieldNumber, wireType) = reader.readTag()
                when (fieldNumber) {
                    1 -> timestamp = reader.readString()
                    2 -> isEncrypted = reader.readBool()
                    3 -> content = reader.readBytes()
                    else -> reader.skipField(wireType)
                }
            }

            return MessageEnvelope(timestamp, isEncrypted, content)
        }

        /**
         * Create an envelope with an unencrypted message.
         */
        fun create(timestamp: String, message: Message): MessageEnvelope {
            return MessageEnvelope(
                timestamp = timestamp,
                isEncrypted = false,
                content = message.encode()
            )
        }
    }

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
 * Sync request sent to the server.
 *
 * Proto definition:
 * message SyncRequest {
 *     repeated MessageEnvelope messages = 1;
 *     string fileId = 2;
 *     string groupId = 3;
 *     string keyId = 5;
 *     string since = 6;
 * }
 */
data class SyncRequest(
    val messages: List<MessageEnvelope> = emptyList(),
    val fileId: String,
    val groupId: String,
    val keyId: String = "",
    val since: String = ""
) {
    fun encode(): ByteArray {
        val writer = ProtobufWriter()
        messages.forEach { envelope ->
            writer.writeMessage(1, envelope.encode())
        }
        writer.writeString(2, fileId)
        writer.writeString(3, groupId)
        if (keyId.isNotEmpty()) writer.writeString(5, keyId)
        if (since.isNotEmpty()) writer.writeString(6, since)
        return writer.toByteArray()
    }

    companion object {
        fun decode(data: ByteArray): SyncRequest {
            val reader = ProtobufReader(data)
            val messages = mutableListOf<MessageEnvelope>()
            var fileId = ""
            var groupId = ""
            var keyId = ""
            var since = ""

            while (!reader.isAtEnd) {
                val (fieldNumber, wireType) = reader.readTag()
                when (fieldNumber) {
                    1 -> messages.add(MessageEnvelope.decode(reader.readBytes()))
                    2 -> fileId = reader.readString()
                    3 -> groupId = reader.readString()
                    5 -> keyId = reader.readString()
                    6 -> since = reader.readString()
                    else -> reader.skipField(wireType)
                }
            }

            return SyncRequest(messages, fileId, groupId, keyId, since)
        }
    }
}

/**
 * Sync response from the server.
 *
 * Proto definition:
 * message SyncResponse {
 *     repeated MessageEnvelope messages = 1;
 *     string merkle = 2;
 * }
 */
data class SyncResponse(
    val messages: List<MessageEnvelope> = emptyList(),
    val merkle: String = ""
) {
    companion object {
        fun decode(data: ByteArray): SyncResponse {
            val reader = ProtobufReader(data)
            val messages = mutableListOf<MessageEnvelope>()
            var merkle = ""

            while (!reader.isAtEnd) {
                val (fieldNumber, wireType) = reader.readTag()
                when (fieldNumber) {
                    1 -> messages.add(MessageEnvelope.decode(reader.readBytes()))
                    2 -> merkle = reader.readString()
                    else -> reader.skipField(wireType)
                }
            }

            return SyncResponse(messages, merkle)
        }
    }

    fun encode(): ByteArray {
        val writer = ProtobufWriter()
        messages.forEach { envelope ->
            writer.writeMessage(1, envelope.encode())
        }
        writer.writeString(2, merkle)
        return writer.toByteArray()
    }
}
