// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "SlingshotUpdater",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "SlingshotUpdater",
            targets: ["SlingshotUpdaterPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", exact: "8.5.2"),
        .package(url: "https://github.com/weichsel/ZIPFoundation.git", exact: "0.9.20"),
    ],
    targets: [
        .target(
            name: "SlingshotUpdaterPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm"),
                .product(name: "ZIPFoundation", package: "ZIPFoundation"),
            ],
            path: "ios/Sources/SlingshotUpdaterPlugin"),
    ]
)
