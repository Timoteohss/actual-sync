# Quick Start Guide

Get up and running with actual-sync in minutes.

## Prerequisites

- An Actual Budget server (self-hosted or cloud)
- JDK 17+ (for building)
- Your server URL and password

## 1. Add Dependency

### Android (Gradle Kotlin DSL)

```kotlin
dependencies {
    implementation("com.actualbudget:actual-sync-kmp:0.6.1")
}
```

### iOS

Add the pre-built XCFramework to your Xcode project, or use Swift Package Manager.

## 2. Initialize

```kotlin
import com.actualbudget.sync.db.DatabaseDriverFactory
import com.actualbudget.sync.db.createDatabase
import com.actualbudget.sync.sync.SyncManager
import io.ktor.client.*

// Create components
val driverFactory = DatabaseDriverFactory(context) // Android
val database = createDatabase(driverFactory, "actual.db")
val httpClient = HttpClient()
val syncManager = SyncManager(serverUrl, httpClient, database)

// Initialize
syncManager.initialize()
syncManager.setToken(authToken)
syncManager.setBudget(fileId, groupId)
```

## 3. First Sync

```kotlin
when (val result = syncManager.fullSync()) {
    is SyncResult.Success -> {
        println("Downloaded ${result.messagesApplied} messages")
    }
    is SyncResult.Error -> {
        println("Error: ${result.message}")
    }
}
```

## Common Recipes

### Get Authentication Token

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

suspend fun login(serverUrl: String, password: String): String {
    val response = httpClient.post("$serverUrl/account/login") {
        contentType(ContentType.Application.Json)
        setBody("""{"password":"$password"}""")
    }

    val json = Json.parseToJsonElement(response.bodyAsText())
    return json.jsonObject["data"]!!
        .jsonObject["token"]!!
        .jsonPrimitive.content
}
```

### List Available Budgets

```kotlin
suspend fun listBudgets(serverUrl: String, token: String): List<BudgetInfo> {
    val response = httpClient.get("$serverUrl/sync/list-user-files") {
        header("X-ACTUAL-TOKEN", token)
    }

    val json = Json.parseToJsonElement(response.bodyAsText())
    return json.jsonObject["data"]!!.jsonArray.map { file ->
        BudgetInfo(
            fileId = file.jsonObject["fileId"]!!.jsonPrimitive.content,
            groupId = file.jsonObject["groupId"]!!.jsonPrimitive.content,
            name = file.jsonObject["name"]!!.jsonPrimitive.content
        )
    }
}

data class BudgetInfo(val fileId: String, val groupId: String, val name: String)
```

### Add a Transaction

```kotlin
import com.benasher44.uuid.uuid4

fun addExpense(
    accountId: String,
    amount: Double,      // e.g., 50.00
    payeeName: String,
    categoryId: String?,
    notes: String?
) {
    // Find or create payee
    val payee = database.actualDatabaseQueries
        .findPayeeByName(payeeName)
        .executeAsOneOrNull()

    val payeeId = payee?.id ?: run {
        val newId = uuid4().toString()
        syncManager.createPayee(newId, payeeName)
        newId
    }

    // Create transaction
    syncManager.createTransaction(
        id = uuid4().toString(),
        accountId = accountId,
        date = todayAsInt(),           // YYYYMMDD
        amount = (-amount * 100).toLong(),  // Convert to cents, negative
        payeeId = payeeId,
        categoryId = categoryId,
        notes = notes
    )
}

fun todayAsInt(): Int {
    val today = java.time.LocalDate.now()
    return today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
}
```

### Transfer Between Accounts

```kotlin
fun transfer(fromAccountId: String, toAccountId: String, amount: Double) {
    syncManager.createTransfer(
        id = uuid4().toString(),
        fromAccountId = fromAccountId,
        toAccountId = toAccountId,
        date = todayAsInt(),
        amount = (amount * 100).toLong()  // Convert to cents
    )
}
```

### Get Account Balance

```kotlin
fun getBalance(accountId: String): Double {
    val balance = database.actualDatabaseQueries
        .getAccountBalance(accountId)
        .executeAsOne()
    return balance / 100.0  // Convert cents to dollars
}
```

### List Recent Transactions

```kotlin
fun getRecentTransactions(accountId: String, limit: Int = 50): List<TransactionInfo> {
    return database.actualDatabaseQueries
        .getTransactionsWithDetailsForAccount(accountId, limit.toLong(), 0)
        .executeAsList()
        .map { tx ->
            TransactionInfo(
                id = tx.id,
                date = tx.date.toInt(),
                amount = tx.amount / 100.0,
                payeeName = tx.payee_name,
                categoryName = tx.category_name,
                notes = tx.notes
            )
        }
}

data class TransactionInfo(
    val id: String,
    val date: Int,
    val amount: Double,
    val payeeName: String?,
    val categoryName: String?,
    val notes: String?
)
```

### Set Monthly Budget

```kotlin
fun setBudget(categoryId: String, year: Int, month: Int, amount: Double) {
    val monthLong = (year * 100 + month).toLong()  // e.g., 202601
    val amountCents = (amount * 100).toLong()
    syncManager.setBudgetAmount(categoryId, monthLong, amountCents)
}
```

### Get Budget Overview

```kotlin
fun getBudgetOverview(year: Int, month: Int): List<CategoryBudget> {
    val monthLong = (year * 100 + month).toLong()

    // Get budgeted amounts
    val budgets = database.actualDatabaseQueries
        .getBudgetForMonth(monthLong)
        .executeAsList()
        .associateBy { it.category }

    // Get spent amounts
    val startDate = year * 10000 + month * 100 + 1
    val endDate = year * 10000 + month * 100 + 31
    val spent = database.actualDatabaseQueries
        .getSpentByCategory(startDate.toLong(), endDate.toLong())
        .executeAsList()
        .associateBy { it.category }

    // Get categories
    return database.actualDatabaseQueries.getCategories().executeAsList()
        .filter { it.tombstone == 0L && it.hidden == 0L }
        .map { cat ->
            val budgeted = budgets[cat.id]?.amount ?: 0L
            val spentAmount = spent[cat.id]?.spent ?: 0.0
            CategoryBudget(
                id = cat.id,
                name = cat.name,
                budgeted = budgeted / 100.0,
                spent = -spentAmount / 100.0,  // Make positive
                available = (budgeted + spentAmount.toLong()) / 100.0
            )
        }
}

data class CategoryBudget(
    val id: String,
    val name: String,
    val budgeted: Double,
    val spent: Double,
    val available: Double
)
```

### Sync Changes

```kotlin
// After making local changes, sync to server
suspend fun syncToServer() {
    // Check if there are pending changes
    val pending = syncManager.getPendingChangeCount()
    if (pending == 0) {
        println("No changes to sync")
        return
    }

    println("Syncing $pending changes...")
    when (val result = syncManager.sync()) {
        is SyncResult.Success -> {
            println("Sent: ${result.messagesSent}")
            println("Received: ${result.messagesReceived}")
        }
        is SyncResult.Error -> {
            println("Sync failed: ${result.message}")
            // Changes are preserved locally, will retry later
        }
    }
}
```

### Periodic Background Sync

```kotlin
// Sync every 5 minutes when app is active
fun startPeriodicSync(scope: CoroutineScope) {
    scope.launch {
        while (isActive) {
            delay(5 * 60 * 1000) // 5 minutes
            try {
                syncManager.sync()
            } catch (e: Exception) {
                println("Background sync failed: ${e.message}")
            }
        }
    }
}
```

### Handle Offline Mode

```kotlin
class BudgetRepository(
    private val syncManager: SyncManager,
    private val database: ActualDatabase
) {
    private var isOnline = true

    fun setOnline(online: Boolean) {
        isOnline = online
    }

    suspend fun addTransaction(/* params */) {
        // Always create locally first
        syncManager.createTransaction(/* params */)

        // Try to sync if online
        if (isOnline) {
            try {
                syncManager.sync()
            } catch (e: Exception) {
                // Will sync later when back online
            }
        }
    }

    suspend fun syncWhenBackOnline() {
        if (isOnline && syncManager.getPendingChangeCount() > 0) {
            syncManager.sync()
        }
    }
}
```

## iOS (Swift) Examples

### Initialize

```swift
import ActualSync

class BudgetService {
    private let syncManager: SyncManager
    private let database: ActualDatabase

    init(serverUrl: String) {
        let driverFactory = DatabaseDriverFactory()
        database = DatabaseHelperKt.createDatabase(
            driverFactory: driverFactory,
            dbName: "actual.db"
        )

        let httpClient = HttpClientKt.createHttpClient()
        syncManager = SyncManager(
            serverUrl: serverUrl,
            httpClient: httpClient,
            database: database
        )

        syncManager.initialize(clientId: nil)
    }

    func configure(token: String, fileId: String, groupId: String) {
        syncManager.setToken(token: token)
        syncManager.setBudget(fileId: fileId, groupId: groupId)
    }

    func fullSync() async throws {
        let result = try await syncManager.fullSync()
        // Handle result
    }
}
```

### Add Transaction

```swift
func addTransaction(
    accountId: String,
    amount: Double,
    payeeId: String?,
    categoryId: String?,
    notes: String?
) {
    let id = UUID().uuidString
    let date = Int32(dateToInt(Date()))
    let amountCents = Int64(-amount * 100)

    syncManager.createTransaction(
        id: id,
        accountId: accountId,
        date: date,
        amount: amountCents,
        payeeId: payeeId,
        categoryId: categoryId,
        notes: notes,
        cleared: true
    )
}

func dateToInt(_ date: Date) -> Int {
    let calendar = Calendar.current
    let year = calendar.component(.year, from: date)
    let month = calendar.component(.month, from: date)
    let day = calendar.component(.day, from: date)
    return year * 10000 + month * 100 + day
}
```

## Troubleshooting

### "Budget not set" Error

Make sure to call `setBudget()` before syncing:

```kotlin
syncManager.setBudget(fileId, groupId)
```

### Sync Fails with 401

Your auth token may have expired. Re-login:

```kotlin
val newToken = login(serverUrl, password)
syncManager.setToken(newToken)
```

### Changes Not Appearing on Server

1. Check pending changes: `syncManager.getPendingChangeCount()`
2. Call `sync()` to push changes
3. Check for sync errors in the result

### Database Locked

Don't access the database from multiple threads simultaneously. Use a single SyncManager instance and ensure thread safety.

## Next Steps

- Read the [full API reference](../kotlin/README.md)
- Learn about the [sync protocol](PROTOCOL.md)
- Check out the [demo application](../kotlin/src/desktopMain/kotlin/com/actualbudget/sync/Main.kt)
