/// Native sync client for Actual Budget.
///
/// This library provides a complete implementation of the Actual Budget
/// CRDT sync protocol for Dart/Flutter applications.
library actual_sync;

// CRDT exports
export 'src/crdt/timestamp.dart';
export 'src/crdt/merkle.dart';
export 'src/crdt/clock.dart';
export 'src/crdt/murmur_hash.dart';

// Protocol exports
export 'src/proto/protobuf.dart';
export 'src/proto/sync_messages.dart';

// Database exports
export 'src/db/database.dart';
export 'src/db/sync_repository.dart';

// Sync exports
export 'src/sync/sync_client.dart';
export 'src/sync/sync_engine.dart';
export 'src/sync/sync_manager.dart';

// API exports
export 'src/api/actual_client.dart';
export 'src/api/models.dart';
