package com.actualbudget.sync

import com.actualbudget.sync.db.DatabaseDriverFactory
import com.actualbudget.sync.db.createDatabase
import com.actualbudget.sync.sync.SyncManager
import com.actualbudget.sync.sync.SyncResult
import com.benasher44.uuid.uuid4
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginResponse(val status: String, val data: TokenData? = null)

@Serializable
data class TokenData(val token: String? = null)

@Serializable
data class FilesResponse(val status: String, val data: List<BudgetFileInfo>? = null)

@Serializable
data class BudgetFileInfo(
    val fileId: String? = null,
    val groupId: String? = null,
    val name: String? = null,
    val encryptKeyId: String? = null,
    val deleted: Int = 0
)

private val json = Json { ignoreUnknownKeys = true }

/**
 * Demo application showing bidirectional sync with an Actual Budget server.
 *
 * Usage:
 *   ./gradlew runTest -PserverUrl="http://your-server:5006" -Ppassword="your-password"
 *
 * Or set environment variables:
 *   ACTUAL_SERVER_URL=http://your-server:5006
 *   ACTUAL_PASSWORD=your-password
 */
fun main(args: Array<String>) = runBlocking {
    // Get configuration from args, properties, or environment
    val serverUrl = System.getProperty("serverUrl")
        ?: System.getenv("ACTUAL_SERVER_URL")
        ?: args.getOrNull(0)
        ?: run {
            println("Error: Server URL required")
            println("Usage: ./gradlew runTest -PserverUrl=\"http://your-server:5006\" -Ppassword=\"your-password\"")
            println("   Or: export ACTUAL_SERVER_URL=http://your-server:5006")
            return@runBlocking
        }

    val password = System.getProperty("password")
        ?: System.getenv("ACTUAL_PASSWORD")
        ?: args.getOrNull(1)
        ?: run {
            println("Error: Password required")
            println("Usage: ./gradlew runTest -PserverUrl=\"http://your-server:5006\" -Ppassword=\"your-password\"")
            println("   Or: export ACTUAL_PASSWORD=your-password")
            return@runBlocking
        }

    println("Actual Budget Sync Demo")
    println("=".repeat(60))
    println("Server: $serverUrl")

    val httpClient = HttpClient(CIO)

    try {
        // 1. Login
        println("\n1. Logging in...")
        val loginResponse = httpClient.post("$serverUrl/account/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"password":"$password"}""")
        }

        if (!loginResponse.status.isSuccess()) {
            println("   Login failed: ${loginResponse.status}")
            return@runBlocking
        }

        val loginBody = loginResponse.bodyAsText()
        val loginData = json.decodeFromString<LoginResponse>(loginBody)
        val token = loginData.data?.token ?: run {
            println("   No token received")
            return@runBlocking
        }
        println("   Logged in successfully")

        // 2. Get budget info
        println("\n2. Getting budget info...")
        val filesResponse = httpClient.get("$serverUrl/sync/list-user-files") {
            header("X-ACTUAL-TOKEN", token)
        }

        val filesBody = filesResponse.bodyAsText()
        val filesData = json.decodeFromString<FilesResponse>(filesBody)
        val budget = filesData.data?.firstOrNull { it.deleted == 0 } ?: run {
            println("   No budget found")
            return@runBlocking
        }

        val fileId = budget.fileId!!
        val groupId = budget.groupId!!
        println("   Budget: ${budget.name}")
        println("   File ID: $fileId")

        // 3. Initialize SyncManager
        println("\n3. Initializing sync engine...")
        val driverFactory = DatabaseDriverFactory()
        val database = createDatabase(driverFactory, "actual_sync.db")
        val syncManager = SyncManager(serverUrl, httpClient, database)

        syncManager.initialize()
        syncManager.setToken(token)
        syncManager.setBudget(fileId, groupId)
        println("   Sync engine initialized")

        // 4. Full sync from server
        println("\n4. Performing full sync...")
        val fullSyncStart = System.currentTimeMillis()
        when (val result = syncManager.fullSync()) {
            is SyncResult.Success -> {
                val duration = System.currentTimeMillis() - fullSyncStart
                println("   Full sync completed in ${duration}ms")
                println("   Messages sent: ${result.messagesSent}")
                println("   Messages received: ${result.messagesReceived}")
                println("   Messages applied: ${result.messagesApplied}")
            }
            is SyncResult.Error -> {
                println("   Sync failed: ${result.message}")
                return@runBlocking
            }
        }

        // 5. Show current data
        println("\n5. Current data in local database:")
        val accounts = database.actualDatabaseQueries.getAccounts().executeAsList()
        println("   Accounts: ${accounts.size}")
        accounts.forEach { println("   - ${it.name}") }

        val categories = database.actualDatabaseQueries.getCategories().executeAsList()
        println("   Categories: ${categories.size}")

        val payees = database.actualDatabaseQueries.getPayees().executeAsList()
        println("   Payees: ${payees.size}")

        // 6. Create a local change (demo)
        println("\n6. Creating local change (new payee)...")
        val newPayeeId = uuid4().toString()
        val newPayeeName = "Demo Payee ${System.currentTimeMillis() % 10000}"
        syncManager.createPayee(newPayeeId, newPayeeName)
        println("   Created payee: $newPayeeName")
        println("   Pending changes: ${syncManager.getPendingChangeCount()}")

        // 7. Sync the change to server
        println("\n7. Syncing local change to server...")
        when (val result = syncManager.sync()) {
            is SyncResult.Success -> {
                println("   Sync completed!")
                println("   Messages sent: ${result.messagesSent}")
                println("   Messages received: ${result.messagesReceived}")
            }
            is SyncResult.Error -> {
                println("   Sync failed: ${result.message}")
            }
        }

        // 8. Verify
        println("\n8. Verifying...")
        val updatedPayees = database.actualDatabaseQueries.getPayees().executeAsList()
        val foundPayee = updatedPayees.find { it.id == newPayeeId }
        if (foundPayee != null) {
            println("   Payee synced successfully: ${foundPayee.name}")
        }

        println("\n" + "=".repeat(60))
        println("Demo complete!")
        println("\nThe created payee should now be visible in your Actual Budget app.")

    } finally {
        httpClient.close()
    }
}
