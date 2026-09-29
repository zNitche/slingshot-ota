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
        print("fetch new");
        
        let releaseZipDownloadUrl = URL(string: metadata.releaseUrl)!;
        let releaseSignatureDownloadUrl = URL(string: metadata.sigUrl)!;
        
        let targetDir = try FileManager.default.url(
                for: .applicationSupportDirectory,
                in: .userDomainMask,
                appropriateFor: nil,
                create: true
            )
        
        let destinationDir = targetDir.appendingPathComponent("release.zip");
        
        let (temporaryURL, response) = try await URLSession.shared.download(from: releaseZipDownloadUrl)
        
        try FileManager.default.moveItem(
                at: temporaryURL,
                to: destinationDir
            )
                
        try writeRevisionNumberToFile(num: metadata.sha256sum);
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
        }
    }
}
