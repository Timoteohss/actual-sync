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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.4.1/ActualSync.xcframework.zip",
            checksum: "0155d85eeca9440720f5198b2686dac86aa19703f8b073a8d0518334aa158dd4"
        ),
    ]
)
