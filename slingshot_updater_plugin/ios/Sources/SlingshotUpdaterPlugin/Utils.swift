import Foundation


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

func loadJsonObject<T>(fileURL: URL, serializable: T.Type) throws -> T where T : Decodable {
    let data = try Data(contentsOf: fileURL)
    return try JSONDecoder().decode(serializable.self, from: data)
}

func dumpJsonObject<T>(encodable: T) throws -> Data where T : Encodable {
    return try JSONEncoder().encode(encodable)
}
