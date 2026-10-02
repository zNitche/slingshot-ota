import Capacitor

public class SlingshotCustomCAPBridgeViewController: CAPBridgeViewController {
    override public func instanceDescriptor() -> InstanceDescriptor {
        let descriptor = super.instanceDescriptor()
        UserDefaults.standard.set(descriptor.appLocation, forKey: SLINGSHOT_CAPACITOR_DEFAULT_SERVER_PATH_KEY)
        
        let revisionCode = UserDefaults.standard.string(forKey: SLINGSHOT_REVISION_KEY)
        
        if (revisionCode == nil) {
            return descriptor
        }
        
        let revisionExists = (try? checkRevisionDirectory()) ?? false
        
        if (revisionExists) {
            let revisionDirURL = try? getRevisionDir(type: .current)
            
            if (revisionDirURL?.path() != nil) {
                FileManager.default.fileExists(atPath: revisionDirURL!.path(percentEncoded: false))
                descriptor.appLocation = revisionDirURL!
            }
        }
        
        return descriptor
    }
}
