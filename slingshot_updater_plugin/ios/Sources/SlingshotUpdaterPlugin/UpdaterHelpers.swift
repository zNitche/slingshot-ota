import Foundation


func doesRevisionNumberFileExist() -> Bool {
    guard let url = try? getSlingshotFilePath(pathItems: ["revision.txt"]) else {
        return false
    }
    
    return FileManager.default.fileExists(atPath: url.path)
}

func writeRevisionNumberToFile(num: String) throws {
    let url = try? getSlingshotFilePath(pathItems: ["revision.txt"])
    try writeToFile(filePath: url!, content: num)
}

func readRevisionNumberFromFile() throws -> String {
    let url = try? getSlingshotFilePath(pathItems: ["revision.txt"])
    return try readFromFile(filePath: url!)
}

func checkIfNewRevisionShouldBeFetched(metadata: RevisionMetadata) throws -> Bool {
    if (doesRevisionNumberFileExist()) {
        let currentRevision = try readRevisionNumberFromFile();
        
        return currentRevision != metadata.sha256sum;
    }
    
    return true;
}

func getAppVersion() -> String {
    return Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String ?? ""
}

func getMetadataApiURL(baseUrl: URL) -> URL {
    let appVersion = getAppVersion()
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
