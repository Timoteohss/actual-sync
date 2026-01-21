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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.3.4/ActualSync.xcframework.zip",
            checksum: "86c5e1997e7731ac79793c0b0d4c701b8bc03aef3ccc16a45a332ea9d426d867"
        ),
    ]
)
