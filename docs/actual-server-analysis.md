# Actual Budget Server Analysis

Complete documentation of the Actual Budget server database schema and sync protocol.

## Project Structure

The Actual Budget application packages:
- **loot-core**: Core server logic, database layer, sync handling
- **sync-server**: Sync protocol server and authentication
- **crdt**: CRDT implementation for sync
- **desktop-client**: Desktop UI
- **api**: Public API handlers

---

## Complete Database Schema

### BUDGET DATABASE (loot-core)

#### accounts
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| account_id | TEXT | - |
| name | TEXT | - |
| balance_current | INTEGER | - |
| balance_available | INTEGER | - |
| balance_limit | INTEGER | - |
| mask | TEXT | - |
| official_name | TEXT | - |
| type | TEXT | - |
| subtype | TEXT | - |
| bank | TEXT | - |
| offbudget | INTEGER | 0 |
| closed | INTEGER | 0 |
| tombstone | INTEGER | 0 |
| sort_order | REAL | - |
| account_sync_source | TEXT | - |
| last_sync | TEXT | - |
| last_reconciled | TEXT | - |

#### transactions
| Column | Type | Default | Notes |
|--------|------|---------|-------|
| id | TEXT PRIMARY KEY | - | |
| isParent | INTEGER | 0 | |
| isChild | INTEGER | 0 | |
| acct | TEXT | - | Account ID |
| category | TEXT | - | Category ID |
| amount | INTEGER | - | In cents |
| **description** | TEXT | - | **PAYEE ID** (not name!) |
| notes | TEXT | - | |
| date | INTEGER | - | YYYYMMDD format |
| financial_id | TEXT | - | |
| type | TEXT | - | |
| location | TEXT | - | |
| error | TEXT | - | |
| imported_description | TEXT | - | Original payee name from bank |
| starting_balance_flag | INTEGER | 0 | |
| transferred_id | TEXT | - | |
| sort_order | REAL | - | |
| tombstone | INTEGER | 0 | |
| parent_id | TEXT | - | |
| cleared | INTEGER | 1 | |
| pending | INTEGER | 0 | |
| schedule | TEXT | - | |
| reconciled | INTEGER | 0 | |
| raw_synced_data | TEXT | - | |

#### payees
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| name | TEXT | - |
| category | TEXT | - |
| tombstone | INTEGER | 0 |
| transfer_acct | TEXT | - |
| favorite | INTEGER | 0 |
| learn_categories | BOOLEAN | 1 |

#### payee_mapping
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| targetId | TEXT | - |

#### categories
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| name | TEXT | - |
| is_income | INTEGER | 0 |
| cat_group | TEXT | - |
| sort_order | REAL | - |
| tombstone | INTEGER | 0 |
| hidden | BOOLEAN | 0 |
| goal_def | TEXT | null |
| template_settings | JSON | '{"source": "notes"}' |

#### category_groups
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| name | TEXT | - |
| is_income | INTEGER | 0 |
| sort_order | REAL | - |
| tombstone | INTEGER | 0 |
| hidden | BOOLEAN | 0 |

#### category_mapping
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| transferId | TEXT | - |

#### zero_budgets
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| month | INTEGER | - |
| category | TEXT | - |
| amount | INTEGER | 0 |
| carryover | INTEGER | 0 |
| goal | INTEGER | null |
| long_goal | INTEGER | null |

#### zero_budget_months
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| buffered | INTEGER | 0 |

#### reflect_budgets
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| month | INTEGER | - |
| category | TEXT | - |
| amount | INTEGER | 0 |
| carryover | INTEGER | 0 |
| goal | INTEGER | null |
| long_goal | INTEGER | null |

#### rules
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| stage | TEXT | - |
| conditions | TEXT | - |
| actions | TEXT | - |
| tombstone | INTEGER | 0 |
| conditions_op | TEXT | 'and' |

#### schedules
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| rule | TEXT | - |
| active | INTEGER | 0 |
| completed | INTEGER | 0 |
| posts_transaction | INTEGER | 0 |
| tombstone | INTEGER | 0 |
| name | TEXT | null |

#### schedules_next_date
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| schedule_id | TEXT | - |
| local_next_date | INTEGER | - |
| local_next_date_ts | INTEGER | - |
| base_next_date | INTEGER | - |
| base_next_date_ts | INTEGER | - |
| tombstone | INTEGER | 0 |

#### schedules_json_paths
| Column | Type | Default |
|--------|------|---------|
| schedule_id | TEXT PRIMARY KEY | - |
| payee | TEXT | - |
| account | TEXT | - |
| amount | TEXT | - |
| date | TEXT | - |

#### notes
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| note | TEXT | - |

#### banks
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| bank_id | TEXT | - |
| name | TEXT | - |
| tombstone | INTEGER | 0 |

#### pending_transactions
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| acct | INTEGER | - |
| amount | INTEGER | - |
| description | TEXT | - |
| date | TEXT | - |

#### transaction_filters
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| name | TEXT | - |
| conditions | TEXT | - |
| conditions_op | TEXT | 'and' |
| tombstone | INTEGER | 0 |

#### custom_reports
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| name | TEXT | - |
| start_date | TEXT | - |
| end_date | TEXT | - |
| date_static | INTEGER | 0 |
| date_range | TEXT | - |
| mode | TEXT | 'total' |
| group_by | TEXT | 'Category' |
| balance_type | TEXT | 'Expense' |
| show_empty | INTEGER | 0 |
| show_offbudget | INTEGER | 0 |
| show_hidden | INTEGER | 0 |
| show_uncategorized | INTEGER | 0 |
| selected_categories | TEXT | - |
| graph_type | TEXT | 'BarGraph' |
| conditions | TEXT | - |
| conditions_op | TEXT | 'and' |
| metadata | TEXT | - |
| interval | TEXT | 'Monthly' |
| color_scheme | TEXT | - |
| tombstone | INTEGER | 0 |
| include_current | INTEGER | 0 |
| sort_by | TEXT | 'desc' |
| trim_intervals | INTEGER | 0 |

#### dashboard
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| type | TEXT | - |
| width | INTEGER | - |
| height | INTEGER | - |
| x | INTEGER | - |
| y | INTEGER | - |
| meta | TEXT | - |
| tombstone | INTEGER | 0 |

#### tags
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| tag | TEXT UNIQUE | - |
| color | TEXT | - |
| description | TEXT | - |
| tombstone | INTEGER | 0 |

#### preferences
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| value | TEXT | - |

#### spreadsheet_cells
| Column | Type | Default |
|--------|------|---------|
| name | TEXT PRIMARY KEY | - |
| expr | TEXT | - |
| cachedValue | TEXT | - |

#### created_budgets
| Column | Type | Default |
|--------|------|---------|
| month | TEXT PRIMARY KEY | - |

#### kvcache
| Column | Type | Default |
|--------|------|---------|
| key | TEXT PRIMARY KEY | - |
| value | TEXT | - |

#### kvcache_key
| Column | Type | Default |
|--------|------|---------|
| id | INTEGER PRIMARY KEY | - |
| key | REAL | - |

#### messages_crdt
| Column | Type | Default |
|--------|------|---------|
| id | INTEGER PRIMARY KEY | - |
| timestamp | TEXT NOT NULL UNIQUE | - |
| dataset | TEXT NOT NULL | - |
| row | TEXT NOT NULL | - |
| column | TEXT NOT NULL | - |
| value | BLOB NOT NULL | - |

#### messages_clock
| Column | Type | Default |
|--------|------|---------|
| id | INTEGER PRIMARY KEY | - |
| clock | TEXT | - |

#### __migrations__
| Column | Type | Default |
|--------|------|---------|
| id | INT PRIMARY KEY NOT NULL | - |

#### __meta__
| Column | Type | Default |
|--------|------|---------|
| key | TEXT PRIMARY KEY | - |
| value | TEXT | - |

---

### SYNC SERVER DATABASE

#### auth
| Column | Type | Default |
|--------|------|---------|
| method | TEXT PRIMARY KEY | - |
| display_name | TEXT | - |
| extra_data | TEXT | - |
| active | INTEGER | - |

#### sessions
| Column | Type | Default |
|--------|------|---------|
| token | TEXT PRIMARY KEY | - |
| expires_at | INTEGER | - |
| user_id | TEXT | - |
| auth_method | TEXT | - |

#### files
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| group_id | TEXT | - |
| sync_version | SMALLINT | - |
| encrypt_meta | TEXT | - |
| encrypt_keyid | TEXT | - |
| encrypt_salt | TEXT | - |
| encrypt_test | TEXT | - |
| deleted | BOOLEAN | FALSE |
| name | TEXT | - |
| owner | TEXT | - |

#### users
| Column | Type | Default |
|--------|------|---------|
| id | TEXT PRIMARY KEY | - |
| user_name | TEXT | - |
| display_name | TEXT | - |
| role | TEXT | - |
| enabled | INTEGER | 1 |
| owner | INTEGER | 0 |

#### user_access
| Column | Type | Default |
|--------|------|---------|
| user_id | TEXT | - |
| file_id | TEXT | - |

#### secrets
| Column | Type | Default |
|--------|------|---------|
| name | TEXT PRIMARY KEY | - |
| value | BLOB | - |

#### pending_openid_requests
| Column | Type | Default |
|--------|------|---------|
| state | TEXT PRIMARY KEY | - |
| code_verifier | TEXT | - |
| return_url | TEXT | - |
| expiry_time | INTEGER | - |

---

## Sync Protocol

### Message Format (Protobuf)

```protobuf
message Message {
  string dataset = 1;    // Table name
  string row = 2;        // Row ID
  string column = 3;     // Column name
  string value = 4;      // Serialized value
}

message MessageEnvelope {
  string timestamp = 1;
  bool isEncrypted = 2;
  bytes content = 3;
}

message SyncRequest {
  repeated MessageEnvelope messages = 1;
  string fileId = 2;
  string groupId = 3;
  string keyId = 5;
  string since = 6;
}

message SyncResponse {
  repeated MessageEnvelope messages = 1;
  string merkle = 2;
}
```

### Value Serialization

```
null    → "0:"
number  → "N:" + value
string  → "S:" + value
```

---

## Critical Field Mappings

### Database Column → CRDT Column → API Field

| Concept | DB Column | CRDT Column | API Field |
|---------|-----------|-------------|-----------|
| **Payee ID** | `description` | `description` | `payee` |
| Payee Name (imported) | `imported_description` | `imported_description` | `imported_payee` |
| Account ID | `acct` | `acct` | `account` |
| Category Group | `cat_group` | `cat_group` | `group` |
| Parent Transaction | `isParent` | `isParent` | `is_parent` |
| Child Transaction | `isChild` | `isChild` | `is_child` |

### CRITICAL: Transaction Payee Handling

The `description` column in transactions stores the **PAYEE ID**, not the payee name.

The API uses a VIEW that maps fields:
```sql
SELECT
  pm.targetId AS payee
FROM transactions _
LEFT JOIN payee_mapping pm ON pm.id = _.description
```

---

## Differences with actual-sync Project

### Schema Differences

| Table | Field | Actual Server | actual-sync | Fix Required |
|-------|-------|---------------|-------------|--------------|
| transactions | payee column | `description` | `payee` | Map `description` → `payee` |
| transactions | sort_order | REAL | INTEGER | Change to REAL |
| accounts | sort_order | REAL | missing | Add field |
| payees | transfer_acct | TEXT | missing | Add field |
| payees | favorite | INTEGER | missing | Add field |
| payees | learn_categories | BOOLEAN | missing | Add field |
| zero_budgets | long_goal | INTEGER | missing | Add field |
| categories | goal_def | TEXT | missing | Add field |
| categories | template_settings | JSON | missing | Add field |

### CRDT Column Mapping Required

In `SyncEngine.kt`, handle these CRDT column names:
- `description` → maps to `payee` field in local DB
- `isParent` → maps to `is_parent`
- `isChild` → maps to `is_child`

---

## Indexes

Key indexes on transactions table:
- `trans_category_date` on `(category, date)`
- `trans_category` on `(category)`
- `trans_date` on `(date)`
- `trans_parent_id` on `(parent_id)`
- `trans_sorted` on `(date desc, starting_balance_flag, sort_order desc, id)`
- `messages_crdt_search` on messages_crdt `(dataset, row, column, timestamp)`
