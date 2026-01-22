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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.4.4/ActualSync.xcframework.zip",
            checksum: "82449e2fd677892fdd238bc96b986d893b96cb597e3d596af1eac66f8af4a88b"
        ),
    ]
)
