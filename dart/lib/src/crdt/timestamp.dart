import 'package:uuid/uuid.dart';
import 'murmur_hash.dart';

/// Hybrid Unique Logical Clock (HULC) timestamp.
///
/// Globally-unique, monotonic timestamps are generated from:
/// - Unreliable system time
/// - A counter for ordering within the same millisecond
/// - A node identifier for the current client
///
/// Format: `{ISO-8601}-{counter}-{nodeId}`
/// Example: `2025-04-24T22:23:42.123Z-0001-A219E7A71CC18912`
class Timestamp implements Comparable<Timestamp> {
  final int millis;
  final int counter;
  final String node;

  static const int _maxCounter = 0xFFFF;
  static const int _maxNodeLength = 16;
  static const int _maxDriftMs = 5 * 60 * 1000; // 5 minutes

  static final Timestamp zero = Timestamp(0, 0, '0000000000000000');
  static final Timestamp max =
      Timestamp.parse('9999-12-31T23:59:59.999Z-FFFF-FFFFFFFFFFFFFFFF')!;

  Timestamp(this.millis, this.counter, this.node);

  /// Generate a unique client ID (last 16 hex chars of a UUID).
  static String makeClientId() {
    return const Uuid()
        .v4()
        .replaceAll('-', '')
        .substring(16)
        .toUpperCase();
  }

  /// Parse a timestamp string into a Timestamp object.
  static Timestamp? parse(String timestamp) {
    final parts = timestamp.split('-');
    if (parts.length != 5) return null;

    try {
      final dateStr = '${parts[0]}-${parts[1]}-${parts[2]}';
      final dateTime = DateTime.parse(dateStr);
      final millis = dateTime.millisecondsSinceEpoch;
      final counter = int.parse(parts[3], radix: 16);
      final node = parts[4];

      if (counter > _maxCounter) return null;
      if (node.length > _maxNodeLength) return null;

      return Timestamp(millis, counter, node);
    } catch (_) {
      return null;
    }
  }

  /// Create a timestamp string for querying "since" a given ISO date.
  static String since(String isoString) {
    return '$isoString-0000-0000000000000000';
  }

  @override
  String toString() {
    final dateTime = DateTime.fromMillisecondsSinceEpoch(millis, isUtc: true);
    final isoDate = dateTime.toIso8601String();
    final counterHex = counter.toRadixString(16).toUpperCase().padLeft(4, '0');
    final paddedNode = node.padLeft(16, '0').substring(node.length > 16 ? node.length - 16 : 0);
    return '$isoDate-$counterHex-$paddedNode';
  }

  /// MurmurHash3 of the timestamp string for merkle trie insertion.
  int hash() => MurmurHash3.hash32(toString());

  @override
  int compareTo(Timestamp other) {
    return toString().compareTo(other.toString());
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Timestamp &&
        other.millis == millis &&
        other.counter == counter &&
        other.node == node;
  }

  @override
  int get hashCode => Object.hash(millis, counter, node);
}

/// Mutable clock state for timestamp generation.
class MutableClock {
  int millis;
  int counter;
  final String node;

  static const int _maxDriftMs = 5 * 60 * 1000;
  static const int _maxCounter = 0xFFFF;

  MutableClock({
    this.millis = 0,
    this.counter = 0,
    required this.node,
  });

  factory MutableClock.from(Timestamp timestamp) {
    return MutableClock(
      millis: timestamp.millis,
      counter: timestamp.counter,
      node: timestamp.node,
    );
  }

  /// Generate a new timestamp for a local change (send operation).
  Timestamp send() {
    final phys = DateTime.now().millisecondsSinceEpoch;

    final lNew = millis > phys ? millis : phys;
    final cNew = lNew == millis ? counter + 1 : 0;

    if (lNew - phys > _maxDriftMs) {
      throw ClockDriftError('Drift: ${lNew - phys}ms exceeds max ${_maxDriftMs}ms');
    }
    if (cNew > _maxCounter) {
      throw OverflowError();
    }

    millis = lNew;
    counter = cNew;

    return Timestamp(millis, counter, node);
  }

  /// Receive and merge a timestamp from a remote client.
  Timestamp recv(Timestamp msg) {
    final phys = DateTime.now().millisecondsSinceEpoch;

    if (msg.millis - phys > _maxDriftMs) {
      throw ClockDriftError();
    }

    final lNew = [millis, phys, msg.millis].reduce((a, b) => a > b ? a : b);
    int cNew;

    if (lNew == millis && lNew == msg.millis) {
      cNew = (counter > msg.counter ? counter : msg.counter) + 1;
    } else if (lNew == millis) {
      cNew = counter + 1;
    } else if (lNew == msg.millis) {
      cNew = msg.counter + 1;
    } else {
      cNew = 0;
    }

    if (lNew - phys > _maxDriftMs) {
      throw ClockDriftError();
    }
    if (cNew > _maxCounter) {
      throw OverflowError();
    }

    millis = lNew;
    counter = cNew;

    return Timestamp(millis, counter, node);
  }

  Timestamp toTimestamp() => Timestamp(millis, counter, node);
}

// Error classes
class ClockDriftError implements Exception {
  final String message;
  ClockDriftError([this.message = 'Maximum clock drift exceeded']);
  @override
  String toString() => 'ClockDriftError: $message';
}

class OverflowError implements Exception {
  @override
  String toString() => 'OverflowError: Timestamp counter overflow';
}

class InvalidTimestampError implements Exception {
  final String timestamp;
  InvalidTimestampError(this.timestamp);
  @override
  String toString() => 'InvalidTimestampError: $timestamp';
}
