# actual-sync-kmp

Kotlin Multiplatform implementation of the Actual Budget sync protocol.

## Platforms

- Android
- iOS (via XCFramework)
- JVM Desktop

## Installation

### Android (Gradle)

```kotlin
dependencies {
    implementation("com.actualbudget:actual-sync-kmp:0.1.0")
}
```

### iOS (Swift Package Manager)

Coming soon - XCFramework distribution.

## Usage

```kotlin
import com.actualbudget.sync.ActualSyncClient

// Initialize client
val client = ActualSyncClient(
    serverUrl = "https://your-actual-server.com",
    dataDir = context.filesDir.path // platform-specific
)

// Login
client.login(password = "your-password")

// Download a budget
client.downloadBudget(syncId = "your-budget-id")

// Get accounts
val accounts = client.getAccounts()

// Get transactions
val transactions = client.getTransactions(
    accountId = accounts.first().id,
    startDate = "2025-01-01",
    endDate = "2025-12-31"
)

// Sync changes
client.sync()
```

## Building

```bash
# Build all targets
./gradlew build

# Build Android AAR
./gradlew :assembleRelease

# Build iOS Framework
./gradlew :linkReleaseFrameworkIosArm64
```

## Project Structure

```
src/
├── commonMain/kotlin/com/actualbudget/sync/
│   ├── crdt/           # CRDT implementation (Timestamp, Merkle)
│   ├── sync/           # Sync client and protocol
│   └── api/            # High-level API
├── androidMain/        # Android-specific code
├── iosMain/            # iOS-specific code
└── desktopMain/        # JVM desktop-specific code
```

## License

MIT
