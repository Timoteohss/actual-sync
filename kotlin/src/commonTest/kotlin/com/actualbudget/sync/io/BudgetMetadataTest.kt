package com.actualbudget.sync.io

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BudgetMetadataTest {

    private val json = Json {
        prettyPrint = false
        encodeDefaults = true
    }

    @Test
    fun testSerializeMinimalMetadata() {
        val metadata = BudgetMetadata(
            id = "test-budget-id",
            budgetName = "My Budget"
        )

        val jsonString = json.encodeToString(metadata)

        assertTrue(jsonString.contains("\"id\":\"test-budget-id\""))
        assertTrue(jsonString.contains("\"budgetName\":\"My Budget\""))
        assertTrue(jsonString.contains("\"resetClock\":true"))
    }

    @Test
    fun testSerializeFullMetadata() {
        val metadata = BudgetMetadata(
            id = "test-budget-id",
            budgetName = "My Budget",
            resetClock = false,
            cloudFileId = "cloud-123",
            groupId = "group-456",
            lastUploaded = "2025-01-23T12:00:00.000Z",
            encryptKeyId = "encrypt-789"
        )

        val jsonString = json.encodeToString(metadata)

        assertTrue(jsonString.contains("\"id\":\"test-budget-id\""))
        assertTrue(jsonString.contains("\"budgetName\":\"My Budget\""))
        assertTrue(jsonString.contains("\"resetClock\":false"))
        assertTrue(jsonString.contains("\"cloudFileId\":\"cloud-123\""))
        assertTrue(jsonString.contains("\"groupId\":\"group-456\""))
        assertTrue(jsonString.contains("\"lastUploaded\":\"2025-01-23T12:00:00.000Z\""))
        assertTrue(jsonString.contains("\"encryptKeyId\":\"encrypt-789\""))
    }

    @Test
    fun testDeserializeMetadata() {
        val jsonString = """
            {
                "id": "budget-abc",
                "budgetName": "Test Budget",
                "resetClock": true,
                "cloudFileId": "cloud-xyz",
                "groupId": null,
                "lastUploaded": "2025-01-01T00:00:00.000Z",
                "encryptKeyId": null
            }
        """.trimIndent()

        val metadata = json.decodeFromString<BudgetMetadata>(jsonString)

        assertEquals("budget-abc", metadata.id)
        assertEquals("Test Budget", metadata.budgetName)
        assertEquals(true, metadata.resetClock)
        assertEquals("cloud-xyz", metadata.cloudFileId)
        assertNull(metadata.groupId)
        assertEquals("2025-01-01T00:00:00.000Z", metadata.lastUploaded)
        assertNull(metadata.encryptKeyId)
    }

    @Test
    fun testRoundTripSerialization() {
        val original = BudgetMetadata(
            id = "roundtrip-id",
            budgetName = "Roundtrip Budget",
            resetClock = true,
            cloudFileId = "cloud-rt",
            groupId = "group-rt",
            lastUploaded = "2025-06-15T18:30:00.000Z",
            encryptKeyId = "key-rt"
        )

        val jsonString = json.encodeToString(original)
        val restored = json.decodeFromString<BudgetMetadata>(jsonString)

        assertEquals(original.id, restored.id)
        assertEquals(original.budgetName, restored.budgetName)
        assertEquals(original.resetClock, restored.resetClock)
        assertEquals(original.cloudFileId, restored.cloudFileId)
        assertEquals(original.groupId, restored.groupId)
        assertEquals(original.lastUploaded, restored.lastUploaded)
        assertEquals(original.encryptKeyId, restored.encryptKeyId)
    }

    @Test
    fun testDefaultValues() {
        val metadata = BudgetMetadata(
            id = "default-test",
            budgetName = "Default Test"
        )

        assertEquals(true, metadata.resetClock)
        assertNull(metadata.cloudFileId)
        assertNull(metadata.groupId)
        assertNull(metadata.lastUploaded)
        assertNull(metadata.encryptKeyId)
    }

    @Test
    fun testSpecialCharactersInBudgetName() {
        val metadata = BudgetMetadata(
            id = "special-chars",
            budgetName = "My Budget (2025) - \"Personal\" & Family"
        )

        val jsonString = json.encodeToString(metadata)
        val restored = json.decodeFromString<BudgetMetadata>(jsonString)

        assertEquals("My Budget (2025) - \"Personal\" & Family", restored.budgetName)
    }

    @Test
    fun testUnicodeInBudgetName() {
        val metadata = BudgetMetadata(
            id = "unicode-test",
            budgetName = "Orçamento 家計簿 Бюджет"
        )

        val jsonString = json.encodeToString(metadata)
        val restored = json.decodeFromString<BudgetMetadata>(jsonString)

        assertEquals("Orçamento 家計簿 Бюджет", restored.budgetName)
    }
}
