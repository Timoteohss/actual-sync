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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.6.1/ActualSync.xcframework.zip",
            checksum: "906bc32bd3cddb45f6ae0dc68f53f8b9ffdcaab3e3242955be3225825f1f4cea"
        ),
    ]
)
