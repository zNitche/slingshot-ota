import Foundation


struct SlingshotConfig: Decodable {
    let url: String
    let periodicUpdater: Bool
    let updaterTickInterval: UInt64
    let reloadWebviewOnNewRelease: Bool
}

struct RevisionMetadata {
    let sha256sum: String
    let releaseUrl: String
    let sigUrl: String
}

struct RevisionFilesURLs {
    let zip: URL
    let signature: URL
}

enum RevisionDirectoryType: String {
    case tmp = "tmp"
    case current = "current"
}

struct RevisionDetails: Decodable, Encodable {
    var revisionNumber: String
    var appVersion: String
}
