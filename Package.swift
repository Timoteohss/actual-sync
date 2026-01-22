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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.4.2/ActualSync.xcframework.zip",
            checksum: "6b2365e712b27442ee6892c996089537ab1e7308626abfe10f509ad80b0fdcc6"
        ),
    ]
)
