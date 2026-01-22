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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.4.0/ActualSync.xcframework.zip",
            checksum: "f833448a8f2deffbd4b2cdc97c971ff6a622857ddc04e56cb008e46b09728c6d"
        ),
    ]
)
