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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.2.2/ActualSync.xcframework.zip",
            checksum: "9574d60ec0d10341d878914c9f4c0c6da6200b35be6d2af9646d0752b3268568"
        ),
    ]
)
