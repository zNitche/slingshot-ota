struct SlingshotConfig: Decodable {
    let url: String
    let updaterTickInterval: UInt64
}


struct RevisionMetadata {
    let sha256sum: String
    let releaseUrl: String
    let sigUrl: String
}
