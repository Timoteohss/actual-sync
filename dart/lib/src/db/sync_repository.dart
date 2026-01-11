import 'dart:convert';
import 'dart:typed_data';

import 'package:drift/drift.dart';

import '../proto/sync_messages.dart' as proto;
import 'database.dart';

/// Repository for syncing CRDT messages to the local database.
class SyncRepository {
  final ActualDatabase db;

  SyncRepository(this.db);

  /// Apply a list of message envelopes to the database.
  /// Messages are stored in the CRDT log and applied to their respective tables.
  Future<void> applyMessages(List<proto.MessageEnvelope> envelopes) async {
    for (final envelope in envelopes) {
      if (!envelope.isEncrypted) {
        try {
          final message = envelope.decodeMessage();
          await applyMessage(envelope.timestamp, message);
        } catch (e) {
          // Log error but continue processing
          print('Failed to apply message ${envelope.timestamp}: $e');
        }
      }
    }
  }

  /// Apply a single CRDT message.
  Future<void> applyMessage(String timestamp, proto.Message message) async {
    // Store in CRDT log (value stored as BLOB)
    await db.insertMessage(
      timestamp: timestamp,
      dataset: message.dataset,
      row: message.row,
      column: message.column,
      value: Uint8List.fromList(utf8.encode(message.value)),
    );

    // Parse the value
    final parsedValue = parseValue(message.value);

    // Ensure row exists, then update the column
    switch (message.dataset) {
      case 'accounts':
        await _ensureAccountExists(message.row);
        await _applyAccountColumn(message.row, message.column, parsedValue);
        break;
      case 'payees':
        await _ensurePayeeExists(message.row);
        await _applyPayeeColumn(message.row, message.column, parsedValue);
        break;
      case 'payee_mapping':
        // Handle payee mapping
        break;
      case 'categories':
        await _ensureCategoryExists(message.row);
        await _applyCategoryColumn(message.row, message.column, parsedValue);
        break;
      case 'category_groups':
        await _ensureCategoryGroupExists(message.row);
        await _applyCategoryGroupColumn(
            message.row, message.column, parsedValue);
        break;
      case 'transactions':
        await _ensureTransactionExists(message.row);
        await _applyTransactionColumn(
            message.row, message.column, parsedValue);
        break;
      case 'zero_budgets':
      case 'zero_budget_months':
        // Handle budget data
        break;
      case 'schedules':
        // Handle schedules
        break;
      case 'rules':
        // Handle rules
        break;
      case 'notes':
        // Handle notes
        break;
      default:
        // Unknown dataset, skip
        break;
    }
  }

  /// Parse Actual's typed value format.
  /// Values are prefixed with type: S: for string, N: for number, 0: for null
  Object? parseValue(String value) {
    if (value.startsWith('S:')) {
      return value.substring(2);
    } else if (value.startsWith('N:')) {
      return int.tryParse(value.substring(2)) ?? 0;
    } else if (value.startsWith('0:') || value == 'null') {
      return null;
    } else {
      return value; // Fallback to raw value
    }
  }

  /// Encode a value to Actual's typed format.
  static String encodeValue(Object? value) {
    if (value == null) {
      return '0:';
    } else if (value is int) {
      return 'N:$value';
    } else if (value is double) {
      return 'N:${value.toInt()}';
    } else {
      return 'S:$value';
    }
  }

  // ========== Account Operations ==========

  Future<void> _ensureAccountExists(String id) async {
    final existing = await db.getAccountById(id);
    if (existing == null) {
      await db.insertAccount(AccountsCompanion(
        id: Value(id),
        name: const Value(''),
        offbudget: const Value(0),
        closed: const Value(0),
        sortOrder: const Value(null),
        tombstone: const Value(0),
      ));
    }
  }

  Future<void> _applyAccountColumn(
      String id, String column, Object? value) async {
    final current = await db.getAccountById(id);
    if (current == null) return;

    await db.insertAccount(AccountsCompanion(
      id: Value(id),
      name: Value(column == 'name' ? (value as String?) ?? '' : current.name),
      offbudget: Value(
          column == 'offbudget' ? (value as int?) ?? 0 : current.offbudget),
      closed:
          Value(column == 'closed' ? (value as int?) ?? 0 : current.closed),
      sortOrder: Value(column == 'sort_order'
          ? (value as int?)?.toDouble()
          : current.sortOrder),
      tombstone: Value(
          column == 'tombstone' ? (value as int?) ?? 0 : current.tombstone),
    ));
  }

  // ========== Payee Operations ==========

  Future<void> _ensurePayeeExists(String id) async {
    final existing = await db.getPayeeById(id);
    if (existing == null) {
      await db.insertPayee(PayeesCompanion(
        id: Value(id),
        name: const Value(''),
        category: const Value(null),
        tombstone: const Value(0),
      ));
    }
  }

  Future<void> _applyPayeeColumn(
      String id, String column, Object? value) async {
    final current = await db.getPayeeById(id);
    if (current == null) return;

    await db.insertPayee(PayeesCompanion(
      id: Value(id),
      name: Value(column == 'name' ? (value as String?) ?? '' : current.name),
      category: Value(
          column == 'category' ? value as String? : current.category),
      tombstone: Value(
          column == 'tombstone' ? (value as int?) ?? 0 : current.tombstone),
    ));
  }

  // ========== Category Operations ==========

  Future<void> _ensureCategoryExists(String id) async {
    final existing = await db.getCategoryById(id);
    if (existing == null) {
      await db.insertCategory(CategoriesCompanion(
        id: Value(id),
        name: const Value(''),
        catGroup: const Value(null),
        isIncome: const Value(0),
        sortOrder: const Value(null),
        hidden: const Value(0),
        tombstone: const Value(0),
      ));
    }
  }

  Future<void> _applyCategoryColumn(
      String id, String column, Object? value) async {
    final current = await db.getCategoryById(id);
    if (current == null) return;

    await db.insertCategory(CategoriesCompanion(
      id: Value(id),
      name: Value(column == 'name' ? (value as String?) ?? '' : current.name),
      catGroup: Value(
          column == 'cat_group' ? value as String? : current.catGroup),
      isIncome: Value(
          column == 'is_income' ? (value as int?) ?? 0 : current.isIncome),
      sortOrder: Value(column == 'sort_order'
          ? (value as int?)?.toDouble()
          : current.sortOrder),
      hidden:
          Value(column == 'hidden' ? (value as int?) ?? 0 : current.hidden),
      tombstone: Value(
          column == 'tombstone' ? (value as int?) ?? 0 : current.tombstone),
    ));
  }

  // ========== Category Group Operations ==========

  Future<void> _ensureCategoryGroupExists(String id) async {
    final existing = await db.getCategoryGroupById(id);
    if (existing == null) {
      await db.insertCategoryGroup(CategoryGroupsCompanion(
        id: Value(id),
        name: const Value(''),
        isIncome: const Value(0),
        sortOrder: const Value(null),
        hidden: const Value(0),
        tombstone: const Value(0),
      ));
    }
  }

  Future<void> _applyCategoryGroupColumn(
      String id, String column, Object? value) async {
    final current = await db.getCategoryGroupById(id);
    if (current == null) return;

    await db.insertCategoryGroup(CategoryGroupsCompanion(
      id: Value(id),
      name: Value(column == 'name' ? (value as String?) ?? '' : current.name),
      isIncome: Value(
          column == 'is_income' ? (value as int?) ?? 0 : current.isIncome),
      sortOrder: Value(column == 'sort_order'
          ? (value as int?)?.toDouble()
          : current.sortOrder),
      hidden:
          Value(column == 'hidden' ? (value as int?) ?? 0 : current.hidden),
      tombstone: Value(
          column == 'tombstone' ? (value as int?) ?? 0 : current.tombstone),
    ));
  }

  // ========== Transaction Operations ==========

  Future<void> _ensureTransactionExists(String id) async {
    final existing = await db.getTransactionById(id);
    if (existing == null) {
      await db.insertTransaction(TransactionsCompanion(
        id: Value(id),
        acct: const Value(null),
        category: const Value(null),
        amount: const Value(0),
        description: const Value(null),
        notes: const Value(null),
        date: const Value(null),
        sortOrder: const Value(null),
        tombstone: const Value(0),
        cleared: const Value(1),
      ));
    }
  }

  Future<void> _applyTransactionColumn(
      String id, String column, Object? value) async {
    final current = await db.getTransactionById(id);
    if (current == null) return;

    // Build updated transaction
    // Note: 'description' column stores the payee ID
    final acct = (column == 'acct' || column == 'account')
        ? value as String?
        : current.acct;
    final category =
        column == 'category' ? value as String? : current.category;
    final amount = column == 'amount' ? (value as int?) : current.amount;
    final description = (column == 'description' || column == 'payee')
        ? value as String?
        : current.description;
    final notes = column == 'notes' ? value as String? : current.notes;
    final date = column == 'date' ? (value as int?) : current.date;
    final cleared = column == 'cleared' ? (value as int?) : current.cleared;
    final sortOrder = column == 'sort_order'
        ? (value as int?)?.toDouble()
        : current.sortOrder;
    final tombstone =
        column == 'tombstone' ? (value as int?) : current.tombstone;

    await db.insertTransaction(TransactionsCompanion(
      id: Value(id),
      acct: Value(acct),
      category: Value(category),
      amount: Value(amount ?? 0),
      description: Value(description),
      notes: Value(notes),
      date: Value(date),
      sortOrder: Value(sortOrder),
      tombstone: Value(tombstone ?? 0),
      cleared: Value(cleared ?? 1),
    ));
  }

  // ========== Query Methods ==========

  Future<List<Account>> getAccounts() => db.getAccounts();

  Future<List<Payee>> getPayees() => db.getPayees();

  Future<List<Category>> getCategories() => db.getCategories();

  Future<List<CategoryGroup>> getCategoryGroups() => db.getCategoryGroups();

  Future<List<Transaction>> getTransactionsByAccount(String accountId) =>
      db.getTransactionsByAccount(accountId);

  Future<List<Transaction>> getTransactionsByDateRange(
          int startDate, int endDate) =>
      db.getTransactionsByDateRange(startDate, endDate);

  Future<String?> getLastSyncTimestamp() => db.getLastTimestamp();

  Future<void> setSyncMetadata(String key, String value) =>
      db.setSyncMetadataValue(key, value);

  Future<String?> getSyncMetadata(String key) =>
      db.getSyncMetadataValue(key);

  /// Clear all data from the database.
  Future<void> clearAll() => db.clearAllData();
}
