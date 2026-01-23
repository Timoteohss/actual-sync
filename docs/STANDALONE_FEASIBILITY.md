# Feasibility Analysis: Standalone Budgeting App with Optional Sync

This document analyzes the feasibility of transforming Actios from a companion app for Actual Budget into a standalone budgeting application that can optionally sync with an Actual Budget server.

## Executive Summary

**Verdict: Highly Feasible**

The ActualSync framework is already built on a **local-first CRDT architecture**. All data operations work offline, with sync being an optional layer. The transformation requires:

1. Adding local budget creation (bypassing server requirement)
2. Making server connection optional
3. Implementing the upload-user-file flow for "sync later" scenarios

The hardest part is modifying ActualSync to support creating budgets locally without server involvement. The iOS app changes are relatively straightforward.

---

## Current Architecture Analysis

### What ActualSync Already Provides

The ActualSync framework implements a production-grade **CRDT (Conflict-free Replicated Data Types)** system:

| Component | Implementation | File Reference |
|-----------|----------------|----------------|
| Hybrid Logical Clocks | Globally-unique timestamps | `crdt/Timestamp.kt:18-91` |
| Merkle Trie | Efficient sync detection | `crdt/Merkle.kt:24-181` |
| Local SQLite Storage | Full offline database | `db/ActualDatabase.sq:1-625` |
| CRDT Message Log | Append-only change history | `db/ActualDatabase.sq:119-134` |
| Bidirectional Sync | Server communication | `sync/SyncEngine.kt:1-432` |

### Current Server Dependencies

| Operation | Server Required | Can Work Offline | Notes |
|-----------|-----------------|------------------|-------|
| Initial login | **YES** | No | Password verification |
| List budgets | **YES** | No | Fetched from server API |
| Download budget | **YES** | No | Full DB transfer |
| Create new budget | **YES** | No | Server creates it |
| View data | No | **YES** | All in local SQLite |
| Create transaction | No | **YES** | Queued for sync |
| Edit transaction | No | **YES** | Queued for sync |
| Delete transaction | No | **YES** | Tombstoned locally |
| Reconciliation | No | **YES** | All local operations |
| Category predictions | No | **YES** | CoreML on-device |
| Multi-device sync | **YES** | No | Server relays changes |

### Key Insight: CRDT Messages Are Generated for ALL Changes

Every local change already generates a CRDT message, regardless of server connectivity:

```kotlin
// SyncEngine.kt:61-101
fun createChange(dataset: String, row: String, column: String, value: Any?): MessageEnvelope {
    val ts = clock.send()  // Generate HLC timestamp
    val encodedValue = encodeValue(value)

    val message = Message(dataset, row, column, encodedValue)
    val envelope = MessageEnvelope.create(ts.toString(), message)

    // Store in local database
    db.actualDatabaseQueries.insertMessage(...)

    // Apply change to entity tables immediately
    applyToTable(dataset, row, column, parsedValue)

    // Update local merkle trie
    localMerkle = Merkle.insert(localMerkle, ts)

    // Add to pending queue for next sync
    pendingMessages.add(envelope)

    return envelope
}
```

This means: **Any locally-created budget already has a complete sync history.**

---

## What Needs to Change

### 1. Local Budget Creation (Core Change)

**Current Flow:**
```
User → Login → Select Budget from Server → Download → Use
```

**New Standalone Flow:**
```
User → Create Local Budget → Use → (Optional) Connect to Server Later
```

#### Required: New `initializeLocalBudget()` Method

```kotlin
// Proposed addition to SyncManager.kt
fun initializeLocalBudget(budgetName: String): String {
    val budgetId = generateUUID()

    // 1. Initialize CRDT engine with fresh clock
    val clientId = Timestamp.makeClientId()  // e.g., "A219E7A71CC18912"
    clock = MutableClock(millis = 0, counter = 0, node = clientId)
    engine = SyncEngine(database, clock)
    engine.initialize()

    // 2. Create default category groups using existing CRUD methods
    val expenseGroupId = generateUUID()
    createCategoryGroup(
        id = expenseGroupId,
        name = "Expenses",
        isIncome = false,
        sortOrder = 0.0,
        hidden = false
    )

    val incomeGroupId = generateUUID()
    createCategoryGroup(
        id = incomeGroupId,
        name = "Income",
        isIncome = true,
        sortOrder = 1.0,
        hidden = false
    )

    // 3. Create default categories
    createDefaultCategories(expenseGroupId, incomeGroupId)

    // 4. Create default payees
    val startingBalancePayeeId = generateUUID()
    createPayee(startingBalancePayeeId, "Starting Balance")

    // 5. Save clock state
    saveClockState()

    return budgetId
}

private fun createDefaultCategories(expenseGroupId: String, incomeGroupId: String) {
    val expenseCategories = listOf(
        "Groceries", "Restaurants", "Transportation", "Utilities",
        "Rent/Mortgage", "Entertainment", "Healthcare", "Clothing",
        "Personal Care", "Education", "Gifts", "Savings"
    )

    for ((index, name) in expenseCategories.withIndex()) {
        val catId = generateUUID()
        createCategory(
            id = catId,
            name = name,
            groupId = expenseGroupId,
            isIncome = false,
            sortOrder = index.toDouble(),
            hidden = false
        )
    }

    val salaryId = generateUUID()
    createCategory(
        id = salaryId,
        name = "Salary",
        groupId = incomeGroupId,
        isIncome = true,
        sortOrder = 0.0,
        hidden = false
    )
}
```

### 2. Offline Mode Flag

Add a mode flag to skip server requirements:

```kotlin
// SyncManager.kt additions
enum class SyncMode {
    LOCAL_ONLY,    // Never sync, purely offline
    CONNECTED      // Normal sync mode
}

private var syncMode: SyncMode = SyncMode.LOCAL_ONLY

fun setSyncMode(mode: SyncMode) {
    this.syncMode = mode
}

// Modify sync methods to check mode
suspend fun sync(): SyncResult {
    if (syncMode == SyncMode.LOCAL_ONLY) {
        return SyncResult.Success(0, 0, 0)  // No-op in local mode
    }
    // ... existing sync logic
}
```

### 3. iOS App Changes (BudgetService)

```swift
// BudgetService.swift additions
enum BudgetMode {
    case localOnly
    case connected
}

var budgetMode: BudgetMode = .localOnly

func createLocalBudget(name: String) async throws -> String {
    // Create new database file
    let dbName = "\(UUID().uuidString).db"
    database = HelpersKt.createOrOpenDatabase(dbName: dbName)

    // Initialize SyncManager in local mode
    syncManager = SyncManager(serverUrl: "", httpClient: httpClient, database: database!)
    syncManager?.initializeLocalBudget(budgetName: name)

    // Store budget info
    UserDefaults.standard.set(dbName, forKey: "localBudgetDb")
    UserDefaults.standard.set(name, forKey: "budgetName")

    // Load data
    try await loadData()

    budgetMode = .localOnly
    return dbName
}

func connectToServer(serverUrl: String, password: String) async throws {
    // Authenticate
    let token = try await syncClient?.login(password: password)
    KeychainHelper.save(key: "authToken", value: token)

    // Upload local budget to server
    let fileId = UserDefaults.standard.string(forKey: "localBudgetDb") ?? UUID().uuidString
    let budgetName = UserDefaults.standard.string(forKey: "budgetName") ?? "My Budget"

    let dbData = try exportDatabaseAsZip()
    let groupId = try await syncClient?.uploadBudget(
        fileId: fileId,
        name: budgetName,
        data: dbData
    )

    // Store sync identifiers
    UserDefaults.standard.set(fileId, forKey: "fileId")
    UserDefaults.standard.set(groupId, forKey: "groupId")

    // Switch to connected mode
    syncManager?.setBudget(fileId: fileId, groupId: groupId!)
    budgetMode = .connected

    // Perform initial sync
    try await sync()
}
```

---

## Effort Breakdown

| Component | Changes Required | Complexity | Status |
|-----------|-----------------|------------|--------|
| **ActualSync (Kotlin)** | | | |
| Category group CRUD | `create/update/deleteCategoryGroup()` | Low | ✅ Done |
| Category CRUD with options | `isIncome`, `sortOrder`, `hidden` params | Low | ✅ Done |
| `initializeLocalBudget()` | New method | Medium | Pending |
| `SyncMode` enum | New enum + checks | Low | Pending |
| Default categories data | Hardcoded list | Low | Pending |
| **iOS App** | | | |
| `BudgetService` changes | New methods | Medium | Pending |
| Onboarding UI | New flow screens | Medium | Pending |
| Settings UI | Sync connection options | Low | Pending |
| Budget list | Enumerate local files | Low | Pending |
| **Testing** | | | |
| Local-only scenarios | New test cases | Medium | Pending |
| Upload + sync scenarios | New test cases | Medium | Pending |

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                      ACTIOS STANDALONE                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌─────────────────────────┐    ┌─────────────────────────┐        │
│  │    LOCAL MODE           │    │    CONNECTED MODE       │        │
│  │    (Default)            │    │    (Optional)           │        │
│  ├─────────────────────────┤    ├─────────────────────────┤        │
│  │ • Create budgets locally│    │ • Connect to server     │        │
│  │ • Full CRUD operations  │    │ • Upload local budget   │        │
│  │ • CRDT messages tracked │    │ • Sync changes both ways│        │
│  │ • Export/backup support │    │ • Multi-device support  │        │
│  │ • No server required    │    │ • Server required       │        │
│  └────────────┬────────────┘    └────────────┬────────────┘        │
│               │                              │                      │
│               └──────────────┬───────────────┘                      │
│                              │                                      │
│  ┌───────────────────────────▼───────────────────────────────────┐  │
│  │              BudgetService (Modified)                         │  │
│  │  • budgetMode: .localOnly | .connected                        │  │
│  │  • createLocalBudget(name:) ← NEW                             │  │
│  │  • connectToServer(url:password:) ← NEW                       │  │
│  │  • exportDatabaseAsZip() ← NEW                                │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                      │
│  ┌───────────────────────────▼───────────────────────────────────┐  │
│  │              ActualSync Framework                              │  │
│  │  • initializeLocalBudget(name:) ← NEW                         │  │
│  │  • SyncMode.LOCAL_ONLY | CONNECTED ← NEW                      │  │
│  │  • All existing CRDT logic preserved                          │  │
│  │  • uploadBudget() ← EXISTS (SyncClient.kt:148-169)            │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                      │
│  ┌───────────────────────────▼───────────────────────────────────┐  │
│  │              SQLite Database                                   │  │
│  │  • Same schema (ActualDatabase.sq)                            │  │
│  │  • messages_crdt table stores all changes                     │  │
│  │  • sync_metadata stores clock state                           │  │
│  │  • Ready for future sync if user connects                     │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Key Decisions to Make

### 1. Default Categories

What preset categories should ship with new local budgets?

**Option A: Minimal (Recommended for v1)**
- Income: Salary
- Expenses: Groceries, Restaurants, Transportation, Utilities, Rent, Entertainment, Healthcare, Other

**Option B: Comprehensive (Match Actual Budget)**
- Full category tree matching what Actual Budget creates
- ~20 categories across 5-6 groups

### 2. Multiple Local Budgets

Should users be able to have multiple offline budgets?

**Option A: Single Budget (Simpler)**
- One budget at a time
- Clear mental model
- Matches current Actios behavior

**Option B: Multiple Budgets**
- Enumerate `.db` files in documents directory
- Budget picker on launch
- More complex state management

### 3. Sync Model

What happens when a local user wants to sync?

**Option A: Upload Only (Recommended)**
- Local budget becomes THE server budget
- Simple, predictable
- User chooses when to "go online"

**Option B: Merge with Existing**
- Complex: requires ID remapping
- Potential for data conflicts
- Not recommended for v1

### 4. Import/Export

How do users move data?

**Recommended:**
- Export: ZIP of db.sqlite (Actual Budget compatible)
- Import from Actual Budget: Download existing budget
- Import from CSV: Future enhancement

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Schema drift from Actual Budget | Medium | High | Pin to specific Actual version, test compatibility |
| CRDT conflicts on first sync | Low | Medium | Upload creates fresh server state, no conflicts |
| User confusion about modes | Medium | Low | Clear UI indicating local vs connected |
| Data loss during mode switch | Low | High | Backup before upload, validate success |

---

## What's Already Working in Your Favor

From analyzing the codebase:

1. **All transaction operations work offline**
   - `SyncManager.createTransaction()` writes to SQLite immediately
   - Reference: `SyncManager.kt:348-367`

2. **CRDT messages generated regardless of connectivity**
   - Every change creates a message in `messages_crdt`
   - Reference: `SyncEngine.kt:61-101`

3. **Merkle trie rebuilt from messages**
   - No special initialization needed
   - Reference: `SyncEngine.kt:43-55`

4. **Upload endpoint exists**
   - `SyncClient.uploadBudget()` ready to use
   - Reference: `SyncClient.kt:148-169`

5. **Balance calculations are local**
   - SQL aggregation, no server dependency
   - Reference: `ActualDatabase.sq:499-502`

6. **Category group CRUD methods exist**
   - `createCategoryGroup()`, `updateCategoryGroup()`, `deleteCategoryGroup()` implemented
   - `createCategory()` supports `isIncome`, `sortOrder`, `hidden` optional parameters
   - Reference: `SyncManager.kt:353-398`

---

## Recommended Implementation Order

1. **Phase 1: Local Budget Creation**
   - Add `initializeLocalBudget()` to SyncManager
   - Add default category/payee data
   - Test local-only usage

2. **Phase 2: iOS App Changes**
   - New onboarding flow (local vs connected)
   - `createLocalBudget()` in BudgetService
   - Settings for sync configuration

3. **Phase 3: Connect to Server**
   - Implement `connectToServer()` flow
   - Upload local budget
   - Switch to connected mode

4. **Phase 4: Polish**
   - Sync status indicators
   - Error handling
   - Backup/export functionality

---

## File References

### ActualSync Framework (Kotlin)

| File | Purpose | Key Lines |
|------|---------|-----------|
| `sync/SyncManager.kt` | High-level sync coordinator | 29-1287 |
| `sync/SyncEngine.kt` | CRDT message handling | 19-432 |
| `sync/SyncClient.kt` | HTTP communication | 148-169 (upload) |
| `crdt/Timestamp.kt` | HLC timestamp generation | 18-164 |
| `crdt/Merkle.kt` | Merkle trie operations | 24-181 |
| `db/ActualDatabase.sq` | SQLite schema & queries | 1-625 |

### iOS App (Swift)

| File | Purpose |
|------|---------|
| `Services/BudgetService.swift` | Core coordinator |
| `Services/TransactionService.swift` | Transaction CRUD |
| `Services/SyncService.swift` | Sync state management |

---

## Conclusion

Transforming Actios into a standalone budgeting app is **highly feasible** because:

1. The CRDT architecture is already local-first
2. All data operations work offline
3. The upload endpoint exists for "sync later" scenarios
4. The change history is preserved, enabling seamless server connection

The main work is in ActualSync (adding `initializeLocalBudget()`) and the iOS app (new onboarding flow). The data layer requires minimal changes.

**Estimated effort: 40% ActualSync, 30% BudgetService, 30% UI**
