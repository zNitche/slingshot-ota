import Capacitor

public class SlingshotCustomCAPBridgeViewController: CAPBridgeViewController {
    override public func instanceDescriptor() -> InstanceDescriptor {
        let descriptor = super.instanceDescriptor()
        
        let revision_path = UserDefaults.standard.string(forKey: "slingshot_revision_path")
        
        if (revision_path != nil) {
            let url = URL(string: revision_path!)
            
            if (url != nil) {
                FileManager.default.fileExists(atPath: url!.path())
                descriptor.appLocation = url!
            }
        }
        
        return descriptor
    }
}
