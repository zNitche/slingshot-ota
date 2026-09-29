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
                
        let targetTmpDir = try getRevisionTmpDir(removeOnGet: true)
        let zipDestinationDir = targetTmpDir.appending(path: "release.zip");
        let sigDestinationDir = targetTmpDir.appending(path: "release.zip.sig");
        
        let (releaseZipURL, _) = try await URLSession.shared.download(from: releaseZipDownloadUrl)
        
        let (releaseSigURL, _) = try await URLSession.shared.download(from: releaseSignatureDownloadUrl)
        
        try FileManager.default.moveItem(
                at: releaseZipURL,
                to: zipDestinationDir
            )
        
        try FileManager.default.moveItem(
                at: releaseSigURL,
                to: sigDestinationDir
            )
    }
    
    private func validateNewRevision() throws -> Bool {
        let targetTmpDir = try getRevisionTmpDir(removeOnGet: false)
        let zipDestinationDir = targetTmpDir.appending(path: "release.zip");
        let sigDestinationDir = targetTmpDir.appending(path: "release.zip.sig");
        
        let signatureValidationResult = try validateFile(fileURL: zipDestinationDir, signatureURL: sigDestinationDir)
        
        return signatureValidationResult
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
        if (backendUrlBase?.path() == nil) {
            return
        }
        
        let metadata = try await self.getRevisionMetadata();
        
        if (metadata == nil) {
            return;
        }
        
        if (try checkIfNewRevisionShouldBeFetched(metadata: metadata!)) {
            try await fetchNewRevision(metadata: metadata!);
            
            if (try validateNewRevision() == false) {
                return;
            }

            try writeRevisionNumberToFile(num: metadata!.sha256sum);
        }
    }
}
