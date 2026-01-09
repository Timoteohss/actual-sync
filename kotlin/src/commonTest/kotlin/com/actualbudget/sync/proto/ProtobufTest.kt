package com.actualbudget.sync.proto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertContentEquals

class ProtobufTest {

    @Test
    fun testVarintEncoding() {
        // Test small numbers
        assertContentEquals(byteArrayOf(0), Protobuf.encodeVarint(0))
        assertContentEquals(byteArrayOf(1), Protobuf.encodeVarint(1))
        assertContentEquals(byteArrayOf(127), Protobuf.encodeVarint(127))

        // Test numbers requiring 2 bytes
        assertContentEquals(byteArrayOf(0x80.toByte(), 0x01), Protobuf.encodeVarint(128))
        assertContentEquals(byteArrayOf(0xFF.toByte(), 0x01), Protobuf.encodeVarint(255))

        // Test larger numbers
        val encoded300 = Protobuf.encodeVarint(300)
        assertContentEquals(byteArrayOf(0xAC.toByte(), 0x02), encoded300)
    }

    @Test
    fun testVarintDecoding() {
        val reader1 = ProtobufReader(byteArrayOf(0))
        assertEquals(0L, reader1.readVarint())

        val reader2 = ProtobufReader(byteArrayOf(1))
        assertEquals(1L, reader2.readVarint())

        val reader3 = ProtobufReader(byteArrayOf(0xAC.toByte(), 0x02))
        assertEquals(300L, reader3.readVarint())
    }

    @Test
    fun testMessageEncodeDecode() {
        val original = Message(
            dataset = "transactions",
            row = "tx-123",
            column = "amount",
            value = "1000"
        )

        val encoded = original.encode()
        val decoded = Message.decode(encoded)

        assertEquals(original, decoded)
    }

    @Test
    fun testMessageEnvelopeEncodeDecode() {
        val message = Message(
            dataset = "accounts",
            row = "acc-456",
            column = "name",
            value = "Checking"
        )

        val original = MessageEnvelope.create(
            timestamp = "2024-01-15T10:30:00.000Z-0001-ABCD1234EFGH5678",
            message = message
        )

        val encoded = original.encode()
        val decoded = MessageEnvelope.decode(encoded)

        assertEquals(original.timestamp, decoded.timestamp)
        assertEquals(original.isEncrypted, decoded.isEncrypted)
        assertContentEquals(original.content, decoded.content)

        // Verify we can decode the inner message
        val decodedMessage = decoded.decodeMessage()
        assertEquals(message, decodedMessage)
    }

    @Test
    fun testSyncRequestEncodeDecode() {
        val messages = listOf(
            MessageEnvelope.create(
                "2024-01-15T10:30:00.000Z-0000-ABCD1234EFGH5678",
                Message("transactions", "tx-1", "amount", "500")
            ),
            MessageEnvelope.create(
                "2024-01-15T10:30:00.000Z-0001-ABCD1234EFGH5678",
                Message("transactions", "tx-1", "category", "food")
            )
        )

        val original = SyncRequest(
            messages = messages,
            fileId = "file-123",
            groupId = "group-456",
            keyId = "",
            since = "2024-01-15T10:00:00.000Z-0000-0000000000000000"
        )

        val encoded = original.encode()
        val decoded = SyncRequest.decode(encoded)

        assertEquals(original.fileId, decoded.fileId)
        assertEquals(original.groupId, decoded.groupId)
        assertEquals(original.since, decoded.since)
        assertEquals(original.messages.size, decoded.messages.size)

        // Verify each message
        for (i in original.messages.indices) {
            assertEquals(original.messages[i].timestamp, decoded.messages[i].timestamp)
        }
    }

    @Test
    fun testSyncResponseDecode() {
        // Create a response
        val messages = listOf(
            MessageEnvelope.create(
                "2024-01-15T10:30:00.000Z-0002-REMOTE12345678",
                Message("categories", "cat-1", "name", "Groceries")
            )
        )

        val original = SyncResponse(
            messages = messages,
            merkle = """{"hash":123456}"""
        )

        val encoded = original.encode()
        val decoded = SyncResponse.decode(encoded)

        assertEquals(original.merkle, decoded.merkle)
        assertEquals(original.messages.size, decoded.messages.size)
        assertEquals(original.messages[0].timestamp, decoded.messages[0].timestamp)
    }

    @Test
    fun testEmptySyncRequest() {
        val original = SyncRequest(
            messages = emptyList(),
            fileId = "file-123",
            groupId = "group-456"
        )

        val encoded = original.encode()
        val decoded = SyncRequest.decode(encoded)

        assertEquals(original.fileId, decoded.fileId)
        assertEquals(original.groupId, decoded.groupId)
        assertTrue(decoded.messages.isEmpty())
    }

    @Test
    fun testEncryptedEnvelope() {
        val envelope = MessageEnvelope(
            timestamp = "2024-01-15T10:30:00.000Z-0000-ABCD1234EFGH5678",
            isEncrypted = true,
            content = byteArrayOf(1, 2, 3, 4, 5) // encrypted bytes
        )

        val encoded = envelope.encode()
        val decoded = MessageEnvelope.decode(encoded)

        assertEquals(envelope.timestamp, decoded.timestamp)
        assertTrue(decoded.isEncrypted)
        assertContentEquals(envelope.content, decoded.content)
    }
}
