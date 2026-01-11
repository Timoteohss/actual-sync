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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.1.0/ActualSync.xcframework.zip",
            checksum: "00798de8a4a5f8569dbb99482a88ac264fe1c7d669a90b8bf3790b1d06d0369c"
        ),
    ]
)
