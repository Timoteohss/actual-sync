/// Account model.
class Account {
  final String id;
  final String name;
  final String? type;
  final bool offBudget;
  final bool closed;
  final int balance;

  Account({
    required this.id,
    required this.name,
    this.type,
    this.offBudget = false,
    this.closed = false,
    this.balance = 0,
  });

  factory Account.fromJson(Map<String, dynamic> json) {
    return Account(
      id: json['id'] as String,
      name: json['name'] as String,
      type: json['type'] as String?,
      offBudget: json['offbudget'] == true || json['offbudget'] == 1,
      closed: json['closed'] == true || json['closed'] == 1,
      balance: json['balance'] as int? ?? 0,
    );
  }
}

/// Transaction model.
class Transaction {
  final String id;
  final String accountId;
  final String date;
  final int amount;
  final String? payeeId;
  final String? payeeName;
  final String? categoryId;
  final String? notes;
  final bool cleared;
  final bool reconciled;

  Transaction({
    required this.id,
    required this.accountId,
    required this.date,
    required this.amount,
    this.payeeId,
    this.payeeName,
    this.categoryId,
    this.notes,
    this.cleared = false,
    this.reconciled = false,
  });

  factory Transaction.fromJson(Map<String, dynamic> json) {
    return Transaction(
      id: json['id'] as String,
      accountId: json['account'] as String,
      date: json['date'] as String,
      amount: json['amount'] as int,
      payeeId: json['payee'] as String?,
      payeeName: json['payee_name'] as String?,
      categoryId: json['category'] as String?,
      notes: json['notes'] as String?,
      cleared: json['cleared'] == true || json['cleared'] == 1,
      reconciled: json['reconciled'] == true || json['reconciled'] == 1,
    );
  }
}

/// New transaction to create.
class NewTransaction {
  final String accountId;
  final String date;
  final int amount;
  final String? payeeName;
  final String? payeeId;
  final String? categoryId;
  final String? notes;
  final bool cleared;

  NewTransaction({
    required this.accountId,
    required this.date,
    required this.amount,
    this.payeeName,
    this.payeeId,
    this.categoryId,
    this.notes,
    this.cleared = true,
  });

  Map<String, dynamic> toJson() {
    return {
      'account': accountId,
      'date': date,
      'amount': amount,
      if (payeeName != null) 'payee_name': payeeName,
      if (payeeId != null) 'payee': payeeId,
      if (categoryId != null) 'category': categoryId,
      if (notes != null) 'notes': notes,
      'cleared': cleared,
    };
  }
}

/// Category model.
class Category {
  final String id;
  final String name;
  final String groupId;
  final bool isIncome;
  final bool hidden;

  Category({
    required this.id,
    required this.name,
    required this.groupId,
    this.isIncome = false,
    this.hidden = false,
  });

  factory Category.fromJson(Map<String, dynamic> json) {
    return Category(
      id: json['id'] as String,
      name: json['name'] as String,
      groupId: json['cat_group'] as String,
      isIncome: json['is_income'] == true || json['is_income'] == 1,
      hidden: json['hidden'] == true || json['hidden'] == 1,
    );
  }
}

/// Category group model.
class CategoryGroup {
  final String id;
  final String name;
  final bool isIncome;
  final bool hidden;
  final List<Category> categories;

  CategoryGroup({
    required this.id,
    required this.name,
    this.isIncome = false,
    this.hidden = false,
    this.categories = const [],
  });

  factory CategoryGroup.fromJson(Map<String, dynamic> json) {
    return CategoryGroup(
      id: json['id'] as String,
      name: json['name'] as String,
      isIncome: json['is_income'] == true || json['is_income'] == 1,
      hidden: json['hidden'] == true || json['hidden'] == 1,
      categories: (json['categories'] as List<dynamic>?)
              ?.map((c) => Category.fromJson(c as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }
}

/// Payee model.
class Payee {
  final String id;
  final String name;

  Payee({
    required this.id,
    required this.name,
  });

  factory Payee.fromJson(Map<String, dynamic> json) {
    return Payee(
      id: json['id'] as String,
      name: json['name'] as String,
    );
  }
}

/// Budget month model.
class BudgetMonth {
  final String month;
  final List<BudgetCategory> categories;

  BudgetMonth({
    required this.month,
    required this.categories,
  });
}

/// Budget category model.
class BudgetCategory {
  final String categoryId;
  final int budgeted;
  final int spent;
  final int balance;
  final bool carryover;

  BudgetCategory({
    required this.categoryId,
    required this.budgeted,
    required this.spent,
    required this.balance,
    this.carryover = false,
  });
}
