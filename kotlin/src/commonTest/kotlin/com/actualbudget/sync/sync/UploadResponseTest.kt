package com.actualbudget.sync.sync

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UploadResponseTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun testDeserializeSuccessResponse() {
        val jsonString = """{"status":"ok","groupId":"abc123"}"""

        val response = json.decodeFromString<UploadResponse>(jsonString)

        assertEquals("ok", response.status)
        assertEquals("abc123", response.groupId)
    }

    @Test
    fun testDeserializeResponseWithoutGroupId() {
        val jsonString = """{"status":"ok"}"""

        val response = json.decodeFromString<UploadResponse>(jsonString)

        assertEquals("ok", response.status)
        assertNull(response.groupId)
    }

    @Test
    fun testDeserializeResponseWithNullGroupId() {
        val jsonString = """{"status":"ok","groupId":null}"""

        val response = json.decodeFromString<UploadResponse>(jsonString)

        assertEquals("ok", response.status)
        assertNull(response.groupId)
    }

    @Test
    fun testDeserializeErrorResponse() {
        val jsonString = """{"status":"error","groupId":null}"""

        val response = json.decodeFromString<UploadResponse>(jsonString)

        assertEquals("error", response.status)
        assertNull(response.groupId)
    }

    @Test
    fun testDeserializeWithExtraFields() {
        // Server might return additional fields we don't care about
        val jsonString = """{"status":"ok","groupId":"xyz789","timestamp":1234567890,"extra":"ignored"}"""

        val response = json.decodeFromString<UploadResponse>(jsonString)

        assertEquals("ok", response.status)
        assertEquals("xyz789", response.groupId)
    }
}
