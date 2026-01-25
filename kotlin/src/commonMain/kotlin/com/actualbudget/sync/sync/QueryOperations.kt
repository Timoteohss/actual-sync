package com.actualbudget.sync.sync

import com.actualbudget.sync.db.ActualDatabase

/**
 * Read-only query operations for the Actual Budget database.
 *
 * All methods are annotated with @Throws for Swift interop - exceptions
 * propagate to Swift as NSError.
 *
 * This class contains NO mutations - it only reads data.
 */
class QueryOperations(
    private val database: ActualDatabase
) {
    // ========== Account Queries ==========

    /**
     * Get all non-tombstoned accounts ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getAccounts() = database.actualDatabaseQueries.getAccounts().executeAsList()

    /**
     * Get account by ID (includes tombstoned).
     */
    @Throws(Exception::class)
    fun getAccountById(id: String) =
        database.actualDatabaseQueries.getAccountById(id).executeAsOneOrNull()

    /**
     * Get the balance for a specific account.
     * Performs SUM aggregation in SQL.
     */
    @Throws(Exception::class)
    fun getAccountBalance(accountId: String): Long =
        database.actualDatabaseQueries.getAccountBalance(accountId).executeAsOne().toLong()

    /**
     * Get on-budget accounts ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getOnBudgetAccountsOrdered() =
        database.actualDatabaseQueries.getOnBudgetAccountsOrdered().executeAsList()

    /**
     * Get off-budget accounts ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getOffBudgetAccountsOrdered() =
        database.actualDatabaseQueries.getOffBudgetAccountsOrdered().executeAsList()

    /**
     * Get closed accounts ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getClosedAccountsOrdered() =
        database.actualDatabaseQueries.getClosedAccountsOrdered().executeAsList()

    // ========== Payee Queries ==========

    /**
     * Get all non-tombstoned payees ordered by name.
     */
    @Throws(Exception::class)
    fun getPayees() = database.actualDatabaseQueries.getPayees().executeAsList()

    /**
     * Get payee by ID (includes tombstoned).
     */
    @Throws(Exception::class)
    fun getPayeeById(id: String) =
        database.actualDatabaseQueries.getPayeeById(id).executeAsOneOrNull()

    /**
     * Get transfer payee for a specific account.
     */
    @Throws(Exception::class)
    fun getTransferPayeeForAccount(accountId: String) =
        database.actualDatabaseQueries.getTransferPayeeForAccount(accountId).executeAsOneOrNull()

    /**
     * Get all transfer payees (payees with transfer_acct set).
     */
    @Throws(Exception::class)
    fun getTransferPayees() =
        database.actualDatabaseQueries.getPayeesWithTransferAcct().executeAsList()

    // ========== Category Queries ==========

    /**
     * Get all non-tombstoned categories ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getCategories() = database.actualDatabaseQueries.getCategories().executeAsList()

    /**
     * Get category by ID (includes tombstoned).
     */
    @Throws(Exception::class)
    fun getCategoryById(id: String) =
        database.actualDatabaseQueries.getCategoryById(id).executeAsOneOrNull()

    /**
     * Get categories in a group ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getCategoriesInGroupOrdered(groupId: String) =
        database.actualDatabaseQueries.getCategoriesInGroupOrdered(groupId).executeAsList()

    /**
     * Get categories with their group info pre-joined.
     * Only returns visible (non-hidden, non-tombstoned) categories and groups.
     */
    @Throws(Exception::class)
    fun getCategoriesWithGroups() =
        database.actualDatabaseQueries.getCategoriesWithGroups().executeAsList()

    // ========== Category Group Queries ==========

    /**
     * Get all non-tombstoned category groups ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getCategoryGroups() = database.actualDatabaseQueries.getCategoryGroups().executeAsList()

    /**
     * Get category group by ID (includes tombstoned).
     */
    @Throws(Exception::class)
    fun getCategoryGroupById(id: String) =
        database.actualDatabaseQueries.getCategoryGroupById(id).executeAsOneOrNull()

    /**
     * Get all category groups ordered by sort_order.
     */
    @Throws(Exception::class)
    fun getCategoryGroupsOrdered() =
        database.actualDatabaseQueries.getCategoryGroupsOrdered().executeAsList()

    // ========== Transaction Queries ==========

    /**
     * Get transactions by account (excludes parents to avoid double-counting).
     */
    @Throws(Exception::class)
    fun getTransactionsByAccount(accountId: String) =
        database.actualDatabaseQueries.getTransactionsByAccount(accountId).executeAsList()

    /**
     * Get transaction by ID (includes tombstoned).
     */
    @Throws(Exception::class)
    fun getTransactionById(id: String) =
        database.actualDatabaseQueries.getTransactionById(id).executeAsOneOrNull()

    /**
     * Get transactions with payee and category names pre-joined.
     * Optimized for list views - eliminates N dictionary lookups in the UI layer.
     */
    @Throws(Exception::class)
    fun getTransactionsWithDetails(accountId: String) =
        database.actualDatabaseQueries.getTransactionsWithDetailsForAccount(accountId).executeAsList()

    /**
     * Get all transactions for display including parents (for accordion grouping).
     */
    @Throws(Exception::class)
    fun getTransactionsForDisplay(accountId: String) =
        database.actualDatabaseQueries.getTransactionsForDisplayByAccount(accountId).executeAsList()

    /**
     * Get transactions with details, paginated for large accounts.
     */
    @Throws(Exception::class)
    fun getTransactionsWithDetailsPaginated(accountId: String, limit: Long, offset: Long) =
        database.actualDatabaseQueries.getTransactionsWithDetailsPaginated(accountId, limit, offset).executeAsList()

    /**
     * Search transactions with text matching on payee name, notes, and amount.
     */
    @Throws(Exception::class)
    fun searchTransactions(accountId: String, searchQuery: String) =
        database.actualDatabaseQueries.searchTransactionsForAccount(
            accountId, "%$searchQuery%", "%$searchQuery%", "%$searchQuery%"
        ).executeAsList()

    /**
     * Get transactions by date range.
     */
    @Throws(Exception::class)
    fun getTransactionsByDateRange(startDate: Long, endDate: Long) =
        database.actualDatabaseQueries.getTransactionsByDateRange(startDate, endDate).executeAsList()

    // ========== Split Transaction Queries ==========

    /**
     * Get child transactions for a parent (split) transaction.
     */
    @Throws(Exception::class)
    fun getChildTransactions(parentId: String) =
        database.actualDatabaseQueries.getChildTransactions(parentId).executeAsList()

    /**
     * Get child transactions with payee and category names pre-joined.
     */
    @Throws(Exception::class)
    fun getChildTransactionsWithDetails(parentId: String) =
        database.actualDatabaseQueries.getChildTransactionsWithDetails(parentId).executeAsList()

    /**
     * Check if a transaction has child transactions (is a split parent).
     */
    @Throws(Exception::class)
    fun hasChildTransactions(transactionId: String): Boolean =
        database.actualDatabaseQueries.hasChildTransactions(transactionId).executeAsOne() > 0

    /**
     * Check if a transaction is a transfer (has transferred_id set).
     */
    @Throws(Exception::class)
    fun isTransferTransaction(transactionId: String): Boolean {
        val result = database.actualDatabaseQueries.isTransferTransaction(transactionId).executeAsOneOrNull()
        return result == 1L
    }

    /**
     * Get the linked (paired) transaction for a transfer.
     */
    @Throws(Exception::class)
    fun getLinkedTransaction(transactionId: String) =
        database.actualDatabaseQueries.getLinkedTransaction(transactionId).executeAsOneOrNull()

    // ========== Reconciliation Queries ==========

    /**
     * Get the cleared balance for an account (only cleared transactions).
     */
    @Throws(Exception::class)
    fun getClearedBalance(accountId: String): Long =
        database.actualDatabaseQueries.getClearedBalance(accountId).executeAsOne().toLong()

    /**
     * Get uncleared transactions for an account.
     */
    @Throws(Exception::class)
    fun getUnclearedTransactions(accountId: String) =
        database.actualDatabaseQueries.getUnclearedTransactions(accountId).executeAsList()

    /**
     * Get the count of uncleared transactions for an account.
     */
    @Throws(Exception::class)
    fun getUnclearedCount(accountId: String): Long =
        database.actualDatabaseQueries.getUnclearedCount(accountId).executeAsOne()

    /**
     * Get reconciled (locked) transactions for an account.
     */
    @Throws(Exception::class)
    fun getReconciledTransactions(accountId: String) =
        database.actualDatabaseQueries.getReconciledTransactions(accountId).executeAsList()

    /**
     * Get pending transactions for an account.
     */
    @Throws(Exception::class)
    fun getPendingTransactions(accountId: String) =
        database.actualDatabaseQueries.getPendingTransactions(accountId).executeAsList()

    /**
     * Get reconciliation summary for an account.
     * Returns total balance, cleared balance, uncleared balance, and uncleared count.
     */
    @Throws(Exception::class)
    fun getReconciliationSummary(accountId: String) =
        database.actualDatabaseQueries.getReconciliationSummary(accountId).executeAsOne()

    /**
     * Get transactions that are cleared but not yet reconciled.
     */
    @Throws(Exception::class)
    fun getClearedUnreconciledTransactions(accountId: String) =
        database.actualDatabaseQueries.getClearedUnreconciledTransactions(accountId).executeAsList()

    /**
     * Get the count of cleared but not yet reconciled transactions.
     */
    @Throws(Exception::class)
    fun getClearedUnreconciledCount(accountId: String): Long =
        database.actualDatabaseQueries.getClearedUnreconciledCount(accountId).executeAsOne()

    // ========== Budget Queries ==========

    /**
     * Get budget entries for a month.
     */
    @Throws(Exception::class)
    fun getBudgetForMonth(month: Long) =
        database.actualDatabaseQueries.getBudgetForMonth(month).executeAsList()

    /**
     * Get budget entries for a specific category.
     */
    @Throws(Exception::class)
    fun getBudgetForCategory(category: String) =
        database.actualDatabaseQueries.getBudgetForCategory(category).executeAsList()

    /**
     * Get a specific budget entry by ID.
     */
    @Throws(Exception::class)
    fun getBudgetById(budgetId: String) =
        database.actualDatabaseQueries.getBudgetById(budgetId).executeAsOneOrNull()

    /**
     * Get spending totals by category for a date range (on-budget accounts only).
     */
    @Throws(Exception::class)
    fun getSpentByCategory(startDate: Long, endDate: Long) =
        database.actualDatabaseQueries.getSpentByCategory(startDate, endDate).executeAsList()

    /**
     * Get spending by category for N previous months (for average calculation).
     */
    @Throws(Exception::class)
    fun getSpentByCategoryForMonths(startDate: Long, endDate: Long) =
        database.actualDatabaseQueries.getSpentByCategoryForMonths(startDate, endDate).executeAsList()

    /**
     * Get total income for a month.
     */
    @Throws(Exception::class)
    fun getTotalIncomeForMonth(month: Long): Long =
        database.actualDatabaseQueries.getTotalIncomeForMonth(month).executeAsOne().toLong()

    /**
     * Get total budgeted for a month.
     */
    @Throws(Exception::class)
    fun getTotalBudgetedForMonth(month: Long): Long =
        database.actualDatabaseQueries.getTotalBudgetedForMonth(month).executeAsOne().toLong()

    /**
     * Get budget month data (for hold operations).
     */
    @Throws(Exception::class)
    fun getBudgetMonth(monthId: String) =
        database.actualDatabaseQueries.getBudgetMonth(monthId).executeAsOneOrNull()

    // ========== Sync Metadata Queries ==========

    /**
     * Get sync metadata value by key.
     */
    @Throws(Exception::class)
    fun getSyncMetadata(key: String) =
        database.actualDatabaseQueries.getSyncMetadata(key).executeAsOneOrNull()

    // ========== Diagnostic Queries ==========

    /**
     * Count all accounts (including tombstoned).
     */
    @Throws(Exception::class)
    fun countAllAccounts(): Long =
        database.actualDatabaseQueries.countAllAccounts().executeAsOne()

    /**
     * Count all payees (including tombstoned).
     */
    @Throws(Exception::class)
    fun countAllPayees(): Long =
        database.actualDatabaseQueries.countAllPayees().executeAsOne()

    /**
     * Count all categories (including tombstoned).
     */
    @Throws(Exception::class)
    fun countAllCategories(): Long =
        database.actualDatabaseQueries.countAllCategories().executeAsOne()

    /**
     * Count all category groups (including tombstoned).
     */
    @Throws(Exception::class)
    fun countAllCategoryGroups(): Long =
        database.actualDatabaseQueries.countAllCategoryGroups().executeAsOne()

    /**
     * Count all transactions (including tombstoned).
     */
    @Throws(Exception::class)
    fun countAllTransactions(): Long =
        database.actualDatabaseQueries.countAllTransactions().executeAsOne()
}
