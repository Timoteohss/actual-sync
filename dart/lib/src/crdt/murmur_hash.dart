import 'dart:convert';
import 'dart:typed_data';

/// MurmurHash3 implementation (32-bit).
/// Used for hashing timestamps in the merkle trie.
class MurmurHash3 {
  static const int _c1 = 0xcc9e2d51;
  static const int _c2 = 0x1b873593;

  static int hash32(String data, [int seed = 0]) {
    return hash32Bytes(utf8.encode(data), seed);
  }

  static int hash32Bytes(List<int> data, [int seed = 0]) {
    final bytes = data is Uint8List ? data : Uint8List.fromList(data);
    final len = bytes.length;
    var h1 = seed;
    final nblocks = len ~/ 4;

    // Body
    for (var i = 0; i < nblocks; i++) {
      final i4 = i * 4;
      var k1 = (bytes[i4] & 0xFF) |
          ((bytes[i4 + 1] & 0xFF) << 8) |
          ((bytes[i4 + 2] & 0xFF) << 16) |
          ((bytes[i4 + 3] & 0xFF) << 24);

      k1 = _multiply32(k1, _c1);
      k1 = _rotl32(k1, 15);
      k1 = _multiply32(k1, _c2);

      h1 ^= k1;
      h1 = _rotl32(h1, 13);
      h1 = _multiply32(h1, 5) + 0xe6546b64;
    }

    // Tail
    final tail = nblocks * 4;
    var k1 = 0;

    switch (len & 3) {
      case 3:
        k1 ^= (bytes[tail + 2] & 0xFF) << 16;
        k1 ^= (bytes[tail + 1] & 0xFF) << 8;
        k1 ^= bytes[tail] & 0xFF;
        k1 = _multiply32(k1, _c1);
        k1 = _rotl32(k1, 15);
        k1 = _multiply32(k1, _c2);
        h1 ^= k1;
        break;
      case 2:
        k1 ^= (bytes[tail + 1] & 0xFF) << 8;
        k1 ^= bytes[tail] & 0xFF;
        k1 = _multiply32(k1, _c1);
        k1 = _rotl32(k1, 15);
        k1 = _multiply32(k1, _c2);
        h1 ^= k1;
        break;
      case 1:
        k1 ^= bytes[tail] & 0xFF;
        k1 = _multiply32(k1, _c1);
        k1 = _rotl32(k1, 15);
        k1 = _multiply32(k1, _c2);
        h1 ^= k1;
        break;
    }

    // Finalization
    h1 ^= len;
    h1 = _fmix32(h1);

    return h1;
  }

  static int _rotl32(int x, int r) {
    return ((x << r) | ((x & 0xFFFFFFFF) >> (32 - r))) & 0xFFFFFFFF;
  }

  static int _multiply32(int a, int b) {
    // Handle 32-bit multiplication overflow
    final aLow = a & 0xFFFF;
    final aHigh = (a >> 16) & 0xFFFF;
    final bLow = b & 0xFFFF;
    final bHigh = (b >> 16) & 0xFFFF;

    final result = (aLow * bLow) + (((aLow * bHigh + aHigh * bLow) & 0xFFFF) << 16);
    return result & 0xFFFFFFFF;
  }

  static int _fmix32(int h) {
    var result = h;
    result ^= (result & 0xFFFFFFFF) >> 16;
    result = _multiply32(result, 0x85ebca6b);
    result ^= (result & 0xFFFFFFFF) >> 13;
    result = _multiply32(result, 0xc2b2ae35);
    result ^= (result & 0xFFFFFFFF) >> 16;
    return result;
  }
}
