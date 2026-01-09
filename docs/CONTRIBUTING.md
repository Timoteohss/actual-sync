# Contributing to actual-sync

Thank you for your interest in contributing to actual-sync!

## Getting Started

1. Fork the repository
2. Clone your fork
3. Create a branch for your changes

## Project Structure

```
actual-sync/
├── docs/           # Shared protocol documentation
├── kotlin/         # Kotlin Multiplatform implementation
├── dart/           # Dart/Flutter implementation
└── .github/        # CI/CD workflows
```

## Adding a New Platform Implementation

1. Create a new directory for your platform
2. Follow the protocol specification in [docs/PROTOCOL.md](./PROTOCOL.md)
3. Use the protobuf definitions in [docs/schemas/sync.proto](./schemas/sync.proto)
4. Implement all required components (see checklist in PROTOCOL.md)
5. Add tests that verify compatibility
6. Add a CI workflow in `.github/workflows/`
7. Update the root README.md

## Code Style

Each platform should follow its idiomatic conventions:

- **Kotlin**: Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Dart**: Follow [Effective Dart](https://dart.dev/guides/language/effective-dart)
- **Swift**: Follow [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- **Rust**: Use `rustfmt` and follow Rust conventions
- **C#**: Follow [.NET Coding Conventions](https://docs.microsoft.com/en-us/dotnet/csharp/fundamentals/coding-style/coding-conventions)

## Testing

All implementations must include:

1. **Unit tests** for CRDT components (Timestamp, Merkle)
2. **Integration tests** for sync operations
3. **Compatibility tests** against the reference TypeScript implementation

## Pull Requests

1. Ensure all tests pass
2. Update documentation if needed
3. Add a clear description of your changes
4. Reference any related issues

## Questions?

Open an issue or start a discussion!
