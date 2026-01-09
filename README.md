# actual-sync

Native sync libraries for [Actual Budget](https://actualbudget.org/) - enabling mobile and desktop apps to sync directly with Actual Budget servers without JavaScript.

## Overview

This project provides native implementations of Actual Budget's CRDT-based sync protocol, allowing you to build native iOS, Android, and desktop applications that sync seamlessly with your Actual Budget server.

### Features

- **Full CRDT Implementation** - Hybrid Logical Clocks, Merkle tries, conflict-free sync
- **Bidirectional Sync** - Download from server, upload local changes
- **Local-First** - Works offline, syncs when connected
- **Multi-Platform** - Kotlin Multiplatform (Android, iOS, Desktop)
- **SQLite Storage** - Local database with SQLDelight

## Project Structure

```
actual-sync/
├── kotlin/          # Kotlin Multiplatform library
│   ├── src/
│   │   ├── commonMain/     # Shared code (CRDT, sync, protobuf)
│   │   ├── androidMain/    # Android-specific (SQLite driver)
│   │   ├── iosMain/        # iOS-specific (SQLite driver)
│   │   └── desktopMain/    # Desktop/JVM (SQLite driver, demo app)
│   └── build.gradle.kts
├── dart/            # Dart/Flutter library (scaffolded)
└── docs/            # Protocol documentation
```

## Quick Start

### Prerequisites

- JDK 17+
- Android SDK (for Android builds)
- Xcode (for iOS builds)

### Build

```bash
cd kotlin
./gradlew build
```

### Run Demo

```bash
cd kotlin
./gradlew runTest -PserverUrl="http://your-server:5006" -Ppassword="your-password"
```

Or using environment variables:

```bash
export ACTUAL_SERVER_URL="http://your-server:5006"
export ACTUAL_PASSWORD="your-password"
./gradlew runTest
```

## Usage

### Android/Kotlin

```kotlin
// Initialize
val driverFactory = DatabaseDriverFactory(context)
val database = createDatabase(driverFactory, "actual.db")
val syncManager = SyncManager(serverUrl, httpClient, database)

syncManager.initialize()
syncManager.setToken(authToken)
syncManager.setBudget(fileId, groupId)

// Full sync
when (val result = syncManager.fullSync()) {
    is SyncResult.Success -> println("Synced ${result.messagesApplied} messages")
    is SyncResult.Error -> println("Error: ${result.message}")
}

// Create local changes (works offline)
syncManager.createTransaction(
    id = uuid4().toString(),
    accountId = "account-id",
    date = 20260115,  // YYYYMMDD
    amount = -5000,   // -$50.00 in cents
    notes = "Grocery shopping"
)

// Sync to server when online
syncManager.sync()
```

### iOS (Swift)

```swift
// Import the framework
import ActualSync

// Initialize
let driverFactory = DatabaseDriverFactory()
let database = DatabaseDriverFactoryKt.createDatabase(driverFactory: driverFactory, dbName: "actual.db")
let syncManager = SyncManager(serverUrl: serverUrl, httpClient: httpClient, database: database)

syncManager.initialize()
syncManager.setToken(token: authToken)
syncManager.setBudget(fileId: fileId, groupId: groupId)

// Sync
syncManager.fullSync { result in
    // Handle result
}
```

## Architecture

### CRDT Sync Protocol

Actual Budget uses CRDTs (Conflict-free Replicated Data Types) for sync:

1. **Hybrid Logical Clocks (HLC)** - Each change gets a unique timestamp
2. **Merkle Tries** - Efficient comparison to find sync points
3. **Message-based** - Changes are individual field updates
4. **Last-write-wins** - Conflicts resolved by timestamp ordering

### How Offline Sync Works

```
Device (offline)                    Server
─────────────────                   ─────────────────
1. User creates transaction
2. CRDT message generated
3. Stored locally + queued

   ... device comes online ...

4. Sync request with pending messages
5. Server processes & responds
6. Device applies server changes
7. Both in sync ✓
```

### Data Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  SyncManager │────▶│  SyncEngine │────▶│  SQLite DB  │
└─────────────┘     └─────────────┘     └─────────────┘
       │                   │
       │                   ▼
       │            ┌─────────────┐
       └───────────▶│ HTTP/Proto  │────▶ Actual Server
                    └─────────────┘
```

## API Reference

### SyncManager

| Method | Description |
|--------|-------------|
| `initialize()` | Load local state and clock |
| `setToken(token)` | Set authentication token |
| `setBudget(fileId, groupId)` | Set active budget |
| `fullSync()` | Download all data from server |
| `sync()` | Incremental bidirectional sync |
| `createAccount(...)` | Create account locally |
| `createPayee(...)` | Create payee locally |
| `createTransaction(...)` | Create transaction locally |
| `updateTransaction(...)` | Update transaction field |
| `deleteTransaction(...)` | Delete (tombstone) transaction |

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

## Building for Production

### Android AAR

```bash
./gradlew :kotlin:assembleRelease
# Output: kotlin/build/outputs/aar/
```

### iOS Framework

```bash
./gradlew :kotlin:linkReleaseFrameworkIosArm64
# Output: kotlin/build/bin/iosArm64/releaseFramework/ActualSync.framework
```

## Contributing

Contributions are welcome! Please read the contributing guidelines before submitting PRs.

### Running Tests

```bash
cd kotlin
./gradlew desktopTest
```

## License

MIT License - see [LICENSE](LICENSE) for details.

## Acknowledgments

- [Actual Budget](https://actualbudget.org/) - The amazing open-source budgeting app
- [SQLDelight](https://cashapp.github.io/sqldelight/) - Multiplatform SQLite
- [Ktor](https://ktor.io/) - Multiplatform HTTP client

## Related Projects

- [actualbudget/actual](https://github.com/actualbudget/actual) - Official Actual Budget
- [actualbudget/actual-server](https://github.com/actualbudget/actual-server) - Sync server
