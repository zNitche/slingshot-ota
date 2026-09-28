import Foundation


func getFilePath(pathItems: [String]) throws -> URL {
    var url = try FileManager.default.url(
        for: .applicationSupportDirectory,
        in: .userDomainMask,
        appropriateFor: nil,
        create: true,
    )
    
    for item in pathItems {
        url = url.appending(path: item, directoryHint: .checkFileSystem);
    }
    
    return url;
}

func writeToFile(filePath: URL, content: String) throws {
    try content.write(
        to: filePath,
        atomically: true,
        encoding: .utf8
    )
}

func readFromFile(filePath: URL) throws -> String {
    return try String(contentsOf: filePath, encoding: .utf8)
}
