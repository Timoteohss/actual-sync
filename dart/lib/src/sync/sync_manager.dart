import 'dart:io';
import 'dart:typed_data';

import 'package:archive/archive.dart';
import 'package:http/http.dart' as http;

import '../crdt/merkle.dart';
import '../crdt/timestamp.dart';
import '../db/database.dart';
import '../proto/sync_messages.dart';
import 'sync_engine.dart';

/// High-level sync manager that coordinates sync operations.
class SyncManager {
  final String serverUrl;
  final http.Client _httpClient;
  final ActualDatabase database;

  String? _token;
  String? _fileId;
  String? _groupId;

  late MutableClock _clock;
  late SyncEngine _engine;
  bool _initialized = false;

  SyncManager({
    required this.serverUrl,
    required this.database,
    http.Client? httpClient,
  }) : _httpClient = httpClient ?? http.Client();

  /// Initialize the sync manager with a client ID.
  Future<void> initialize([String? clientId]) async {
    final nodeId = clientId ?? Timestamp.makeClientId();

    // Try to load clock state from database
    final savedMillis =
        int.tryParse(await database.getSyncMetadataValue('clock_millis') ?? '') ?? 0;
    final savedCounter =
        int.tryParse(await database.getSyncMetadataValue('clock_counter') ?? '') ?? 0;
    final savedNode =
        await database.getSyncMetadataValue('clock_node') ?? nodeId;

    _clock = MutableClock(millis: savedMillis, counter: savedCounter, node: savedNode);
    _engine = SyncEngine(db: database, clock: _clock);
    await _engine.initialize();
    _initialized = true;
  }

  /// Save clock state to database.
  Future<void> _saveClockState() async {
    await database.setSyncMetadataValue('clock_millis', _clock.millis.toString());
    await database.setSyncMetadataValue('clock_counter', _clock.counter.toString());
    await database.setSyncMetadataValue('clock_node', _clock.node);
  }

  /// Set authentication token.
  void setToken(String token) {
    _token = token;
  }

  /// Set the current budget file.
  void setBudget(String fileId, String groupId) {
    _fileId = fileId;
    _groupId = groupId;
  }

  /// Perform a full sync from scratch.
  /// Downloads all messages from the server.
  Future<SyncResult> fullSync() async {
    _checkInitialized();
    if (_fileId == null || _groupId == null) {
      throw StateError('Budget not set. Call setBudget() first.');
    }

    final request = await _engine.buildSyncRequest(_fileId!, _groupId!, true);
    return _performSync(request);
  }

  /// Download the complete budget database from the server.
  /// This downloads the full SQLite database, not just sync messages.
  Future<Uint8List?> downloadBudgetFile(String fileId) async {
    try {
      print('[SyncManager] Downloading budget file: $fileId');

      final response = await _httpClient.get(
        Uri.parse('$serverUrl/sync/download-user-file'),
        headers: {
          if (_token != null) 'X-ACTUAL-TOKEN': _token!,
          'X-ACTUAL-FILE-ID': fileId,
        },
      );

      if (response.statusCode == 200) {
        print('[SyncManager] Downloaded ${response.bodyBytes.length} bytes');
        return response.bodyBytes;
      } else {
        print('[SyncManager] Download failed: ${response.statusCode}');
        return null;
      }
    } catch (e) {
      print('[SyncManager] Download error: $e');
      return null;
    }
  }

  /// Extract a downloaded budget zip and install the database.
  ///
  /// [zipData] The raw zip file bytes from downloadBudgetFile()
  /// [targetDbPath] The path where the database should be installed
  /// Returns true if extraction and installation succeeded
  Future<bool> extractAndInstallBudget(
      Uint8List zipData, String targetDbPath) async {
    try {
      print('[SyncManager] Extracting budget to: $targetDbPath');
      print('[SyncManager] Zip data size: ${zipData.length} bytes');

      // Decode the zip archive
      final archive = ZipDecoder().decodeBytes(zipData);

      // Find db.sqlite in the archive
      ArchiveFile? dbFile;
      for (final file in archive.files) {
        if (file.name.endsWith('db.sqlite')) {
          dbFile = file;
          break;
        }
      }

      if (dbFile == null) {
        print('[SyncManager] Failed to find db.sqlite in zip');
        return false;
      }

      print('[SyncManager] Found db.sqlite, size: ${dbFile.size} bytes');

      // Delete existing file first to ensure clean install
      final targetFile = File(targetDbPath);
      if (await targetFile.exists()) {
        print('[SyncManager] Deleting existing database at: $targetDbPath');
        await targetFile.delete();
        // Also delete WAL and SHM files
        final walFile = File('$targetDbPath-wal');
        final shmFile = File('$targetDbPath-shm');
        if (await walFile.exists()) await walFile.delete();
        if (await shmFile.exists()) await shmFile.delete();
      }

      // Ensure parent directory exists
      await targetFile.parent.create(recursive: true);

      // Write the database file
      await targetFile.writeAsBytes(dbFile.content as List<int>);

      // Verify the file exists
      if (await targetFile.exists()) {
        print('[SyncManager] Budget installed successfully');
        return true;
      } else {
        print('[SyncManager] ERROR: target file does not exist after write!');
        return false;
      }
    } catch (e) {
      print('[SyncManager] Error installing budget: $e');
      return false;
    }
  }

  /// Download and install a budget in one step.
  /// After calling this, you should reinitialize the database and SyncManager.
  Future<bool> downloadAndInstallBudget(
      String fileId, String targetDbPath) async {
    final zipData = await downloadBudgetFile(fileId);
    if (zipData == null) {
      print('[SyncManager] Failed to download budget');
      return false;
    }

    return extractAndInstallBudget(zipData, targetDbPath);
  }

  /// Perform an incremental sync.
  /// Only syncs changes since last sync point.
  Future<SyncResult> sync() async {
    _checkInitialized();
    if (_fileId == null || _groupId == null) {
      throw StateError('Budget not set. Call setBudget() first.');
    }

    final serverMerkle = await _engine.getServerMerkle();

    final SyncRequest request;
    if (serverMerkle != null) {
      request = await _engine.buildIncrementalSyncRequest(
          _fileId!, _groupId!, serverMerkle);
    } else {
      // First sync, do full
      request = await _engine.buildSyncRequest(_fileId!, _groupId!, true);
    }

    return _performSync(request);
  }

  /// Perform the actual sync HTTP request.
  Future<SyncResult> _performSync(SyncRequest request) async {
    try {
      final requestBytes = request.encode();

      final response = await _httpClient.post(
        Uri.parse('$serverUrl/sync/sync'),
        headers: {
          if (_token != null) 'X-ACTUAL-TOKEN': _token!,
          if (_fileId != null) 'X-ACTUAL-FILE-ID': _fileId!,
          'Content-Type': 'application/actual-sync',
        },
        body: requestBytes,
      );

      if (response.statusCode == 200) {
        final responseBytes = response.bodyBytes;
        final syncResponse = SyncResponse.decode(responseBytes);

        final applied = await _engine.processSyncResponse(syncResponse);
        await _saveClockState();

        return SyncResult.success(
          messagesSent: request.messages.length,
          messagesReceived: syncResponse.messages.length,
          messagesApplied: applied,
        );
      } else {
        return SyncResult.error('Sync failed: ${response.statusCode}');
      }
    } catch (e) {
      return SyncResult.error('Sync error: $e');
    }
  }

  // ========== Local Change Methods ==========

  /// Create a new account locally.
  Future<String> createAccount(String id, String name,
      {bool offbudget = false}) async {
    _checkInitialized();
    await _engine.createChange('accounts', id, 'name', name);
    await _engine.createChange('accounts', id, 'offbudget', offbudget ? 1 : 0);
    await _engine.createChange('accounts', id, 'closed', 0);
    await _engine.createChange('accounts', id, 'tombstone', 0);
    return id;
  }

  /// Update an account field.
  Future<void> updateAccount(String id, String field, Object? value) async {
    _checkInitialized();
    await _engine.createChange('accounts', id, field, value);
  }

  /// Delete an account (set tombstone).
  Future<void> deleteAccount(String id) async {
    _checkInitialized();
    await _engine.createChange('accounts', id, 'tombstone', 1);
  }

  /// Create a new payee locally.
  /// Also creates the required payee_mapping entry (id -> id) for the payee to be usable.
  Future<String> createPayee(String id, String name) async {
    _checkInitialized();
    await _engine.createChange('payees', id, 'name', name);
    await _engine.createChange('payees', id, 'tombstone', 0);
    // Actual Budget requires a payee_mapping entry for each payee
    // The mapping points to itself (id -> id) for regular payees
    // NOTE: payee_mapping table has NO tombstone column in Actual Budget
    await _engine.createChange('payee_mapping', id, 'targetId', id);
    return id;
  }

  /// Update a payee field.
  Future<void> updatePayee(String id, String field, Object? value) async {
    _checkInitialized();
    await _engine.createChange('payees', id, field, value);
  }

  /// Delete a payee (set tombstone).
  Future<void> deletePayee(String id) async {
    _checkInitialized();
    await _engine.createChange('payees', id, 'tombstone', 1);
  }

  /// Create a new category locally.
  Future<String> createCategory(String id, String name, String groupId) async {
    _checkInitialized();
    await _engine.createChange('categories', id, 'name', name);
    await _engine.createChange('categories', id, 'cat_group', groupId);
    await _engine.createChange('categories', id, 'tombstone', 0);
    return id;
  }

  /// Update a category field.
  Future<void> updateCategory(String id, String field, Object? value) async {
    _checkInitialized();
    await _engine.createChange('categories', id, field, value);
  }

  /// Delete a category (set tombstone).
  Future<void> deleteCategory(String id) async {
    _checkInitialized();
    await _engine.createChange('categories', id, 'tombstone', 1);
  }

  /// Create a new transaction locally.
  ///
  /// [id] Transaction ID
  /// [accountId] Account ID
  /// [date] Date as YYYYMMDD integer
  /// [amount] Amount in cents (negative for expenses)
  /// [payeeId] Optional payee ID (stored in 'description' column in Actual)
  /// [categoryId] Optional category ID
  /// [notes] Optional notes
  Future<String> createTransaction({
    required String id,
    required String accountId,
    required int date,
    required int amount,
    String? payeeId,
    String? categoryId,
    String? notes,
  }) async {
    _checkInitialized();
    await _engine.createChange('transactions', id, 'acct', accountId);
    await _engine.createChange('transactions', id, 'date', date);
    await _engine.createChange('transactions', id, 'amount', amount);
    // Actual stores payee ID in 'description' column
    if (payeeId != null) {
      await _engine.createChange('transactions', id, 'description', payeeId);
    }
    if (categoryId != null) {
      await _engine.createChange('transactions', id, 'category', categoryId);
    }
    if (notes != null) {
      await _engine.createChange('transactions', id, 'notes', notes);
    }
    await _engine.createChange('transactions', id, 'cleared', 0);
    await _engine.createChange('transactions', id, 'tombstone', 0);
    return id;
  }

  /// Update a transaction field.
  Future<void> updateTransaction(
      String id, String field, Object? value) async {
    _checkInitialized();
    await _engine.createChange('transactions', id, field, value);
  }

  /// Delete a transaction (set tombstone).
  Future<void> deleteTransaction(String id) async {
    _checkInitialized();
    await _engine.createChange('transactions', id, 'tombstone', 1);
  }

  /// Get the number of pending changes.
  int getPendingChangeCount() {
    _checkInitialized();
    return _engine.getPendingMessages().length;
  }

  /// Check if local and server are in sync.
  Future<bool> isInSync() async {
    _checkInitialized();
    return _engine.isInSync();
  }

  /// Get the sync engine for advanced operations.
  SyncEngine get engine {
    _checkInitialized();
    return _engine;
  }

  // ========== Query Methods ==========

  /// Get all accounts.
  Future<List<Account>> getAccounts() => database.getAccounts();

  /// Get all payees.
  Future<List<Payee>> getPayees() => database.getPayees();

  /// Get all categories.
  Future<List<Category>> getCategories() => database.getCategories();

  /// Get all category groups.
  Future<List<CategoryGroup>> getCategoryGroups() =>
      database.getCategoryGroups();

  /// Get transactions by account.
  Future<List<Transaction>> getTransactionsByAccount(String accountId) =>
      database.getTransactionsByAccount(accountId);

  /// Get budget for month.
  Future<List<ZeroBudget>> getBudgetForMonth(int month) =>
      database.getBudgetForMonth(month);

  // ========== Diagnostic Methods ==========

  /// Get diagnostic info about what's in the database.
  Future<String> getDatabaseDiagnostics() async {
    final sb = StringBuffer();
    sb.writeln('=== Database Diagnostics ===');

    try {
      sb.writeln('\n--- Row Counts ---');
      sb.writeln('Accounts: ${await database.countAccounts()}');
      sb.writeln('Payees: ${await database.countPayees()}');
      sb.writeln('Categories: ${await database.countCategories()}');
      sb.writeln('Transactions: ${await database.countTransactions()}');

      sb.writeln('\n--- Active Accounts ---');
      final accounts = await database.getAccounts();
      for (final acc in accounts) {
        sb.writeln('  ${acc.id}: name="${acc.name}"');
      }

      sb.writeln('\n--- Active Payees (first 10) ---');
      final payees = await database.getPayees();
      for (final p in payees.take(10)) {
        sb.writeln('  ${p.id}: name="${p.name}"');
      }

      sb.writeln('\n--- Active Categories (first 10) ---');
      final categories = await database.getCategories();
      for (final c in categories.take(10)) {
        sb.writeln('  ${c.id}: name="${c.name}", group=${c.catGroup}');
      }
    } catch (e) {
      sb.writeln('Error during diagnostics: $e');
    }

    return sb.toString();
  }

  void _checkInitialized() {
    if (!_initialized) {
      throw StateError('SyncManager not initialized. Call initialize() first.');
    }
  }

  /// Dispose resources.
  void dispose() {
    _httpClient.close();
  }
}

/// Mutable clock for timestamp generation.
class MutableClock {
  int millis;
  int counter;
  final String node;

  static const int _maxDriftMs = 5 * 60 * 1000;
  static const int _maxCounter = 0xFFFF;

  MutableClock({
    this.millis = 0,
    this.counter = 0,
    required this.node,
  });

  /// Generate a new timestamp for a local change (send operation).
  Timestamp send() {
    final phys = DateTime.now().millisecondsSinceEpoch;

    final lNew = millis > phys ? millis : phys;
    final cNew = lNew == millis ? counter + 1 : 0;

    if (lNew - phys > _maxDriftMs) {
      throw ClockDriftError('Drift: ${lNew - phys}ms exceeds max ${_maxDriftMs}ms');
    }
    if (cNew > _maxCounter) {
      throw OverflowError();
    }

    millis = lNew;
    counter = cNew;

    return Timestamp(millis, counter, node);
  }
}

/// Result of a sync operation.
sealed class SyncResult {
  const SyncResult();

  factory SyncResult.success({
    required int messagesSent,
    required int messagesReceived,
    required int messagesApplied,
  }) = SyncSuccess;

  factory SyncResult.error(String message) = SyncError;
}

class SyncSuccess extends SyncResult {
  final int messagesSent;
  final int messagesReceived;
  final int messagesApplied;

  const SyncSuccess({
    required this.messagesSent,
    required this.messagesReceived,
    required this.messagesApplied,
  });

  @override
  String toString() =>
      'SyncSuccess(sent: $messagesSent, received: $messagesReceived, applied: $messagesApplied)';
}

class SyncError extends SyncResult {
  final String message;

  const SyncError(this.message);

  @override
  String toString() => 'SyncError: $message';
}

/// Clock drift error.
class ClockDriftError implements Exception {
  final String message;
  ClockDriftError([this.message = 'Maximum clock drift exceeded']);
  @override
  String toString() => 'ClockDriftError: $message';
}

/// Counter overflow error.
class OverflowError implements Exception {
  @override
  String toString() => 'OverflowError: Timestamp counter overflow';
}
