# Actual Budget Sync Protocol

This document describes the CRDT-based sync protocol used by Actual Budget. All implementations in this repository must follow this specification to ensure compatibility.

## Overview

Actual Budget uses a **local-first** architecture with **CRDT (Conflict-free Replicated Data Types)** for synchronization. This means:

1. All data is stored locally in SQLite
2. Changes are recorded as CRDT messages with HLC timestamps
3. Sync is performed by exchanging messages with the server
4. Conflicts are resolved automatically using "last-write-wins" at the field level

## Core Components

### 1. Hybrid Logical Clock (HLC) Timestamps

Each change is assigned a globally-unique, monotonic timestamp in the format:

```
{ISO-8601-date}-{counter}-{node-id}
```

Example: `2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912`

Components:
- **ISO-8601 date**: Wall clock time in UTC (millisecond precision)
- **Counter**: 4-digit hex counter (0000-FFFF) for ordering within same millisecond
- **Node ID**: 16-character hex identifier for the client

#### Timestamp Generation (Send)

When creating a local change:

```
function send():
    phys = now()                          # Current wall clock time
    lNew = max(clock.millis, phys)        # Ensure monotonic
    cNew = (lNew == clock.millis) ? clock.counter + 1 : 0

    if (lNew - phys > MAX_DRIFT):         # Default: 5 minutes
        throw ClockDriftError
    if (cNew > 0xFFFF):
        throw OverflowError

    clock.millis = lNew
    clock.counter = cNew
    return Timestamp(lNew, cNew, clock.node)
```

#### Timestamp Reception (Recv)

When receiving a remote change:

```
function recv(msg):
    phys = now()

    if (msg.millis - phys > MAX_DRIFT):
        throw ClockDriftError

    lNew = max(clock.millis, phys, msg.millis)
    cNew = calculateCounter(lNew, clock, msg)

    clock.millis = lNew
    clock.counter = cNew
    return Timestamp(lNew, cNew, clock.node)
```

### 2. Merkle Trie

A Merkle trie is used to efficiently detect which messages need to be synced between clients. The trie is keyed by timestamp (converted to base-3 representation of minutes since epoch).

#### Structure

```typescript
type TrieNode = {
    '0'?: TrieNode;   // Base-3 digit branches
    '1'?: TrieNode;
    '2'?: TrieNode;
    hash?: number;    // XOR of all timestamp hashes in subtree
}
```

#### Operations

**Insert**: Add a timestamp to the trie
```
function insert(trie, timestamp):
    hash = murmurhash3(timestamp.toString())
    key = toBase3(timestamp.millis / 60000)  # Minutes since epoch

    trie.hash = trie.hash XOR hash
    insertKey(trie, key, hash)
```

**Diff**: Find the earliest divergence point between two tries
```
function diff(trie1, trie2):
    if trie1.hash == trie2.hash:
        return null  # In sync

    # Traverse to find earliest different branch
    # Returns timestamp in milliseconds
```

**Prune**: Keep only recent branches to limit trie size
```
function prune(trie, n=2):
    # Keep only the n most recent branches at each level
```

### 3. CRDT Messages

Each change is stored as a message with:

| Field | Type | Description |
|-------|------|-------------|
| timestamp | string | HLC timestamp |
| dataset | string | Table name (e.g., "transactions") |
| row | string | Row ID (UUID) |
| column | string | Field name |
| value | string | JSON-encoded new value |

Messages are immutable and append-only. The current state is derived by replaying messages in timestamp order.

### 4. Encryption

Messages can be encrypted. The envelope contains:

| Field | Type | Description |
|-------|------|-------------|
| timestamp | string | HLC timestamp (always visible) |
| isEncrypted | bool | Whether content is encrypted |
| content | bytes | Message or EncryptedData |

EncryptedData structure:
- `iv`: Initialization vector (bytes)
- `authTag`: Authentication tag (bytes)
- `data`: Encrypted message (bytes)

## Sync Protocol

### Endpoints

| Endpoint | Method | Content-Type | Description |
|----------|--------|--------------|-------------|
| `/sync/sync` | POST | `application/actual-sync` | Exchange CRDT messages |
| `/sync/upload-user-file` | POST | `application/encrypted-file` | Upload budget backup |
| `/sync/download-user-file` | GET | - | Download budget backup |
| `/sync/list-user-files` | GET | - | List available budgets |
| `/account/login` | POST | `application/json` | Authenticate |

### Sync Request (Protobuf)

```protobuf
message SyncRequest {
    repeated MessageEnvelope messages = 1;  // New messages from client
    string fileId = 2;                       // Budget file ID
    string groupId = 3;                      // Sync group ID
    string keyId = 5;                        // Encryption key ID
    string since = 6;                        // HLC timestamp to sync from
}
```

### Sync Response (Protobuf)

```protobuf
message SyncResponse {
    repeated MessageEnvelope messages = 1;  // New messages from server
    string merkle = 2;                       // Server's merkle trie (JSON)
}
```

### Sync Flow

1. Client computes `diff(localMerkle, serverMerkle)` to find sync point
2. Client sends `SyncRequest` with:
   - `since`: The divergence timestamp (or "1970-01-01..." for full sync)
   - `messages`: All local messages since `since`
3. Server responds with:
   - `messages`: All server messages since `since`
   - `merkle`: Updated server merkle trie
4. Client applies received messages and updates local merkle

## Database Schema

### Messages Table

```sql
CREATE TABLE messages_binary (
    timestamp TEXT PRIMARY KEY,
    is_encrypted INTEGER NOT NULL DEFAULT 0,
    content BLOB NOT NULL
);

CREATE TABLE messages_merkles (
    id INTEGER PRIMARY KEY,
    merkle TEXT NOT NULL
);
```

### Clock State

Store the clock state (timestamp + merkle) in local storage. Serialize as:

```json
{
    "timestamp": "2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912",
    "merkle": { "hash": 12345, "0": { ... }, "1": { ... } }
}
```

## Implementation Checklist

Each implementation must provide:

- [ ] `Timestamp` class with `send()`, `recv()`, `parse()`, `toString()`, `hash()`
- [ ] `Merkle` module with `insert()`, `diff()`, `prune()`, `build()`
- [ ] `Clock` management with `getClock()`, `setClock()`, `makeClock()`, `serialize()`, `deserialize()`
- [ ] `SyncClient` with protobuf serialization and HTTP transport
- [ ] Local SQLite storage for messages and merkle state
- [ ] High-level API for accounts, transactions, categories, budgets

## References

- [HLC Paper](http://www.cse.buffalo.edu/tech-reports/2014-04.pdf) - Hybrid Logical Clocks
- [Using CRDTs in the Wild](https://archive.jlongster.com/using-crdts-in-the-wild) - James Long's blog post
- [Original Implementation](https://github.com/actualbudget/actual/tree/master/packages/crdt) - TypeScript reference
