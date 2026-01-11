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
            url: "https://github.com/Timoteohss/actual-sync/releases/download/v0.2.1/ActualSync.xcframework.zip",
            checksum: "19714700a3baf7e27f9cacf89bdac18051da428b2d6d4deffa19415274209fed"
        ),
    ]
)
