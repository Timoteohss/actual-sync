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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.3.0/ActualSync.xcframework.zip",
            checksum: "6361e89637ebc2d51a2500053da5d17e7ca10cdc3a9ed04c08540d58cf7ef9b8"
        ),
    ]
)
