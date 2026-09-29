import Foundation
import Security
import CryptoKit

func loadPublicRsaKey() throws -> SecKey {
    let fileUrl = Bundle.main.url(forResource: "slingshot", withExtension: "pem");
    let fileContent = try readFromFile(filePath: fileUrl!)
    
    let key = SecKeyCreateWithData(
        Data(base64Encoded: fileContent
            .replacingOccurrences(of: "-----BEGIN PUBLIC KEY-----", with: "")
            .replacingOccurrences(of: "-----END PUBLIC KEY-----", with: "")
            .components(separatedBy: .whitespacesAndNewlines)
            .joined())! as CFData,
        [kSecAttrKeyType: kSecAttrKeyTypeRSA, kSecAttrKeyClass: kSecAttrKeyClassPublic] as CFDictionary,
        nil
    )
    
    return key!
    
}

func validateFile(fileURL: URL, signatureURL: URL) throws -> Bool {
    let pubKey = try loadPublicRsaKey()
    let fileSignature = try Data(contentsOf: signatureURL)
    
    let signatureValidationResult = try validateFileSignature(fileURL: fileURL, signature: fileSignature, publicKey: pubKey)
    
    return signatureValidationResult;
}

func validateFileSignature(
    fileURL: URL,
    signature: Data,
    publicKey: SecKey
) throws -> Bool {
    let fileData = try Data(contentsOf: fileURL)
    
    let isAlgoSupported = SecKeyIsAlgorithmSupported(
        publicKey,
        .verify,
        .rsaSignatureMessagePKCS1v15SHA256)
    
    if (isAlgoSupported == false) {
        return false
    }
    
    return SecKeyVerifySignature(
        publicKey,
        .rsaSignatureMessagePKCS1v15SHA256,
        fileData as CFData,
        signature as CFData,
        nil
    )
}

func validateFileSHA256(fileURL: URL, originHash: String) throws -> Bool {
    let fileData = try Data(contentsOf: fileURL)
    let sha256sum = SHA256.hash(data: fileData)
    
    let localFileHashString = sha256sum
        .map { String(format: "%02x", $0) }
        .joined()
    
    return localFileHashString.caseInsensitiveCompare(originHash.trimmingCharacters(in: .whitespacesAndNewlines)) == .orderedSame
}
