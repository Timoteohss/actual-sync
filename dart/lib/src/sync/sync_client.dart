import 'dart:convert';
import 'dart:typed_data';
import 'package:http/http.dart' as http;
import '../crdt/merkle.dart';

/// Client for syncing with Actual Budget server.
class SyncClient {
  final String serverUrl;
  final http.Client _httpClient;

  String? _authToken;
  String? _fileId;
  String? _groupId;

  SyncClient({
    required this.serverUrl,
    http.Client? httpClient,
  }) : _httpClient = httpClient ?? http.Client();

  /// Authenticate with the server.
  Future<void> login(String password) async {
    final response = await _httpClient.post(
      Uri.parse('$serverUrl/account/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'password': password}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      _authToken = data['token'] as String?;
    } else {
      throw SyncException('Login failed: ${response.statusCode}');
    }
  }

  /// List available budget files.
  Future<List<BudgetFile>> listFiles() async {
    final response = await _httpClient.get(
      Uri.parse('$serverUrl/sync/list-user-files'),
      headers: _authHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body) as Map<String, dynamic>;
      final files = data['data'] as List<dynamic>? ?? [];
      return files
          .map((f) => BudgetFile.fromJson(f as Map<String, dynamic>))
          .toList();
    } else {
      throw SyncException('Failed to list files: ${response.statusCode}');
    }
  }

  /// Download a budget file.
  Future<Uint8List> downloadBudget(String syncId) async {
    final response = await _httpClient.get(
      Uri.parse('$serverUrl/sync/download-user-file'),
      headers: {
        ..._authHeaders(),
        'X-ACTUAL-FILE-ID': syncId,
      },
    );

    if (response.statusCode == 200) {
      _fileId = syncId;
      return response.bodyBytes;
    } else {
      throw SyncException('Failed to download budget: ${response.statusCode}');
    }
  }

  /// Sync messages with the server.
  Future<SyncResponse> sync({
    required List<MessageEnvelope> messages,
    required String since,
    required String fileId,
    required String groupId,
    String? keyId,
  }) async {
    // TODO: Implement protobuf serialization
    // 1. Build SyncRequest protobuf
    // 2. POST to /sync/sync with content-type application/actual-sync
    // 3. Parse SyncResponse protobuf
    throw UnimplementedError('Sync not yet implemented');
  }

  /// Upload budget file to server.
  Future<String> uploadBudget({
    required String fileId,
    required String name,
    required Uint8List data,
    String? groupId,
    String? encryptMeta,
  }) async {
    final response = await _httpClient.post(
      Uri.parse('$serverUrl/sync/upload-user-file'),
      headers: {
        ..._authHeaders(),
        'X-ACTUAL-FILE-ID': fileId,
        'X-ACTUAL-NAME': Uri.encodeComponent(name),
        if (groupId != null) 'X-ACTUAL-GROUP-ID': groupId,
        if (encryptMeta != null) 'X-ACTUAL-ENCRYPT-META': encryptMeta,
        'Content-Type': 'application/encrypted-file',
      },
      body: data,
    );

    if (response.statusCode == 200) {
      final result = jsonDecode(response.body) as Map<String, dynamic>;
      return result['groupId'] as String? ?? groupId ?? '';
    } else {
      throw SyncException('Failed to upload budget: ${response.statusCode}');
    }
  }

  Map<String, String> _authHeaders() {
    return {
      if (_authToken != null) 'X-ACTUAL-TOKEN': _authToken!,
    };
  }

  void dispose() {
    _httpClient.close();
  }
}

/// Represents a budget file on the server.
class BudgetFile {
  final String id;
  final String name;
  final String? groupId;
  final String? encryptKeyId;
  final bool deleted;

  BudgetFile({
    required this.id,
    required this.name,
    this.groupId,
    this.encryptKeyId,
    this.deleted = false,
  });

  factory BudgetFile.fromJson(Map<String, dynamic> json) {
    return BudgetFile(
      id: json['fileId'] as String,
      name: json['name'] as String,
      groupId: json['groupId'] as String?,
      encryptKeyId: json['encryptKeyId'] as String?,
      deleted: json['deleted'] == 1 || json['deleted'] == true,
    );
  }
}

/// Envelope for sync messages.
class MessageEnvelope {
  final String timestamp;
  final bool isEncrypted;
  final Uint8List content;

  MessageEnvelope({
    required this.timestamp,
    required this.isEncrypted,
    required this.content,
  });
}

/// Response from sync operation.
class SyncResponse {
  final List<MessageEnvelope> messages;
  final TrieNode merkle;

  SyncResponse({
    required this.messages,
    required this.merkle,
  });
}

/// Exception thrown during sync operations.
class SyncException implements Exception {
  final String message;
  SyncException(this.message);

  @override
  String toString() => 'SyncException: $message';
}
