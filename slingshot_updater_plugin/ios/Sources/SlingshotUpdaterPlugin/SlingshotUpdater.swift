import Foundation


@objc public class SlingshotUpdater: NSObject {
    private var backendUrlBase: URL? = nil
    
    override init(){
        super.init();
    }
    
    @objc public func get_revision_number() -> String {
        let currentRevision = try? readRevisionNumberFromFile() ?? "";
        
        return currentRevision ?? "";
    }
    
    func configure(pluignConfig: SlingshotConfig) {
        backendUrlBase = URL(string: pluignConfig.url)
    }
    
    private func fetchNewRevision(metadata: RevisionMetadata) async throws {
        let releaseZipDownloadUrl = URL(string: metadata.releaseUrl)!;
        let releaseSignatureDownloadUrl = URL(string: metadata.sigUrl)!;
        
        let revisionFilesURLs = try getRevisionTmpFilesURLs();
        
        let (releaseZipURL, _) = try await URLSession.shared.download(from: releaseZipDownloadUrl)
        let (releaseSigURL, _) = try await URLSession.shared.download(from: releaseSignatureDownloadUrl)
        
        try FileManager.default.moveItem(
            at: releaseZipURL,
            to: revisionFilesURLs.zip
        )
        
        try FileManager.default.moveItem(
            at: releaseSigURL,
            to: revisionFilesURLs.signature
        )
    }
    
    private func validateNewRevision(metadata: RevisionMetadata) throws -> Bool {
        let revisionFilesURLs = try getRevisionTmpFilesURLs();
        
        let signatureValidationResult = try validateFile(fileURL: revisionFilesURLs.zip, signatureURL: revisionFilesURLs.signature)
        let hashValidationResult = try validateFileSHA256(fileURL: revisionFilesURLs.zip, originHash: metadata.sha256sum)
        
        return Bool(hashValidationResult && hashValidationResult)
    }
    
    private func extractReleaseZip() throws {
        let revisionFilesURLs = try getRevisionTmpFilesURLs();
        
        let fileManager = FileManager()
        try fileManager.unzipItem(at: revisionFilesURLs.zip, to: revisionFilesURLs.zip)
    }
    
    private func getRevisionMetadata() async throws -> RevisionMetadata? {
        let targetUrl = getMetadataUrl(baseUrl: self.backendUrlBase!);
        
        let (resData, response) = try await URLSession.shared.data(from: targetUrl);
        let resJson = try JSONSerialization.jsonObject(with: resData) as? [String: Any];

        let new_revision_exists = resJson?["exists"] as? Bool;
        
        if (!(new_revision_exists ?? false)) {
            return nil;
        }
        
        let sha256sum = resJson?["sha256sum"] as? String;
        let releaseUrl = resJson?["release_url"] as? String;
        let sigUrl = resJson?["sig_url"] as? String;
        
        let metadata = RevisionMetadata(sha256sum: sha256sum!,
                                        releaseUrl: releaseUrl!,
                                        sigUrl: sigUrl!);
        
        return metadata;
    }
    
    func mainloop() async throws {
        debugPrint("[SHT] iteration start")

        if (backendUrlBase?.path() == nil) {
            return
        }
        
        debugPrint("[SHT] getting revision metadata")
        
        let metadata = try await self.getRevisionMetadata();
        
        if (metadata == nil) {
            return;
        }
        
        debugPrint("[SHT] got revison metadata")
        
        try Task.checkCancellation()
        
        if (try checkIfNewRevisionShouldBeFetched(metadata: metadata!)) {
            debugPrint("[SHT] fetching new revision")
            
            try removeRevisionDir(type: .tmp);
            try await fetchNewRevision(metadata: metadata!);
            
            if (try validateNewRevision(metadata: metadata!) == false) {
                debugPrint("[SHT] revision rsa validation has failed")
                return;
            }
            
            debugPrint("[SHT] processing revision files")
            
            try Task.checkCancellation()

            try writeRevisionNumberToFile(num: metadata!.sha256sum);
        }
    }
}
