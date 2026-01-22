// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "ActualSync",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(
            name: "ActualSync",
            targets: ["ActualSync"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "ActualSync",
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.5.1/ActualSync.xcframework.zip",
            checksum: "d2c6b79afb914b7e2846269b545266d4b011f76223708e4038d82a970e740c8b"
        ),
    ]
)
