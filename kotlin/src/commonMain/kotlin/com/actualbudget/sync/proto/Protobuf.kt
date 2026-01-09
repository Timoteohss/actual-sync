package com.actualbudget.sync.proto

/**
 * Minimal protobuf encoder/decoder for the Actual Budget sync protocol.
 *
 * Wire format:
 * - Each field is: (field_number << 3) | wire_type
 * - Wire type 0: Varint (int32, int64, uint32, uint64, sint32, sint64, bool, enum)
 * - Wire type 2: Length-delimited (string, bytes, embedded messages, packed repeated fields)
 */
object Protobuf {

    // Wire types
    const val WIRE_VARINT = 0
    const val WIRE_LENGTH_DELIMITED = 2

    /**
     * Encode a varint (variable-length integer).
     */
    fun encodeVarint(value: Long): ByteArray {
        if (value == 0L) return byteArrayOf(0)

        val result = mutableListOf<Byte>()
        var v = value
        while (v != 0L) {
            var b = (v and 0x7F).toByte()
            v = v ushr 7
            if (v != 0L) {
                b = (b.toInt() or 0x80).toByte()
            }
            result.add(b)
        }
        return result.toByteArray()
    }

    fun encodeVarint(value: Int): ByteArray = encodeVarint(value.toLong())

    /**
     * Encode a field tag.
     */
    fun encodeTag(fieldNumber: Int, wireType: Int): ByteArray {
        return encodeVarint((fieldNumber shl 3) or wireType)
    }

    /**
     * Encode a string field.
     */
    fun encodeString(fieldNumber: Int, value: String): ByteArray {
        if (value.isEmpty()) return byteArrayOf()
        val bytes = value.encodeToByteArray()
        return encodeTag(fieldNumber, WIRE_LENGTH_DELIMITED) +
               encodeVarint(bytes.size) +
               bytes
    }

    /**
     * Encode a bytes field.
     */
    fun encodeBytes(fieldNumber: Int, value: ByteArray): ByteArray {
        if (value.isEmpty()) return byteArrayOf()
        return encodeTag(fieldNumber, WIRE_LENGTH_DELIMITED) +
               encodeVarint(value.size) +
               value
    }

    /**
     * Encode a bool field.
     */
    fun encodeBool(fieldNumber: Int, value: Boolean): ByteArray {
        if (!value) return byteArrayOf()
        return encodeTag(fieldNumber, WIRE_VARINT) + byteArrayOf(1)
    }

    /**
     * Encode an embedded message field.
     */
    fun encodeMessage(fieldNumber: Int, value: ByteArray): ByteArray {
        if (value.isEmpty()) return byteArrayOf()
        return encodeTag(fieldNumber, WIRE_LENGTH_DELIMITED) +
               encodeVarint(value.size) +
               value
    }
}

/**
 * Protobuf reader for decoding messages.
 */
class ProtobufReader(private val data: ByteArray) {
    private var pos = 0

    val isAtEnd: Boolean get() = pos >= data.size

    fun readVarint(): Long {
        var result = 0L
        var shift = 0
        while (pos < data.size) {
            val b = data[pos++].toInt() and 0xFF
            result = result or ((b and 0x7F).toLong() shl shift)
            if ((b and 0x80) == 0) break
            shift += 7
        }
        return result
    }

    fun readTag(): Pair<Int, Int> {
        val tag = readVarint().toInt()
        return (tag ushr 3) to (tag and 0x07)
    }

    fun readBytes(): ByteArray {
        val length = readVarint().toInt()
        val result = data.copyOfRange(pos, pos + length)
        pos += length
        return result
    }

    fun readString(): String = readBytes().decodeToString()

    fun readBool(): Boolean = readVarint() != 0L

    fun skipField(wireType: Int) {
        when (wireType) {
            Protobuf.WIRE_VARINT -> readVarint()
            Protobuf.WIRE_LENGTH_DELIMITED -> {
                val length = readVarint().toInt()
                pos += length
            }
            else -> throw IllegalArgumentException("Unknown wire type: $wireType")
        }
    }
}

/**
 * Protobuf writer for building messages.
 */
class ProtobufWriter {
    private val buffer = mutableListOf<Byte>()

    fun writeString(fieldNumber: Int, value: String) {
        buffer.addAll(Protobuf.encodeString(fieldNumber, value).toList())
    }

    fun writeBytes(fieldNumber: Int, value: ByteArray) {
        buffer.addAll(Protobuf.encodeBytes(fieldNumber, value).toList())
    }

    fun writeBool(fieldNumber: Int, value: Boolean) {
        buffer.addAll(Protobuf.encodeBool(fieldNumber, value).toList())
    }

    fun writeMessage(fieldNumber: Int, value: ByteArray) {
        buffer.addAll(Protobuf.encodeMessage(fieldNumber, value).toList())
    }

    fun toByteArray(): ByteArray = buffer.toByteArray()
}
