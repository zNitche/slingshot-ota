import Foundation


func doesRevisionNumberFileExist() -> Bool {
    guard let url = try? getFilePath(pathItems: ["revision.txt"]) else {
        return false
    }

    return FileManager.default.fileExists(atPath: url.path)
}

func writeRevisionNumberToFile(num: String) throws {
    let url = try? getFilePath(pathItems: ["revision.txt"])
    try writeToFile(filePath: url!, content: num)
}

func readRevisionNumberFromFile() throws -> String {
    let url = try? getFilePath(pathItems: ["revision.txt"])
    return try readFromFile(filePath: url!)
}

func checkIfNewRevisionShouldBeFetched(metadata: RevisionMetadata) throws -> Bool {
    if (doesRevisionNumberFileExist()) {
        let currentRevision = try readRevisionNumberFromFile();
                                
        return currentRevision != metadata.sha256sum;
    }
    
    return true;
}
