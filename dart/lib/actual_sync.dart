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

// Sync exports
export 'src/sync/sync_client.dart';

// API exports
export 'src/api/actual_client.dart';
export 'src/api/models.dart';
