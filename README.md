# actual-sync

Native sync libraries for [Actual Budget](https://actualbudget.org/) — enabling mobile and desktop apps to sync directly with Actual Budget servers without JavaScript.

## Overview

This project provides native implementations of Actual Budget's CRDT-based sync protocol. Build native iOS, Android, and desktop applications that sync seamlessly with your self-hosted Actual Budget server.

### Features

- **Full CRDT Implementation** — Hybrid Logical Clocks, Merkle tries, last-write-wins conflict resolution
- **Bidirectional Sync** — Download from server, upload local changes
- **Local-First** — Works offline, syncs when connected
- **Multi-Platform** — Kotlin Multiplatform (Android, iOS, Desktop)
- **SQLite Storage** — Local database via SQLDelight
- **Complete Budget API** — Accounts, transactions, categories, payees, budgets

## Installation

### Android (Gradle)

```kotlin
dependencies {
    implementation("com.actualbudget:actual-sync:0.6.1")
}
```

### iOS (Swift Package Manager)

Add the package URL to your Xcode project:
```
https://github.com/your-org/actual-sync
```

Or add to `Package.swift`:
```swift
dependencies: [
    .package(url: "https://github.com/your-org/actual-sync", from: "0.6.1")
]
```

### Pre-built XCFramework

Download `ActualSync.xcframework` from the [Releases](releases) page and add to your Xcode project.

## Quick Start

### Basic Setup

```kotlin
import com.actualbudget.sync.db.DatabaseDriverFactory
import com.actualbudget.sync.db.createDatabase
import com.actualbudget.sync.sync.SyncManager
import io.ktor.client.*

// 1. Create database and sync manager
val driverFactory = DatabaseDriverFactory(context) // Android
// val driverFactory = DatabaseDriverFactory()      // iOS/Desktop
val database = createDatabase(driverFactory, "actual.db")
val httpClient = HttpClient()
val syncManager = SyncManager(serverUrl, httpClient, database)

// 2. Initialize
syncManager.initialize()
syncManager.setToken(authToken)
syncManager.setBudget(fileId, groupId)

// 3. Full sync (first time)
when (val result = syncManager.fullSync()) {
    is SyncResult.Success -> println("Synced ${result.messagesApplied} messages")
    is SyncResult.Error -> println("Error: ${result.message}")
}
```

### Creating Transactions

```kotlin
import com.benasher44.uuid.uuid4

// Create a new transaction
val transactionId = uuid4().toString()
syncManager.createTransaction(
    id = transactionId,
    accountId = "account-uuid",
    date = 20260123,          // YYYYMMDD format
    amount = -5000,           // -$50.00 (negative = expense, cents)
    payeeId = "payee-uuid",   // optional
    notes = "Grocery shopping"
)

// Sync to server
syncManager.sync()
```

### Working Offline

```kotlin
// Create changes while offline - they're stored locally
syncManager.createPayee(uuid4().toString(), "New Store")
syncManager.createTransaction(...)

// Later, when online
when (syncManager.sync()) {
    is SyncResult.Success -> println("Changes synced!")
    is SyncResult.Error -> println("Will retry later")
}
```

## Project Structure

```
actual-sync/
├── kotlin/                    # Kotlin Multiplatform library
│   └── src/
│       ├── commonMain/        # Shared code
│       │   ├── crdt/          # CRDT: Timestamp, Merkle, Clock
│       │   ├── sync/          # SyncManager, SyncEngine, SyncClient
│       │   ├── db/            # Database schema (SQLDelight)
│       │   ├── proto/         # Protobuf serialization
│       │   └── api/           # High-level API (WIP)
│       ├── androidMain/       # Android SQLite driver
│       ├── iosMain/           # iOS SQLite driver
│       └── desktopMain/       # JVM SQLite driver + demo app
├── dart/                      # Dart/Flutter (scaffolded)
├── docs/                      # Protocol documentation
└── Package.swift              # Swift Package Manager
```

## API Reference

### SyncManager

The main entry point for all sync operations.

#### Initialization

| Method | Description |
|--------|-------------|
| `initialize(clientId?)` | Initialize sync engine, load clock state |
| `setToken(token)` | Set authentication token |
| `setBudget(fileId, groupId)` | Set active budget file |

#### Sync Operations

| Method | Description |
|--------|-------------|
| `fullSync()` | Download all data from server |
| `sync()` | Incremental bidirectional sync |
| `downloadBudgetFile(fileId)` | Download budget SQLite file |
| `downloadAndInstallBudget(fileId, path)` | Download and install budget |
| `getPendingChangeCount()` | Count of unsynced local changes |

#### Accounts

| Method | Description |
|--------|-------------|
| `createAccount(id, name, offbudget)` | Create new account |
| `updateAccount(id, field, value)` | Update account field |
| `deleteAccount(id)` | Delete account (tombstone) |
| `moveAccount(id, targetId)` | Reorder account |
| `reopenAccount(id)` | Reopen closed account |

#### Transactions

| Method | Description |
|--------|-------------|
| `createTransaction(id, accountId, date, amount, ...)` | Create transaction |
| `updateTransaction(id, field, value)` | Update transaction field |
| `deleteTransaction(id)` | Delete transaction |
| `createTransfer(id, fromAccountId, toAccountId, date, amount)` | Create transfer |
| `createSplitTransaction(...)` | Create split transaction |

#### Payees

| Method | Description |
|--------|-------------|
| `createPayee(id, name)` | Create new payee |
| `updatePayee(id, field, value)` | Update payee |
| `deletePayee(id)` | Delete payee |

#### Categories

| Method | Description |
|--------|-------------|
| `createCategory(id, name, groupId, isIncome, sortOrder, hidden)` | Create category |
| `updateCategory(id, field, value)` | Update category |
| `deleteCategory(id)` | Delete category |
| `moveCategory(id, newGroupId, targetId)` | Move/reorder category |
| `createCategoryGroup(id, name, isIncome, sortOrder, hidden)` | Create group |
| `updateCategoryGroup(id, field, value)` | Update group |
| `deleteCategoryGroup(id)` | Delete group |
| `moveCategoryGroup(id, targetId)` | Reorder group |

#### Budget Operations

| Method | Description |
|--------|-------------|
| `setBudgetAmount(categoryId, month, amount)` | Set monthly budget |
| `setBudgetGoal(categoryId, month, goal)` | Set budget goal |
| `setBudgetCarryover(categoryId, month, carryover)` | Set carryover |
| `transferBudget(fromId, toId, month, amount)` | Transfer between categories |
| `copyBudgetFromPreviousMonth(month)` | Copy last month's budget |
| `zeroBudgetsForMonth(month)` | Reset all budgets to zero |
| `setNMonthAverage(categoryId, month, n)` | Budget based on spending average |
| `set3MonthAverage(month)` | Set 3-month average for all |
| `holdForNextMonth(month, amount)` | Hold funds for next month |
| `coverOverspending(fromId, toId, month, amount)` | Cover overspent category |

### SyncResult

```kotlin
sealed class SyncResult {
    data class Success(
        val messagesSent: Int,
        val messagesReceived: Int,
        val messagesApplied: Int
    ) : SyncResult()

    data class Error(val message: String) : SyncResult()
}
```

### Data Formats

| Format | Example | Description |
|--------|---------|-------------|
| Date | `20260123` | YYYYMMDD integer |
| Month | `202601` | YYYYMM long |
| Amount | `-5000` | Cents (negative = expense) |
| ID | `"a1b2c3d4-..."` | UUID string |

## Architecture

### CRDT Sync Protocol

Actual Budget uses CRDTs (Conflict-free Replicated Data Types) for sync:

1. **Hybrid Logical Clocks (HLC)** — Each change gets a globally-unique timestamp
2. **Merkle Tries** — Efficient comparison to find sync points
3. **Message-based** — Changes are individual field updates
4. **Last-write-wins** — Conflicts resolved by timestamp ordering

### Sync Flow

```
Device (offline)                    Server
─────────────────                   ─────────────────
1. User creates transaction
2. CRDT message generated with HLC timestamp
3. Stored locally + merkle trie updated

   ... device comes online ...

4. Compare merkle tries to find divergence
5. Exchange messages since divergence point
6. Both sides apply received messages
7. Merkle tries now match ✓
```

### Data Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│ SyncManager │────▶│ SyncEngine  │────▶│  SQLite DB  │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │
       │                   ▼
       │            ┌─────────────┐
       └───────────▶│ HTTP/Proto  │────▶ Actual Server
                    └─────────────┘
```

## Building

### Prerequisites

- JDK 17+
- Android SDK (for Android builds)
- Xcode 15+ (for iOS builds)

### Build Commands

```bash
cd kotlin

# Build all platforms
./gradlew build

# Run tests
./gradlew desktopTest

# Build Android AAR
./gradlew assembleRelease

# Build iOS XCFramework
./gradlew linkReleaseFrameworkIosArm64
./gradlew linkReleaseFrameworkIosSimulatorArm64
./gradlew assembleXCFramework
```

### Run Demo

Test against your Actual Budget server:

```bash
cd kotlin
./gradlew runTest \
  -PserverUrl="http://your-server:5006" \
  -Ppassword="your-password"
```

Or via environment variables:

```bash
export ACTUAL_SERVER_URL="http://your-server:5006"
export ACTUAL_PASSWORD="your-password"
./gradlew runTest
```

## iOS Integration (Swift)

```swift
import ActualSync

// Create database
let driverFactory = DatabaseDriverFactory()
let database = DatabaseHelperKt.createDatabase(
    driverFactory: driverFactory,
    dbName: "actual.db"
)

// Create HTTP client
let httpClient = HttpClientKt.createHttpClient()

// Initialize sync manager
let syncManager = SyncManager(
    serverUrl: "https://your-server.com",
    httpClient: httpClient,
    database: database
)

syncManager.initialize(clientId: nil)
syncManager.setToken(token: authToken)
syncManager.setBudget(fileId: fileId, groupId: groupId)

// Sync (async)
Task {
    let result = try await syncManager.fullSync()
    switch result {
    case let success as SyncResult.Success:
        print("Synced \(success.messagesApplied) messages")
    case let error as SyncResult.Error:
        print("Error: \(error.message)")
    default:
        break
    }
}
```

## Documentation

- [PROTOCOL.md](docs/PROTOCOL.md) — Complete sync protocol specification
- [CONTRIBUTING.md](docs/CONTRIBUTING.md) — Contribution guidelines
- [kotlin/README.md](kotlin/README.md) — Kotlin-specific documentation

## Dependencies

| Library | Purpose |
|---------|---------|
| [Ktor Client](https://ktor.io/) | Multiplatform HTTP |
| [SQLDelight](https://cashapp.github.io/sqldelight/) | Multiplatform SQLite |
| [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON encoding |
| [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) | Async/await |
| [Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime) | Date/time handling |

## License

MIT License — see [LICENSE](LICENSE) for details.

## Related Projects

- [Actual Budget](https://github.com/actualbudget/actual) — The official app
- [Actual Server](https://github.com/actualbudget/actual-server) — Self-hosted sync server
