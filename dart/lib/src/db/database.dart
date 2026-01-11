import 'dart:io';
import 'dart:typed_data';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';

part 'database.g.dart';

/// Accounts table
class Accounts extends Table {
  TextColumn get id => text()();
  TextColumn get name => text().nullable()();
  IntColumn get offbudget => integer().withDefault(const Constant(0))();
  IntColumn get closed => integer().withDefault(const Constant(0))();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();
  RealColumn get sortOrder => real().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

/// Payees table
class Payees extends Table {
  TextColumn get id => text()();
  TextColumn get name => text().nullable()();
  TextColumn get category => text().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Payee mapping rules
class PayeeMapping extends Table {
  TextColumn get id => text()();
  TextColumn get targetId => text()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Category groups table
class CategoryGroups extends Table {
  TextColumn get id => text()();
  TextColumn get name => text().nullable()();
  IntColumn get isIncome => integer().withDefault(const Constant(0))();
  RealColumn get sortOrder => real().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();
  IntColumn get hidden => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Categories table
class Categories extends Table {
  TextColumn get id => text()();
  TextColumn get name => text().nullable()();
  IntColumn get isIncome => integer().withDefault(const Constant(0))();
  TextColumn get catGroup => text().nullable()();
  RealColumn get sortOrder => real().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();
  IntColumn get hidden => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Transactions table
/// Note: 'description' column stores the payee ID (not the payee name)
class Transactions extends Table {
  TextColumn get id => text()();
  TextColumn get acct => text().nullable()();
  TextColumn get category => text().nullable()();
  IntColumn get amount => integer().nullable()();
  TextColumn get description => text().nullable()();
  TextColumn get notes => text().nullable()();
  IntColumn get date => integer().nullable()();
  RealColumn get sortOrder => real().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();
  IntColumn get cleared => integer().withDefault(const Constant(1))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Monthly budget amounts
class ZeroBudgets extends Table {
  TextColumn get id => text()();
  IntColumn get month => integer()();
  TextColumn get category => text()();
  IntColumn get amount => integer().withDefault(const Constant(0))();
  IntColumn get carryover => integer().withDefault(const Constant(0))();
  IntColumn get goal => integer().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Notes table
class Notes extends Table {
  TextColumn get id => text()();
  TextColumn get note => text().withDefault(const Constant(''))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Schedules for recurring transactions
class Schedules extends Table {
  TextColumn get id => text()();
  TextColumn get rule => text().nullable()();
  IntColumn get active => integer().withDefault(const Constant(1))();
  IntColumn get completed => integer().withDefault(const Constant(0))();
  IntColumn get postsTransaction =>
      integer().withDefault(const Constant(0))();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();
  TextColumn get name => text().nullable()();

  @override
  Set<Column> get primaryKey => {id};
}

/// Schedule-transaction link
class ScheduleNextDate extends Table {
  TextColumn get id => text()();
  TextColumn get scheduleId => text()();
  IntColumn get localNextDate => integer().nullable()();
  IntColumn get localNextDateTs => integer().nullable()();
  IntColumn get baseNextDate => integer().nullable()();
  IntColumn get baseNextDateTs => integer().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// Rules for auto-categorization
class Rules extends Table {
  TextColumn get id => text()();
  TextColumn get stage => text().nullable()();
  TextColumn get conditions => text().nullable()();
  TextColumn get actions => text().nullable()();
  IntColumn get tombstone => integer().withDefault(const Constant(0))();

  @override
  Set<Column> get primaryKey => {id};
}

/// CRDT messages log (for sync)
class MessagesCrdt extends Table {
  IntColumn get id => integer().autoIncrement()();
  TextColumn get timestamp => text().unique()();
  TextColumn get dataset => text()();
  TextColumn get row => text()();
  TextColumn get column => text()();
  BlobColumn get value => blob()();

  @override
  String get tableName => 'messages_crdt';
}

/// Sync metadata
class SyncMetadata extends Table {
  TextColumn get key => text()();
  TextColumn get value => text().nullable()();

  @override
  Set<Column> get primaryKey => {key};

  @override
  String get tableName => 'sync_metadata';
}

@DriftDatabase(tables: [
  Accounts,
  Payees,
  PayeeMapping,
  CategoryGroups,
  Categories,
  Transactions,
  ZeroBudgets,
  Notes,
  Schedules,
  ScheduleNextDate,
  Rules,
  MessagesCrdt,
  SyncMetadata,
])
class ActualDatabase extends _$ActualDatabase {
  ActualDatabase(super.e);

  /// Create a database from a file path
  factory ActualDatabase.fromFile(String path) {
    return ActualDatabase(_openConnection(path));
  }

  /// Create an in-memory database (useful for testing)
  factory ActualDatabase.inMemory() {
    return ActualDatabase(NativeDatabase.memory());
  }

  @override
  int get schemaVersion => 1;

  // ============= Account Queries =============

  Future<List<Account>> getAccounts() {
    return (select(accounts)
          ..where((t) => t.tombstone.equals(0))
          ..orderBy([(t) => OrderingTerm.asc(t.sortOrder)]))
        .get();
  }

  Future<Account?> getAccountById(String id) {
    return (select(accounts)..where((t) => t.id.equals(id))).getSingleOrNull();
  }

  Future<void> insertAccount(AccountsCompanion account) {
    return into(accounts).insertOnConflictUpdate(account);
  }

  // ============= Payee Queries =============

  Future<List<Payee>> getPayees() {
    return (select(payees)
          ..where((t) => t.tombstone.equals(0))
          ..orderBy([(t) => OrderingTerm.asc(t.name)]))
        .get();
  }

  Future<Payee?> getPayeeById(String id) {
    return (select(payees)..where((t) => t.id.equals(id))).getSingleOrNull();
  }

  Future<void> insertPayee(PayeesCompanion payee) {
    return into(payees).insertOnConflictUpdate(payee);
  }

  // ============= Category Group Queries =============

  Future<List<CategoryGroup>> getCategoryGroups() {
    return (select(categoryGroups)
          ..where((t) => t.tombstone.equals(0))
          ..orderBy([(t) => OrderingTerm.asc(t.sortOrder)]))
        .get();
  }

  Future<CategoryGroup?> getCategoryGroupById(String id) {
    return (select(categoryGroups)..where((t) => t.id.equals(id)))
        .getSingleOrNull();
  }

  Future<void> insertCategoryGroup(CategoryGroupsCompanion group) {
    return into(categoryGroups).insertOnConflictUpdate(group);
  }

  // ============= Category Queries =============

  Future<List<Category>> getCategories() {
    return (select(categories)
          ..where((t) => t.tombstone.equals(0))
          ..orderBy([(t) => OrderingTerm.asc(t.sortOrder)]))
        .get();
  }

  Future<List<Category>> getCategoriesByGroup(String groupId) {
    return (select(categories)
          ..where(
              (t) => t.catGroup.equals(groupId) & t.tombstone.equals(0))
          ..orderBy([(t) => OrderingTerm.asc(t.sortOrder)]))
        .get();
  }

  Future<Category?> getCategoryById(String id) {
    return (select(categories)..where((t) => t.id.equals(id)))
        .getSingleOrNull();
  }

  Future<void> insertCategory(CategoriesCompanion category) {
    return into(categories).insertOnConflictUpdate(category);
  }

  // ============= Transaction Queries =============

  Future<List<Transaction>> getTransactionsByAccount(String accountId) {
    return (select(transactions)
          ..where((t) => t.acct.equals(accountId) & t.tombstone.equals(0))
          ..orderBy([
            (t) => OrderingTerm.desc(t.date),
            (t) => OrderingTerm.desc(t.sortOrder)
          ]))
        .get();
  }

  Future<List<Transaction>> getTransactionsByDateRange(
      int startDate, int endDate) {
    return (select(transactions)
          ..where((t) =>
              t.date.isBiggerOrEqualValue(startDate) &
              t.date.isSmallerOrEqualValue(endDate) &
              t.tombstone.equals(0))
          ..orderBy([
            (t) => OrderingTerm.desc(t.date),
            (t) => OrderingTerm.desc(t.sortOrder)
          ]))
        .get();
  }

  Future<Transaction?> getTransactionById(String id) {
    return (select(transactions)..where((t) => t.id.equals(id)))
        .getSingleOrNull();
  }

  Future<void> insertTransaction(TransactionsCompanion transaction) {
    return into(transactions).insertOnConflictUpdate(transaction);
  }

  // ============= Budget Queries =============

  Future<List<ZeroBudget>> getBudgetForMonth(int month) {
    return (select(zeroBudgets)
          ..where((t) => t.month.equals(month) & t.tombstone.equals(0)))
        .get();
  }

  Future<void> insertBudget(ZeroBudgetsCompanion budget) {
    return into(zeroBudgets).insertOnConflictUpdate(budget);
  }

  // ============= CRDT Message Queries =============

  Future<void> insertMessage({
    required String timestamp,
    required String dataset,
    required String row,
    required String column,
    required Uint8List value,
  }) {
    return into(messagesCrdt).insert(
      MessagesCrdtCompanion.insert(
        timestamp: timestamp,
        dataset: dataset,
        row: row,
        column: column,
        value: value,
      ),
      mode: InsertMode.insertOrIgnore,
    );
  }

  Future<List<MessagesCrdtData>> getMessagesSince(String timestamp) {
    return (select(messagesCrdt)
          ..where((t) => t.timestamp.isBiggerThanValue(timestamp))
          ..orderBy([(t) => OrderingTerm.asc(t.timestamp)]))
        .get();
  }

  Future<bool> messageExists(String timestamp) async {
    final count = await (selectOnly(messagesCrdt)
          ..addColumns([messagesCrdt.id.count()])
          ..where(messagesCrdt.timestamp.equals(timestamp)))
        .map((row) => row.read(messagesCrdt.id.count()))
        .getSingle();
    return (count ?? 0) > 0;
  }

  Future<String?> getLastTimestamp() async {
    final result = await (selectOnly(messagesCrdt)
          ..addColumns([messagesCrdt.timestamp])
          ..orderBy([OrderingTerm.desc(messagesCrdt.timestamp)])
          ..limit(1))
        .map((row) => row.read(messagesCrdt.timestamp))
        .getSingleOrNull();
    return result;
  }

  // ============= Sync Metadata Queries =============

  Future<String?> getSyncMetadataValue(String key) async {
    final result = await (select(syncMetadata)
          ..where((t) => t.key.equals(key)))
        .getSingleOrNull();
    return result?.value;
  }

  Future<void> setSyncMetadataValue(String key, String? value) {
    return into(syncMetadata).insertOnConflictUpdate(
      SyncMetadataCompanion(
        key: Value(key),
        value: Value(value),
      ),
    );
  }

  // ============= Clear Data =============

  Future<void> clearAllData() async {
    await delete(messagesCrdt).go();
    await delete(transactions).go();
    await delete(categories).go();
    await delete(categoryGroups).go();
    await delete(payees).go();
    await delete(accounts).go();
    await delete(zeroBudgets).go();
    await delete(notes).go();
    await delete(schedules).go();
    await delete(rules).go();
    await delete(syncMetadata).go();
  }

  // ============= Diagnostic Queries =============

  Future<int> countAccounts() async {
    final count = await (selectOnly(accounts)
          ..addColumns([accounts.id.count()]))
        .map((row) => row.read(accounts.id.count()))
        .getSingle();
    return count ?? 0;
  }

  Future<int> countPayees() async {
    final count =
        await (selectOnly(payees)..addColumns([payees.id.count()]))
            .map((row) => row.read(payees.id.count()))
            .getSingle();
    return count ?? 0;
  }

  Future<int> countCategories() async {
    final count = await (selectOnly(categories)
          ..addColumns([categories.id.count()]))
        .map((row) => row.read(categories.id.count()))
        .getSingle();
    return count ?? 0;
  }

  Future<int> countTransactions() async {
    final count = await (selectOnly(transactions)
          ..addColumns([transactions.id.count()]))
        .map((row) => row.read(transactions.id.count()))
        .getSingle();
    return count ?? 0;
  }
}

/// Open a database connection to a file
LazyDatabase _openConnection(String path) {
  return LazyDatabase(() async {
    final file = File(path);
    return NativeDatabase.createInBackground(file);
  });
}
