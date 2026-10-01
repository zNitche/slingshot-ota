import Capacitor

public class SlingshotCustomCAPBridgeViewController: CAPBridgeViewController {
    override public func instanceDescriptor() -> InstanceDescriptor {
        let descriptor = super.instanceDescriptor()
        
        let revisionCode = UserDefaults.standard.string(forKey: "slingshot_revision")
        let revisionExists = (try? checkRevisionDirectory()) ?? false
        
        if (revisionExists && revisionCode != nil) {
            let revisionDirURL = try? getRevisionDir(type: .current)
            
            if (revisionDirURL?.path() != nil) {
                FileManager.default.fileExists(atPath: revisionDirURL!.path(percentEncoded: false))
                descriptor.appLocation = revisionDirURL!
            }
        }
        
        return descriptor
    }
}
