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
            checksum: "815e27868f05e28a4338ab914dd7eb40a29e0f63159598c57d5c2f9600f187d8"
        ),
    ]
)
