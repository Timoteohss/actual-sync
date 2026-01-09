import '../crdt/timestamp.dart';
import '../crdt/merkle.dart';
import '../crdt/clock.dart';
import '../sync/sync_client.dart';
import 'models.dart';

/// High-level API client for Actual Budget.
///
/// This is the main entry point for interacting with Actual Budget
/// from Dart/Flutter applications.
class ActualClient {
  final String serverUrl;
  final String dataDir;
  final SyncClient _syncClient;

  SyncClock? _clock;
  bool _initialized = false;

  ActualClient({
    required this.serverUrl,
    required this.dataDir,
    SyncClient? syncClient,
  }) : _syncClient = syncClient ?? SyncClient(serverUrl: serverUrl);

  /// Initialize the client and load local state.
  Future<void> init() async {
    // TODO: Load clock state from local storage
    final nodeId = Timestamp.makeClientId();
    _clock = ClockManager.makeClock(
      Timestamp(0, 0, nodeId),
      Merkle.emptyTrie(),
    );
    ClockManager.setClock(_clock!);
    _initialized = true;
  }

  /// Authenticate with the server.
  Future<void> login({required String password}) async {
    await _syncClient.login(password);
  }

  /// List available budgets.
  Future<List<BudgetFile>> getBudgets() async {
    return _syncClient.listFiles();
  }

  /// Download and open a budget.
  Future<void> downloadBudget({
    required String syncId,
    String? password,
  }) async {
    final data = await _syncClient.downloadBudget(syncId);
    // TODO: Save to local storage
    // TODO: Decrypt if password provided
    // TODO: Initialize local database
  }

  /// Sync local changes with server.
  Future<void> sync() async {
    _checkInitialized();
    // TODO: Implement full sync flow
    // 1. Get local messages since last sync
    // 2. Compare merkle tries to find sync point
    // 3. Exchange messages with server
    // 4. Apply received messages locally
  }

  /// Get all accounts.
  Future<List<Account>> getAccounts() async {
    _checkInitialized();
    // TODO: Query local database
    return [];
  }

  /// Get transactions for an account.
  Future<List<Transaction>> getTransactions({
    required String accountId,
    required String startDate,
    required String endDate,
  }) async {
    _checkInitialized();
    // TODO: Query local database
    return [];
  }

  /// Create a new transaction.
  Future<String> createTransaction(NewTransaction transaction) async {
    _checkInitialized();
    final ts = _clock!.send();
    // TODO: Create CRDT message and apply locally
    return ts.toString();
  }

  /// Update an existing transaction.
  Future<void> updateTransaction({
    required String id,
    required Map<String, dynamic> fields,
  }) async {
    _checkInitialized();
    final ts = _clock!.send();
    // TODO: Create CRDT messages for each field update
  }

  /// Delete a transaction.
  Future<void> deleteTransaction(String id) async {
    _checkInitialized();
    final ts = _clock!.send();
    // TODO: Create CRDT message for tombstone
  }

  /// Get all categories.
  Future<List<Category>> getCategories() async {
    _checkInitialized();
    // TODO: Query local database
    return [];
  }

  /// Get all category groups with their categories.
  Future<List<CategoryGroup>> getCategoryGroups() async {
    _checkInitialized();
    // TODO: Query local database
    return [];
  }

  /// Get all payees.
  Future<List<Payee>> getPayees() async {
    _checkInitialized();
    // TODO: Query local database
    return [];
  }

  /// Get budget for a specific month.
  Future<BudgetMonth> getBudgetMonth(String month) async {
    _checkInitialized();
    // TODO: Query local database
    throw UnimplementedError();
  }

  /// Set budget amount for a category in a month.
  Future<void> setBudgetAmount({
    required String month,
    required String categoryId,
    required int amount,
  }) async {
    _checkInitialized();
    final ts = _clock!.send();
    // TODO: Create CRDT message for budget update
  }

  /// Shutdown the client and save state.
  Future<void> shutdown() async {
    // TODO: Save clock state to local storage
    _initialized = false;
    _syncClient.dispose();
  }

  void _checkInitialized() {
    if (!_initialized) {
      throw StateError('Client not initialized. Call init() first.');
    }
  }
}
