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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.6.0/ActualSync.xcframework.zip",
            checksum: "9b4f16b373a1b285dd1e2788e7665a0dc3245b60dd0f1b5bd7dab6374d28d73f"
        ),
    ]
)
