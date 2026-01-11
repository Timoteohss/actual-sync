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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.2.0/ActualSync.xcframework.zip",
            checksum: "98f83954498a576dc7138f86ec7c6c380289a314ec5e88ef9e7f0ffbba086c16"
        ),
    ]
)
