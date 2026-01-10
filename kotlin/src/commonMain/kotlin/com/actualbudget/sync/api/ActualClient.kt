package com.actualbudget.sync.api

import com.actualbudget.sync.crdt.*
import com.actualbudget.sync.sync.*

/**
 * High-level API client for Actual Budget.
 *
 * This is the main entry point for interacting with Actual Budget
 * from native applications.
 */
class ActualClient(
    private val serverUrl: String,
    private val dataDir: String
) {
    private val syncClient = SyncClient(serverUrl)
    private var clock: SyncClock? = null
    private var initialized = false

    /**
     * Initialize the client and load local state.
     */
    suspend fun init() {
        // TODO: Load clock state from local storage
        val nodeId = Timestamp.makeClientId()
        clock = ClockManager.makeClock(Timestamp.ZERO.copy(node = nodeId))
        ClockManager.setClock(clock!!)
        initialized = true
    }

    /**
     * Authenticate with the server.
     */
    suspend fun login(password: String) {
        syncClient.login(password)
    }

    /**
     * List available budgets.
     */
    suspend fun getBudgets(): List<BudgetFile> {
        return syncClient.listFiles()
    }

    /**
     * Download and open a budget.
     */
    suspend fun downloadBudget(syncId: String, password: String? = null) {
        syncClient.downloadBudget(syncId)
        // TODO: Save to local storage
        // TODO: Decrypt if password provided
        // TODO: Initialize local database
    }

    /**
     * Sync local changes with server.
     */
    suspend fun sync(): Result<Unit> {
        checkInitialized()
        // TODO: Implement full sync flow
        // 1. Get local messages since last sync
        // 2. Compare merkle tries to find sync point
        // 3. Exchange messages with server
        // 4. Apply received messages locally
        return Result.success(Unit)
    }

    /**
     * Get all accounts.
     */
    suspend fun getAccounts(): Result<List<Account>> {
        checkInitialized()
        // TODO: Query local database
        return Result.success(emptyList())
    }

    /**
     * Get transactions for an account.
     */
    suspend fun getTransactions(
        accountId: String,
        startDate: String,
        endDate: String
    ): Result<List<Transaction>> {
        checkInitialized()
        // TODO: Query local database
        return Result.success(emptyList())
    }

    /**
     * Create a new transaction.
     */
    suspend fun createTransaction(transaction: NewTransaction): Result<String> {
        checkInitialized()
        val ts = clock!!.send()
        // TODO: Create CRDT message and apply locally
        return Result.success(ts.toString())
    }

    /**
     * Get all categories.
     */
    suspend fun getCategories(): Result<List<Category>> {
        checkInitialized()
        // TODO: Query local database
        return Result.success(emptyList())
    }

    /**
     * Get budget for a specific month.
     */
    suspend fun getBudgetMonth(month: String): Result<BudgetMonth> {
        checkInitialized()
        // TODO: Query local database
        return Result.failure(NotImplementedError())
    }

    /**
     * Shutdown the client and save state.
     */
    suspend fun shutdown() {
        // TODO: Save clock state to local storage
        initialized = false
    }

    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException("Client not initialized. Call init() first.")
        }
    }
}

// Data models

data class Account(
    val id: String,
    val name: String,
    val type: String?,
    val offBudget: Boolean = false,
    val closed: Boolean = false,
    val balance: Long = 0
)

data class Transaction(
    val id: String,
    val accountId: String,
    val date: String,
    val amount: Long,
    val payeeId: String?,
    val payeeName: String?,
    val categoryId: String?,
    val notes: String?,
    val cleared: Boolean = false,
    val reconciled: Boolean = false
)

data class NewTransaction(
    val accountId: String,
    val date: String,
    val amount: Long,
    val payeeName: String? = null,
    val payeeId: String? = null,
    val categoryId: String? = null,
    val notes: String? = null,
    val cleared: Boolean = true
)

data class Category(
    val id: String,
    val name: String,
    val groupId: String,
    val isIncome: Boolean = false,
    val hidden: Boolean = false
)

data class CategoryGroup(
    val id: String,
    val name: String,
    val isIncome: Boolean = false,
    val hidden: Boolean = false,
    val categories: List<Category> = emptyList()
)

data class BudgetMonth(
    val month: String,
    val categories: List<BudgetCategory>
)

data class BudgetCategory(
    val categoryId: String,
    val budgeted: Long,
    val spent: Long,
    val balance: Long,
    val carryover: Boolean = false
)
