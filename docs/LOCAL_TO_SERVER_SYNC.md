# First Sync Architecture: Local → Server

This document details exactly what happens when a user creates a budget locally and later decides to sync with an Actual Budget server. It covers the CRDT mechanics, server protocol, and implementation requirements.

## Overview

The key insight is that the **upload-user-file endpoint** (`/sync/upload-user-file`) is designed exactly for this use case. A locally-created budget can be uploaded to become a server-managed budget, and from that point forward, sync "just works."

```
LOCAL BUDGET                          FIRST SYNC                           SERVER BUDGET
─────────────────                     ─────────────────                    ─────────────────
Create DB locally                     Authenticate with server             Budget now on server
Generate node ID                      ZIP local db.sqlite                  groupId assigned
All changes → CRDT messages           Upload via uploadBudget()            Other devices can sync
Merkle trie tracks history            Server returns groupId               Incremental sync works
                        ────────────────────────────────────────►
```

---

## Phase 1: Local Budget Creation

### Step 1: Initialize Database with Schema

Create a fresh SQLite database with the Actual Budget schema. The schema is defined in:
- **File:** `kotlin/src/commonMain/sqldelight/com/actualbudget/sync/db/ActualDatabase.sq`

**Required Tables:**

```sql
-- Core entity tables
accounts              -- Bank accounts
payees                -- Payee master list
payee_mapping         -- Payee ID mappings (required for payee lookups)
categories            -- Category definitions
category_groups       -- Category groupings
transactions          -- All transactions
zero_budgets          -- Monthly budget allocations

-- Sync infrastructure tables
messages_crdt         -- CRDT message log (CRITICAL for sync)
sync_metadata         -- Clock state storage
```

### Step 2: Generate Client Identity

Each client needs a unique node ID for HLC timestamps:

```kotlin
// Timestamp.kt:52-54
fun makeClientId(): String {
    return uuid4().toString().replace("-", "").takeLast(16).uppercase()
}
// Result: e.g., "A219E7A71CC18912"
```

Initialize the clock state:

```kotlin
// MutableClock initialization
clock = MutableClock(
    millis = 0,        // Will be updated on first change
    counter = 0,       // Increments within same millisecond
    node = clientId    // Unique to this device
)

// Store in sync_metadata table
db.setSyncMetadata("clock_millis", "0")
db.setSyncMetadata("clock_counter", "0")
db.setSyncMetadata("clock_node", clientId)
```

### Step 3: Create Default Data AS CRDT MESSAGES

**This is the critical step.** Don't just INSERT rows—create CRDT messages for each field. This ensures sync compatibility.

```kotlin
// WRONG: Direct insert (not sync-compatible)
db.insertCategoryGroup(id, "Expenses", 0, null, 0, 0)

// CORRECT: Use SyncManager's high-level methods (recommended)
syncManager.createCategoryGroup(
    id = groupId,
    name = "Expenses",
    isIncome = false,
    sortOrder = 0.0,
    hidden = false
)

// Or use low-level CRDT messages directly:
engine.createChange("category_groups", groupId, "name", "Expenses")
engine.createChange("category_groups", groupId, "is_income", 0)
engine.createChange("category_groups", groupId, "sort_order", 0.0)
engine.createChange("category_groups", groupId, "hidden", 0)
engine.createChange("category_groups", groupId, "tombstone", 0)
```

**Available high-level methods in SyncManager:**
- `createCategoryGroup(id, name, isIncome, sortOrder, hidden)` - Create a category group
- `updateCategoryGroup(id, field, value)` - Update a category group field
- `deleteCategoryGroup(id)` - Delete a category group (tombstone)
- `createCategory(id, name, groupId, isIncome, sortOrder, hidden)` - Create a category
- `updateCategory(id, field, value)` - Update a category field
- `deleteCategory(id)` - Delete a category (tombstone)

**What `createChange()` does internally:**

```kotlin
// SyncEngine.kt:61-101
fun createChange(dataset: String, row: String, column: String, value: Any?): MessageEnvelope {
    // 1. Generate HLC timestamp
    val ts = clock.send()  // e.g., "2025-01-22T10:00:00.123Z-0000-A219E7A71CC18912"

    // 2. Encode value
    val encodedValue = encodeValue(value)  // "S:Expenses" or "N:0" or "0:"

    // 3. Create message
    val message = Message(dataset, row, column, encodedValue)
    val envelope = MessageEnvelope.create(ts.toString(), message)

    // 4. Store in messages_crdt table
    db.insertMessage(
        timestamp = ts.toString(),
        dataset = dataset,
        row = row,
        column = column,
        value_ = encodedValue.encodeToByteArray()
    )

    // 5. Apply to entity table immediately
    applyToTable(dataset, row, column, value)

    // 6. Update merkle trie
    localMerkle = Merkle.insert(localMerkle, ts)

    // 7. Queue for sync
    pendingMessages.add(envelope)

    return envelope
}
```

### Step 4: Merkle Trie State

The merkle trie tracks all message timestamps for efficient sync detection:

```kotlin
// Merkle.kt:47-55
fun insert(trie: TrieNode, timestamp: Timestamp): TrieNode {
    val hash = timestamp.hash()  // MurmurHash3 of timestamp string
    val minutes = timestamp.millis / 1000 / 60
    val key = minutes.toString(3)  // Base-3 representation

    val newTrie = trie.copy(hash = trie.hash xor hash)
    return insertKey(newTrie, key, hash)
}
```

**After creating a local budget with default data, the state looks like:**

```
messages_crdt table:
┌──────────────────────────────────────────────────┬──────────────────┬─────────┐
│ timestamp                                        │ dataset          │ column  │
├──────────────────────────────────────────────────┼──────────────────┼─────────┤
│ 2025-01-22T10:00:00.000Z-0000-A219E7A71CC18912  │ category_groups  │ id      │
│ 2025-01-22T10:00:00.000Z-0001-A219E7A71CC18912  │ category_groups  │ name    │
│ 2025-01-22T10:00:00.000Z-0002-A219E7A71CC18912  │ category_groups  │ is_income│
│ ... (all default data messages)                  │                  │         │
└──────────────────────────────────────────────────┴──────────────────┴─────────┘

Merkle trie:
{
  "hash": 847291,
  "children": {
    "2": {
      "hash": 384721,
      "children": { ... }
    }
  }
}
```

---

## Phase 2: User Uses App Normally

Every operation goes through `SyncManager`, creating CRDT messages:

```swift
// iOS: User creates a transaction
budgetService.createTransaction(
    accountId: "checking-123",
    date: 20250122,
    amount: -5000,  // -$50.00
    payeeId: "grocery-store-456",
    categoryId: "groceries-789"
)
```

```kotlin
// Kotlin: SyncManager.createTransaction()
fun createTransaction(id, accountId, date, amount, payeeId, categoryId, notes): String {
    engine.createChange("transactions", id, "acct", accountId)
    engine.createChange("transactions", id, "date", date)
    engine.createChange("transactions", id, "amount", amount)
    engine.createChange("transactions", id, "description", payeeId)  // Note: 'description' stores payee ID
    engine.createChange("transactions", id, "category", categoryId)
    engine.createChange("transactions", id, "cleared", 0)
    engine.createChange("transactions", id, "tombstone", 0)
    return id
}
```

**Result: Complete change history accumulates in `messages_crdt`.**

---

## Phase 3: First Sync with Server

### Step 1: Authenticate

```kotlin
// SyncClient.kt:56-80
suspend fun login(password: String): String? {
    val response = httpClient.post("$serverUrl/account/login") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("password" to password))
    }
    // Returns auth token
    return response.data?.token
}
```

Store the token:
```swift
// iOS
KeychainHelper.save(key: "authToken", value: token)
syncManager?.setToken(token)
```

### Step 2: Generate Budget Identifiers

```kotlin
// Generate fileId (or use existing if converting)
val fileId = generateUUID()  // e.g., "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

// groupId will be assigned by server
var groupId: String? = null
```

### Step 3: Package Local Database

Create a ZIP file containing the local `db.sqlite`:

```kotlin
// BudgetFileManager.kt handles ZIP operations
fun exportDatabaseAsZip(): ByteArray {
    // 1. Get database file path
    val dbPath = getDefaultBudgetDir() + "/actual.db"

    // 2. Create ZIP with db.sqlite inside
    val zipData = createZip(mapOf("db.sqlite" to readFile(dbPath)))

    return zipData
}
```

### Step 4: Upload to Server

```kotlin
// SyncClient.kt:148-169
suspend fun uploadBudget(
    fileId: String,
    name: String,
    data: ByteArray,
    groupId: String? = null
): String {
    val response = httpClient.post("$serverUrl/sync/upload-user-file") {
        token?.let { header("X-ACTUAL-TOKEN", it) }
        header("X-ACTUAL-FILE-ID", fileId)
        header("X-ACTUAL-NAME", name)
        groupId?.let { header("X-ACTUAL-GROUP-ID", it) }
        contentType(ContentType("application", "encrypted-file"))
        setBody(data)
    }

    // Server assigns groupId if not provided
    return parseGroupIdFromResponse(response)
}
```

**Server Response:**
```json
{
  "status": "ok",
  "groupId": "sync-group-abc123"
}
```

### Step 5: Configure Sync Manager

```kotlin
// Store identifiers
syncManager.setToken(authToken)
syncManager.setBudget(fileId, groupId)

// Save to persistent storage
UserDefaults.set("fileId", fileId)
UserDefaults.set("groupId", groupId)
```

### Step 6: Initial Sync (Confirmation)

Perform a sync to verify everything is in order:

```kotlin
// SyncManager.kt:88-94
suspend fun fullSync(): SyncResult {
    val request = engine.buildSyncRequest(fileId, groupId, fullSync = true)
    return performSync(request)
}
```

**Request sent to server:**
```protobuf
SyncRequest {
    messages: []  // Empty - we uploaded the DB, no new changes
    fileId: "a1b2c3d4-..."
    groupId: "sync-group-abc123"
    since: "1970-01-01T00:00:00.000Z-0000-0000000000000000"  // Full sync
}
```

**Server response:**
```protobuf
SyncResponse {
    messages: []  // Empty - server has same data (we uploaded it)
    merkle: "{\"hash\": 847291, ...}"  // Should match our local merkle
}
```

**Verification:**
```kotlin
// SyncEngine.kt:428-431
fun isInSync(): Boolean {
    val serverMerkle = getServerMerkle() ?: return false
    return Merkle.diff(localMerkle, serverMerkle) == null
}
```

---

## What the Server Does

When the server receives an upload:

```
POST /sync/upload-user-file
Headers:
  X-ACTUAL-TOKEN: {authToken}
  X-ACTUAL-FILE-ID: {fileId}
  X-ACTUAL-NAME: "My Budget"
Body: [ZIP with db.sqlite]
```

**Server processing:**

1. **Validate auth token** → Identify user
2. **Extract ZIP** → Get db.sqlite
3. **Generate groupId** → Unique sync group identifier
4. **Store budget file** → Save to server's budget directory
5. **Register in files table:**
   ```sql
   INSERT INTO files (id, group_id, name, owner, sync_version)
   VALUES ('a1b2c3d4-...', 'sync-group-abc123', 'My Budget', 'user-id', 1)
   ```
6. **Return groupId** → Client uses for future syncs

---

## Why This Works

### CRDT Messages Are Self-Describing

Each message contains everything needed to apply it:

```protobuf
Message {
    dataset: "transactions"           // Table name
    row: "txn-uuid-123"              // Primary key
    column: "amount"                  // Column to update
    value: "N:-5000"                 // Typed value
}
```

### Merkle Trie Enables Efficient Sync

```kotlin
// Merkle.kt:87-123
fun diff(trie1: TrieNode, trie2: TrieNode): Long? {
    if (trie1.hash == trie2.hash) {
        return null  // In sync!
    }
    // Find earliest divergence point
    // Returns timestamp in milliseconds
}
```

After upload:
- Server merkle = Client merkle (same data)
- `diff()` returns `null`
- **Result: In sync**

### Future Changes Sync Incrementally

When user makes a change after connecting:

```
Local change → CRDT message → pendingMessages queue
                   ↓
              Next sync()
                   ↓
         SyncRequest with messages
                   ↓
         Server applies, responds
                   ↓
         Merkles updated, still in sync
```

---

## Complete Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           LOCAL BUDGET CREATION                              │
└─────────────────────────────────────────────────────────────────────────────┘

Step 1: Initialize Database
───────────────────────────
┌──────────────────┐
│ Create actual.db │ ← Schema from ActualDatabase.sq
│ with all tables  │
└────────┬─────────┘
         │
         ▼
Step 2: Generate Client ID
──────────────────────────
┌───────────────────────────┐
│ clientId = makeClientId() │ → "A219E7A71CC18912"
│ clock = MutableClock(     │
│   millis=0, counter=0,    │
│   node=clientId           │
│ )                         │
└────────┬──────────────────┘
         │
         ▼
Step 3: Create Default Data (AS CRDT MESSAGES)
──────────────────────────────────────────────
┌─────────────────────────────────────────────────────────────┐
│ for each default entity:                                     │
│   engine.createChange(dataset, id, "field", value)          │
│     ├─ Generates HLC timestamp                              │
│     ├─ Stores in messages_crdt table                        │
│     ├─ Applies to entity table                              │
│     └─ Updates merkle trie                                  │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
Step 4: User Uses App
─────────────────────
┌─────────────────────────────────────────────────────────────┐
│ All operations via SyncManager:                              │
│   createTransaction() → CRDT messages                       │
│   updateTransaction() → CRDT messages                       │
│   deleteTransaction() → CRDT message (tombstone=1)          │
│                                                              │
│ State accumulates:                                           │
│   messages_crdt: [msg1, msg2, msg3, ... msgN]              │
│   merkle: { hash: XYZ, children: {...} }                    │
└────────┬────────────────────────────────────────────────────┘
         │
         │
┌────────▼────────────────────────────────────────────────────────────────────┐
│                           FIRST SYNC WITH SERVER                             │
└─────────────────────────────────────────────────────────────────────────────┘

Step 1: Authenticate
────────────────────
┌─────────────────────────────────┐
│ POST /account/login             │
│ { "password": "..." }           │
│                                 │
│ Response: { token: "abc123" }   │
└────────┬────────────────────────┘
         │
         ▼
Step 2: Package Database
────────────────────────
┌─────────────────────────────────┐
│ ZIP actual.db → budget.zip      │
│ (includes messages_crdt table!) │
└────────┬────────────────────────┘
         │
         ▼
Step 3: Upload to Server
────────────────────────
┌─────────────────────────────────────────────────────────────┐
│ POST /sync/upload-user-file                                  │
│ Headers:                                                     │
│   X-ACTUAL-TOKEN: abc123                                    │
│   X-ACTUAL-FILE-ID: budget-uuid                             │
│   X-ACTUAL-NAME: "My Budget"                                │
│ Body: [ZIP data]                                            │
│                                                              │
│ Server Response:                                             │
│   { "status": "ok", "groupId": "sync-group-xyz" }          │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
Step 4: Configure Sync
──────────────────────
┌─────────────────────────────────────────────────────────────┐
│ syncManager.setToken(authToken)                              │
│ syncManager.setBudget(fileId, groupId)                       │
│ UserDefaults.set("fileId", fileId)                          │
│ UserDefaults.set("groupId", groupId)                        │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
Step 5: Initial Sync (Verification)
───────────────────────────────────
┌─────────────────────────────────────────────────────────────┐
│ POST /sync/sync                                              │
│ SyncRequest {                                                │
│   messages: []      // No new changes since upload          │
│   fileId: "..."                                             │
│   groupId: "..."                                            │
│   since: "1970-01-01T00:00:00.000Z-0000-0000000000000000"  │
│ }                                                            │
│                                                              │
│ SyncResponse {                                               │
│   messages: []      // Server has same data                 │
│   merkle: "{...}"   // Should match local                   │
│ }                                                            │
│                                                              │
│ Verification:                                                │
│   Merkle.diff(localMerkle, serverMerkle) == null           │
│   ✅ IN SYNC                                                 │
└────────┬────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              SYNC ESTABLISHED                                │
│                                                                              │
│  • Local merkle matches server merkle                                        │
│  • Future changes sync incrementally                                         │
│  • Other devices can download this budget                                    │
│  • Multi-device sync now works                                               │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Value Encoding

All values in CRDT messages use typed encoding:

```kotlin
// SyncEngine.kt:106-114
private fun encodeValue(value: Any?): String {
    return when (value) {
        null -> "0:"              // Null
        is String -> "S:$value"   // String
        is Number -> "N:$value"   // Number
        is Boolean -> "N:${if (value) 1 else 0}"
        else -> "S:$value"
    }
}
```

**Examples:**
| Value | Encoded |
|-------|---------|
| `null` | `0:` |
| `"Groceries"` | `S:Groceries` |
| `5000` | `N:5000` |
| `-5000` | `N:-5000` |
| `true` | `N:1` |
| `false` | `N:0` |

---

## Critical Implementation Notes

### 1. The `description` Column Stores Payee ID

```sql
-- ActualDatabase.sq:48
-- Note: 'description' column stores the payee ID (not the payee name)
```

```kotlin
// SyncManager.kt:361
if (payeeId != null) engine.createChange("transactions", id, "description", payeeId)
```

### 2. Payee Mapping Required

Every payee needs a `payee_mapping` entry:

```kotlin
// SyncManager.kt:289-296
fun createPayee(id: String, name: String): String {
    engine.createChange("payees", id, "name", name)
    engine.createChange("payees", id, "tombstone", 0)
    // Required for payee to be usable
    engine.createChange("payee_mapping", id, "targetId", id)
    return id
}
```

### 3. Tombstone Pattern for Deletes

No hard deletes—set `tombstone = 1`:

```kotlin
// SyncManager.kt:395-397
fun deleteTransaction(id: String) {
    engine.createChange("transactions", id, "tombstone", 1)
}
```

### 4. Split Transactions

- Parent: `isParent = 1`, no category
- Child: `isChild = 1`, `parent_id` points to parent

```kotlin
// SyncManager.kt:793-795
fun convertToSplitParent(transactionId: String) {
    engine.createChange("transactions", transactionId, "isParent", 1)
    engine.createChange("transactions", transactionId, "category", null)
}
```

### 5. Transfer Transactions

Two linked transactions with opposite amounts:

```kotlin
// SyncManager.kt:945-984
fun createTransfer(...) {
    // From side (negative)
    engine.createChange("transactions", fromTxId, "amount", -amount)
    engine.createChange("transactions", fromTxId, "transferred_id", toTxId)

    // To side (positive)
    engine.createChange("transactions", toTxId, "amount", amount)
    engine.createChange("transactions", toTxId, "transferred_id", fromTxId)
}
```

---

## Alternative: Merge with Existing Server Budget

**NOT RECOMMENDED for v1.** This is complex:

```
Local budget             Server budget (existing)
     │                          │
     │  User wants to           │
     │  "merge" them            │
     ▼                          ▼
┌─────────────────────────────────────┐
│         CONFLICT ZONE               │
│  • Different entity IDs             │
│  • Different node IDs               │
│  • Potentially overlapping data     │
└─────────────────────────────────────┘

Would require:
1. Download server budget
2. Map local IDs → server IDs
3. Rewrite all local messages with new IDs
4. Merge merkle tries carefully
5. Handle duplicate detection
6. Upload merged result
```

**Recommendation:** Offer simple choices instead:
- "Upload local budget" (replaces server if exists)
- "Download server budget" (replaces local)
- "Keep both as separate budgets"

---

## File References

| File | Purpose | Key Sections |
|------|---------|--------------|
| `sync/SyncManager.kt` | High-level sync coordination | Lines 44-94 (init, setBudget, fullSync) |
| `sync/SyncEngine.kt` | CRDT message handling | Lines 61-101 (createChange), 191-249 (processSyncResponse) |
| `sync/SyncClient.kt` | HTTP communication | Lines 148-169 (uploadBudget) |
| `crdt/Timestamp.kt` | HLC timestamp generation | Lines 52-54 (makeClientId), 104-121 (send) |
| `crdt/Merkle.kt` | Merkle trie operations | Lines 47-55 (insert), 87-123 (diff) |
| `db/ActualDatabase.sq` | SQLite schema | Lines 119-134 (messages_crdt), 131-134 (sync_metadata) |
| `proto/SyncMessages.kt` | Protobuf definitions | Lines 201-246 (SyncRequest) |

---

## Summary

The "local first, sync later" flow works because:

1. **All changes generate CRDT messages** from day one
2. **Merkle trie tracks complete history** of all changes
3. **Upload endpoint exists** to push local budget to server
4. **Server assigns groupId** for future sync coordination
5. **Incremental sync works** because merkle tries match after upload

The key requirement: **Generate CRDT messages for ALL changes**, including initial default data. This makes the sync history complete and server-compatible from the start.
