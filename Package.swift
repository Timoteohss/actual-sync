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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.5.0/ActualSync.xcframework.zip",
            checksum: "ff8ad504dca89edc2730c0e042d7ff2ce5a922b9cc134dba2ea7f3e64969bf8d"
        ),
    ]
)
