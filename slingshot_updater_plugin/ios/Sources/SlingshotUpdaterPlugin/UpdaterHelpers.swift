import Foundation


func getSlingshotFilePath(pathItems: [String]) throws -> URL {
    var url = try FileManager.default.url(
        for: .applicationSupportDirectory,
        in: .userDomainMask,
        appropriateFor: nil,
        create: true,
    )
    
    url = url.appending(path: "slingshot", directoryHint: .isDirectory)
    
    try? FileManager.default.createDirectory(
        at: url,
        withIntermediateDirectories: true
    )
    
    
    for item in pathItems {
        url = url.appending(path: item, directoryHint: .checkFileSystem)
    }
    
    return url
}

func doesRevisionNumberFileExist() -> Bool {
    guard let url = try? getSlingshotFilePath(pathItems: ["revision.json"]) else {
        return false
    }
    
    return FileManager.default.fileExists(atPath: url.path)
}

func writeRevisionNumberToFile(num: String) throws {
    let url = try? getSlingshotFilePath(pathItems: ["revision.json"])
    
    let appVersion = try getAppVersion()
    
    let data = RevisionDetails(revisionNumber: num, appVersion: appVersion)
    let dataJson = try dumpJsonObject(encodable: data)
    
    try dataJson.write(to: url!)
}

func readRevisionNumberFromFile() throws -> RevisionDetails {
    let url = try getSlingshotFilePath(pathItems: ["revision.json"])
    return try loadJsonObject(fileURL: url, serializable: RevisionDetails.self)
}

func checkIfNewRevisionShouldBeFetched(metadata: RevisionMetadata) throws -> Bool {
    if (doesRevisionNumberFileExist()) {
        let currentRevision = try readRevisionNumberFromFile()
        let appVersion = try getAppVersion()
        
        if (currentRevision.appVersion != appVersion) {
            return true
        }
        
        return currentRevision.revisionNumber != metadata.sha256sum
    }
    
    return true
}

func getAppVersion() throws -> String {
    return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String
}

func getMetadataApiURL(baseUrl: URL) throws -> URL {
    let appVersion = try getAppVersion()
    var url = baseUrl
    
    url = url.appending(path: appVersion)
    url = url.appending(path: "current-revision")
    
    return url
}

func loadPluginConfig() throws -> SlingshotConfig {
    let fileUrl = Bundle.main.url(forResource: "slingshot", withExtension: "json");
    
    let data = try Data(contentsOf: fileUrl!)
    return try JSONDecoder().decode(SlingshotConfig.self, from: data)
}

func getRevisionDir(type: RevisionDirectoryType) throws -> URL {
    var dir = try getSlingshotFilePath(pathItems: [type.rawValue])
    
    try? FileManager.default.createDirectory(
        at: dir,
        withIntermediateDirectories: true
    )
    
    return dir
}

func removeRevisionDir(type: RevisionDirectoryType) throws {
    let url = try getRevisionDir(type: type);
    try? FileManager.default.removeItem(at: url)
}

func getRevisionTmpFilesURLs() throws -> RevisionFilesURLs  {
    let targetTmpDir = try getRevisionDir(type: .tmp)
    
    return RevisionFilesURLs(zip: targetTmpDir.appending(path: "release.zip"),
                             signature: targetTmpDir.appending(path: "release.zip.sig"))
}

func checkRevisionDirectory() throws -> Bool  {
    let revisionDirURL = try getRevisionDir(type: .current)
    
    let indexPathURL = revisionDirURL.appending(path: "index.html")
    
    guard FileManager.default.fileExists(atPath: indexPathURL.path(percentEncoded: false)) else {
        throw NSError(domain: "index.html doesn't exist at " + indexPathURL.path(percentEncoded: false), code: 21)
    }
    
    return true
}
