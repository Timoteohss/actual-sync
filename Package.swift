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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.8.0/ActualSync.xcframework.zip",
            checksum: "6cb379b5e1b3a9a5cd269e74cdfaf9c9a85e94f9b2df5df8216a720a3ae96f59"
        ),
    ]
)
