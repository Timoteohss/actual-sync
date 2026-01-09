# actual_sync

Dart/Flutter implementation of the Actual Budget sync protocol.

## Platforms

- iOS
- Android
- macOS
- Windows
- Linux
- Web

## Installation

```yaml
dependencies:
  actual_sync: ^0.1.0
```

## Usage

```dart
import 'package:actual_sync/actual_sync.dart';

// Initialize client
final client = ActualClient(
  serverUrl: 'https://your-actual-server.com',
  dataDir: await getApplicationDocumentsDirectory(),
);

await client.init();

// Login
await client.login(password: 'your-password');

// Download a budget
await client.downloadBudget(syncId: 'your-budget-id');

// Get accounts
final accounts = await client.getAccounts();

// Get transactions
final transactions = await client.getTransactions(
  accountId: accounts.first.id,
  startDate: '2025-01-01',
  endDate: '2025-12-31',
);

// Create a transaction
await client.createTransaction(NewTransaction(
  accountId: accounts.first.id,
  date: '2025-01-15',
  amount: -5000, // -$50.00 in cents
  payeeName: 'Coffee Shop',
  categoryId: 'food-category-id',
));

// Sync changes
await client.sync();

// Cleanup
await client.shutdown();
```

## Project Structure

```
lib/
├── actual_sync.dart          # Main export file
└── src/
    ├── crdt/                 # CRDT implementation
    │   ├── timestamp.dart
    │   ├── merkle.dart
    │   └── clock.dart
    ├── sync/                 # Sync protocol
    │   └── sync_client.dart
    └── api/                  # High-level API
        └── actual_client.dart
```

## Building Protobufs

```bash
# Generate Dart files from proto
protoc --dart_out=lib/src/proto ../docs/schemas/sync.proto
```

## License

MIT
