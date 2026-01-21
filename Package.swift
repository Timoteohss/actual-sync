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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.3.3/ActualSync.xcframework.zip",
            checksum: "1f5db839a907c2aeac9c8d6e714e164c14a83f0cc7b5682fb057d6c78d803792"
        ),
    ]
)
