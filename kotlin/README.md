# actual-sync-kmp

Kotlin Multiplatform implementation of the Actual Budget sync protocol.

## Platforms

| Platform | Status | Output |
|----------|--------|--------|
| Android | ✅ | AAR library |
| iOS | ✅ | XCFramework |
| JVM Desktop | ✅ | JAR library |

## Installation

### Android (Gradle)

```kotlin
dependencies {
    implementation("com.actualbudget:actual-sync-kmp:0.6.1")
}
```

### iOS (CocoaPods)

```ruby
pod 'ActualSync', '~> 0.6.1'
```

### iOS (Swift Package Manager)

Use the pre-built XCFramework from the releases page, or build from source.

### Desktop/JVM

```kotlin
dependencies {
    implementation("com.actualbudget:actual-sync-kmp-jvm:0.6.1")
}
```

## Usage

### Complete Example

```kotlin
import com.actualbudget.sync.db.DatabaseDriverFactory
import com.actualbudget.sync.db.createDatabase
import com.actualbudget.sync.sync.SyncManager
import com.actualbudget.sync.sync.SyncResult
import com.benasher44.uuid.uuid4
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val serverUrl = "http://your-server:5006"
    val httpClient = HttpClient()

    // 1. Login to get token
    val loginResponse = httpClient.post("$serverUrl/account/login") {
        contentType(ContentType.Application.Json)
        setBody("""{"password":"your-password"}""")
    }
    val token = parseToken(loginResponse.bodyAsText())

    // 2. List budgets
    val filesResponse = httpClient.get("$serverUrl/sync/list-user-files") {
        header("X-ACTUAL-TOKEN", token)
    }
    val (fileId, groupId) = parseBudgetInfo(filesResponse.bodyAsText())

    // 3. Initialize sync manager
    val driverFactory = DatabaseDriverFactory() // Platform-specific
    val database = createDatabase(driverFactory, "actual.db")
    val syncManager = SyncManager(serverUrl, httpClient, database)

    syncManager.initialize()
    syncManager.setToken(token)
    syncManager.setBudget(fileId, groupId)

    // 4. Full sync (first time)
    when (val result = syncManager.fullSync()) {
        is SyncResult.Success -> {
            println("Synced ${result.messagesApplied} messages")
        }
        is SyncResult.Error -> {
            println("Error: ${result.message}")
            return@runBlocking
        }
    }

    // 5. Query local data
    val accounts = database.actualDatabaseQueries.getAccounts().executeAsList()
    val categories = database.actualDatabaseQueries.getCategories().executeAsList()
    val payees = database.actualDatabaseQueries.getPayees().executeAsList()

    println("Accounts: ${accounts.size}")
    println("Categories: ${categories.size}")
    println("Payees: ${payees.size}")

    // 6. Create local changes
    val newPayeeId = uuid4().toString()
    syncManager.createPayee(newPayeeId, "Coffee Shop")

    val transactionId = uuid4().toString()
    syncManager.createTransaction(
        id = transactionId,
        accountId = accounts.first().id,
        date = 20260123,    // YYYYMMDD
        amount = -450,      // -$4.50 (cents)
        payeeId = newPayeeId,
        categoryId = categories.first().id,
        notes = "Morning coffee"
    )

    // 7. Sync changes to server
    when (val result = syncManager.sync()) {
        is SyncResult.Success -> {
            println("Sent ${result.messagesSent} messages")
            println("Received ${result.messagesReceived} messages")
        }
        is SyncResult.Error -> println("Sync failed: ${result.message}")
    }

    httpClient.close()
}
```

### Platform-Specific Setup

#### Android

```kotlin
// In your Application or Activity
val driverFactory = DatabaseDriverFactory(applicationContext)
val database = createDatabase(driverFactory, "actual.db")

// Use CIO or OkHttp engine
val httpClient = HttpClient(CIO) {
    // Configure as needed
}
```

#### iOS (Swift interop)

```swift
import ActualSync

let driverFactory = DatabaseDriverFactory()
let database = DatabaseHelperKt.createDatabase(
    driverFactory: driverFactory,
    dbName: "actual.db"
)

let httpClient = HttpClientKt.createHttpClient()
let syncManager = SyncManager(
    serverUrl: serverUrl,
    httpClient: httpClient,
    database: database
)
```

#### Desktop/JVM

```kotlin
val driverFactory = DatabaseDriverFactory()
val database = createDatabase(driverFactory, "actual.db")
val httpClient = HttpClient(CIO)
```

## API Reference

### SyncManager

Primary interface for all sync and data operations.

#### Initialization

```kotlin
// Create and initialize
val syncManager = SyncManager(serverUrl, httpClient, database)
syncManager.initialize()           // Load clock state
syncManager.setToken(token)        // Set auth token
syncManager.setBudget(fileId, groupId) // Set active budget
```

#### Sync Operations

```kotlin
// Full sync - download everything
val result = syncManager.fullSync()

// Incremental sync - only changes since last sync
val result = syncManager.sync()

// Download and install a budget file
val success = syncManager.downloadAndInstallBudget(fileId, "/path/to/db.sqlite")

// Check pending changes
val count = syncManager.getPendingChangeCount()
val details = syncManager.getPendingChangeDetails()
```

#### Transactions

```kotlin
// Create transaction
syncManager.createTransaction(
    id = uuid4().toString(),
    accountId = "account-id",
    date = 20260123,           // YYYYMMDD
    amount = -5000,            // -$50.00 (cents, negative = expense)
    payeeId = "payee-id",      // optional
    categoryId = "category-id", // optional
    notes = "Description",      // optional
    cleared = true              // optional, default true
)

// Update transaction field
syncManager.updateTransaction(transactionId, "notes", "New description")
syncManager.updateTransaction(transactionId, "amount", -6000)
syncManager.updateTransaction(transactionId, "cleared", true)

// Delete transaction
syncManager.deleteTransaction(transactionId)

// Create transfer between accounts
syncManager.createTransfer(
    id = uuid4().toString(),
    fromAccountId = "checking-id",
    toAccountId = "savings-id",
    date = 20260123,
    amount = 10000  // $100.00 (positive = transfer amount)
)

// Create split transaction
syncManager.createSplitTransaction(
    parentId = uuid4().toString(),
    accountId = "account-id",
    date = 20260123,
    totalAmount = -10000,      // -$100.00 total
    splits = listOf(
        SplitItem(uuid4().toString(), categoryId1, -6000, "Groceries"),
        SplitItem(uuid4().toString(), categoryId2, -4000, "Household")
    )
)
```

#### Accounts

```kotlin
// Create account
syncManager.createAccount(
    id = uuid4().toString(),
    name = "Checking Account",
    offbudget = false
)

// Update account
syncManager.updateAccount(accountId, "name", "New Name")
syncManager.updateAccount(accountId, "offbudget", true)

// Delete (tombstone)
syncManager.deleteAccount(accountId)

// Reopen closed account
syncManager.reopenAccount(accountId)

// Reorder accounts
syncManager.moveAccount(accountId, targetAccountId) // Insert before target
syncManager.moveAccount(accountId, null)            // Move to end
```

#### Payees

```kotlin
// Create payee
syncManager.createPayee(uuid4().toString(), "Amazon")

// Update payee
syncManager.updatePayee(payeeId, "name", "New Name")

// Delete payee
syncManager.deletePayee(payeeId)
```

#### Categories

```kotlin
// Create category group
syncManager.createCategoryGroup(
    id = uuid4().toString(),
    name = "Monthly Bills",
    isIncome = false,
    sortOrder = 1.0,
    hidden = false
)

// Create category
syncManager.createCategory(
    id = uuid4().toString(),
    name = "Rent",
    groupId = groupId,
    isIncome = false,
    sortOrder = 1.0,
    hidden = false
)

// Update category
syncManager.updateCategory(categoryId, "name", "Mortgage")
syncManager.updateCategory(categoryId, "hidden", true)

// Delete category
syncManager.deleteCategory(categoryId)
syncManager.deleteCategoryGroup(groupId)

// Move category to different group
syncManager.moveCategory(categoryId, newGroupId, targetCategoryId)

// Reorder category group
syncManager.moveCategoryGroup(groupId, targetGroupId)
```

#### Budget Operations

```kotlin
// Set monthly budget (month format: YYYYMM as Long)
syncManager.setBudgetAmount(categoryId, 202601L, 50000L) // $500.00 for Jan 2026

// Set budget goal
syncManager.setBudgetGoal(categoryId, 202601L, 100000L) // $1000 goal

// Set carryover
syncManager.setBudgetCarryover(categoryId, 202601L, 5000L)

// Transfer budget between categories
syncManager.transferBudget(
    fromCategoryId = "groceries-id",
    toCategoryId = "dining-id",
    month = 202601L,
    amount = 5000L  // $50.00
)

// Copy last month's budgets
syncManager.copyBudgetFromPreviousMonth(202601L)

// Zero all budgets for a month
syncManager.zeroBudgetsForMonth(202601L)

// Set budget based on spending average
syncManager.setNMonthAverage(categoryId, 202601L, 3)  // 3-month average
syncManager.set3MonthAverage(202601L)   // All categories
syncManager.set6MonthAverage(202601L)
syncManager.set12MonthAverage(202601L)

// Hold money for next month
val success = syncManager.holdForNextMonth(202601L, 10000L)
syncManager.resetHold(202601L)

// Cover overspending
syncManager.coverOverspending(
    fromCategoryId = "savings-id",
    toCategoryId = "overspent-id",
    month = 202601L,
    amount = null  // null = cover full overspending
)

// Calculate available to budget
val toBudget = syncManager.calculateToBudget(202601L)
val leftover = syncManager.calculateCategoryLeftover(categoryId, 202601L)
```

### Database Queries

Direct access to SQLDelight-generated queries for read operations:

```kotlin
// Accounts
val accounts = database.actualDatabaseQueries.getAccounts().executeAsList()
val account = database.actualDatabaseQueries.getAccountById(id).executeAsOne()
val balance = database.actualDatabaseQueries.getAccountBalance(id).executeAsOne()

// Transactions
val transactions = database.actualDatabaseQueries
    .getTransactionsForAccount(accountId, limit = 100)
    .executeAsList()

val transaction = database.actualDatabaseQueries
    .getTransactionById(id)
    .executeAsOneOrNull()

// With pagination
val page = database.actualDatabaseQueries
    .getTransactionsWithDetailsForAccount(accountId, limit = 50, offset = 0)
    .executeAsList()

// Search
val results = database.actualDatabaseQueries
    .searchTransactionsForAccount(accountId, "%coffee%", limit = 50)
    .executeAsList()

// Categories
val categories = database.actualDatabaseQueries.getCategories().executeAsList()
val groups = database.actualDatabaseQueries.getCategoryGroups().executeAsList()

// Payees
val payees = database.actualDatabaseQueries.getPayees().executeAsList()

// Budget data
val budgets = database.actualDatabaseQueries.getBudgetForMonth(202601L).executeAsList()
val spent = database.actualDatabaseQueries.getSpentByCategory(startDate, endDate).executeAsList()
val income = database.actualDatabaseQueries.getTotalIncomeForMonth(202601L).executeAsOne()
```

### Data Models

```kotlin
// From database
data class Accounts(
    val id: String,
    val name: String,
    val offbudget: Long,   // 0 or 1
    val closed: Long,      // 0 or 1
    val sort_order: Double?,
    val tombstone: Long    // 0 or 1
)

data class Transactions(
    val id: String,
    val acct: String,
    val date: Long,        // YYYYMMDD
    val amount: Long,      // cents
    val payee: String?,
    val category: String?,
    val notes: String?,
    val cleared: Long,     // 0 or 1
    val reconciled: Long,  // 0 or 1
    val isParent: Long,    // 0 or 1 (split parent)
    val isChild: Long,     // 0 or 1 (split child)
    val parent_id: String?,
    val tombstone: Long
)

data class Categories(
    val id: String,
    val name: String,
    val cat_group: String,
    val is_income: Long,   // 0 or 1
    val hidden: Long,      // 0 or 1
    val sort_order: Double?,
    val tombstone: Long
)

data class Payees(
    val id: String,
    val name: String,
    val transfer_acct: String?,  // For transfer payees
    val tombstone: Long
)

data class Zero_budgets(
    val id: String,        // "YYYYMM-categoryId"
    val month: Long,       // YYYYMM
    val category: String,
    val amount: Long,      // cents
    val carryover: Long,
    val goal: Long?,
    val tombstone: Long
)
```

## Project Structure

```
src/
├── commonMain/kotlin/com/actualbudget/sync/
│   ├── api/            # High-level ActualClient API (WIP)
│   ├── crdt/           # CRDT implementation
│   │   ├── Clock.kt        # Clock state management
│   │   ├── Merkle.kt       # Merkle trie for sync
│   │   ├── MurmurHash3.kt  # Hash function
│   │   └── Timestamp.kt    # HLC timestamps
│   ├── db/             # Database layer
│   │   └── DatabaseDriverFactory.kt  # Platform expect
│   ├── io/             # File I/O
│   │   └── BudgetFileManager.kt  # Zip extraction
│   ├── proto/          # Protocol buffers
│   │   ├── Protobuf.kt     # Encoder/decoder
│   │   └── SyncMessages.kt # Message definitions
│   └── sync/           # Sync engine
│       ├── BudgetUtils.kt  # Budget calculations
│       ├── SyncClient.kt   # HTTP transport
│       ├── SyncEngine.kt   # Core sync logic
│       └── SyncManager.kt  # High-level API
├── commonMain/sqldelight/
│   └── ActualDatabase.sq   # SQLDelight schema (100+ queries)
├── androidMain/        # Android implementations
├── iosMain/            # iOS implementations
└── desktopMain/        # JVM implementations + demo
```

## Building

```bash
# Build all targets
./gradlew build

# Run tests
./gradlew desktopTest
./gradlew allTests

# Build Android AAR
./gradlew assembleRelease
# Output: build/outputs/aar/

# Build iOS Framework
./gradlew linkReleaseFrameworkIosArm64
./gradlew linkReleaseFrameworkIosSimulatorArm64
# Output: build/bin/ios*/releaseFramework/

# Build XCFramework (all iOS architectures)
./gradlew assembleXCFramework
# Output: build/XCFrameworks/

# Build Desktop JAR
./gradlew jvmJar
# Output: build/libs/
```

## Run Demo

Test sync against your Actual Budget server:

```bash
# Via Gradle properties
./gradlew runTest \
  -PserverUrl="http://your-server:5006" \
  -Ppassword="your-password"

# Via environment variables
export ACTUAL_SERVER_URL="http://your-server:5006"
export ACTUAL_PASSWORD="your-password"
./gradlew runTest
```

The demo will:
1. Login and list budgets
2. Perform full sync
3. Display account/category/payee counts
4. Create a test payee
5. Sync the change to server
6. Verify the payee was synced

## Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.1.0 | Language |
| Ktor Client | 3.0.3 | HTTP requests |
| SQLDelight | 2.0.2 | Database |
| Kotlinx Serialization | 1.7.3 | JSON |
| Kotlinx Coroutines | 1.9.0 | Async |
| Kotlinx DateTime | 0.6.1 | Date/time |
| UUID | 0.8.4 | ID generation |

## License

MIT
