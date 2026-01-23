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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.7.0/ActualSync.xcframework.zip",
            checksum: "381203c6acb7a1699dc2988990f2b730ac5d4bd748d13fe28b5fbc7bf8628750"
        ),
    ]
)
