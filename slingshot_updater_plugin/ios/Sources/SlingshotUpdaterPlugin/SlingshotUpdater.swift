import Foundation

@objc public class SlingshotUpdater: NSObject {
    private let metadataEndpointUrl = URL(string: "http://127.0.0.1:8080/api/v1/health-check");

    @objc public func get_revision_number() -> String {
        return "f4f4WFE";
    }
    
    private func fetchUpdaterMetadata() {
        let urlTask = URLSession.shared.dataTask(with: metadataEndpointUrl!) {data, response, error  in
            print(data);
        }
        
        urlTask.resume();
    }
    
    public func mainloop() {
        do {
            self.fetchUpdaterMetadata();
        } catch {
            print("\(error)");
        }
    }
}
