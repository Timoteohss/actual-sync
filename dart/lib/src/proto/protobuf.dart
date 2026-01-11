import 'dart:convert';
import 'dart:typed_data';

/// Minimal protobuf encoder/decoder for the Actual Budget sync protocol.
///
/// Wire format:
/// - Each field is: (field_number << 3) | wire_type
/// - Wire type 0: Varint (int32, int64, uint32, uint64, sint32, sint64, bool, enum)
/// - Wire type 2: Length-delimited (string, bytes, embedded messages, packed repeated fields)
class Protobuf {
  static const int wireVarint = 0;
  static const int wireLengthDelimited = 2;

  /// Encode a varint (variable-length integer).
  static Uint8List encodeVarint(int value) {
    if (value == 0) return Uint8List.fromList([0]);

    final result = <int>[];
    var v = value;
    while (v != 0) {
      var b = v & 0x7F;
      v = v >> 7;
      if (v != 0) {
        b |= 0x80;
      }
      result.add(b);
    }
    return Uint8List.fromList(result);
  }

  /// Encode a field tag.
  static Uint8List encodeTag(int fieldNumber, int wireType) {
    return encodeVarint((fieldNumber << 3) | wireType);
  }

  /// Encode a string field.
  static Uint8List encodeString(int fieldNumber, String value) {
    if (value.isEmpty) return Uint8List(0);
    final bytes = utf8.encode(value);
    return Uint8List.fromList([
      ...encodeTag(fieldNumber, wireLengthDelimited),
      ...encodeVarint(bytes.length),
      ...bytes,
    ]);
  }

  /// Encode a bytes field.
  static Uint8List encodeBytes(int fieldNumber, Uint8List value) {
    if (value.isEmpty) return Uint8List(0);
    return Uint8List.fromList([
      ...encodeTag(fieldNumber, wireLengthDelimited),
      ...encodeVarint(value.length),
      ...value,
    ]);
  }

  /// Encode a bool field.
  static Uint8List encodeBool(int fieldNumber, bool value) {
    if (!value) return Uint8List(0);
    return Uint8List.fromList([
      ...encodeTag(fieldNumber, wireVarint),
      1,
    ]);
  }

  /// Encode an embedded message field.
  static Uint8List encodeMessage(int fieldNumber, Uint8List value) {
    if (value.isEmpty) return Uint8List(0);
    return Uint8List.fromList([
      ...encodeTag(fieldNumber, wireLengthDelimited),
      ...encodeVarint(value.length),
      ...value,
    ]);
  }
}

/// Protobuf reader for decoding messages.
class ProtobufReader {
  final Uint8List _data;
  int _pos = 0;

  ProtobufReader(this._data);

  bool get isAtEnd => _pos >= _data.length;

  int readVarint() {
    var result = 0;
    var shift = 0;
    while (_pos < _data.length) {
      final b = _data[_pos++];
      result |= (b & 0x7F) << shift;
      if ((b & 0x80) == 0) break;
      shift += 7;
    }
    return result;
  }

  /// Returns (fieldNumber, wireType)
  (int, int) readTag() {
    final tag = readVarint();
    return (tag >> 3, tag & 0x07);
  }

  Uint8List readBytes() {
    final length = readVarint();
    final result = _data.sublist(_pos, _pos + length);
    _pos += length;
    return result;
  }

  String readString() => utf8.decode(readBytes());

  bool readBool() => readVarint() != 0;

  void skipField(int wireType) {
    switch (wireType) {
      case Protobuf.wireVarint:
        readVarint();
        break;
      case Protobuf.wireLengthDelimited:
        final length = readVarint();
        _pos += length;
        break;
      default:
        throw ArgumentError('Unknown wire type: $wireType');
    }
  }
}

/// Protobuf writer for building messages.
class ProtobufWriter {
  final _buffer = <int>[];

  void writeString(int fieldNumber, String value) {
    _buffer.addAll(Protobuf.encodeString(fieldNumber, value));
  }

  void writeBytes(int fieldNumber, Uint8List value) {
    _buffer.addAll(Protobuf.encodeBytes(fieldNumber, value));
  }

  void writeBool(int fieldNumber, bool value) {
    _buffer.addAll(Protobuf.encodeBool(fieldNumber, value));
  }

  void writeMessage(int fieldNumber, Uint8List value) {
    _buffer.addAll(Protobuf.encodeMessage(fieldNumber, value));
  }

  Uint8List toBytes() => Uint8List.fromList(_buffer);
}
