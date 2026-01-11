// swift-tools-version:5.9
import PackageDescription
import Foundation

// Check for local XCFramework (for development)
let localFrameworkPath = "kotlin/build/XCFrameworks/release/ActualSync.xcframework"
let useLocalFramework = ProcessInfo.processInfo.environment["USE_LOCAL_XCFRAMEWORK"] == "1" ||
    FileManager.default.fileExists(atPath: localFrameworkPath)

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
        useLocalFramework
            ? .binaryTarget(
                name: "ActualSync",
                path: localFrameworkPath
            )
            : .binaryTarget(
                name: "ActualSync",
                url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.1.0/ActualSync.xcframework.zip",
                checksum: "815e27868f05e28a4338ab914dd7eb40a29e0f63159598c57d5c2f9600f187d8"
            ),
    ]
)
