import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SlingshotUpdaterPlugin)
public class SlingshotUpdaterPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SlingshotUpdaterPlugin"
    public let jsName = "SlingshotUpdater"
    
    private let updaterTickInterval = 5;
    
    private var mainloopPoolingTask: Task<Void, Never>? = nil;
    
    private let implementation = SlingshotUpdater()
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "get_revision_number", returnType: CAPPluginReturnPromise)
    ]
    
    @objc func get_revision_number(_ call: CAPPluginCall) {
        call.resolve([
            "value": implementation.get_revision_number()
        ])
    }
    
    override public func load() {
        self.updaterCleanup();
        self.runUpdaterMainloop();
        
        super.load();
    }
    
    private func updaterCleanup() {
        mainloopPoolingTask?.cancel();
        mainloopPoolingTask = nil;
    }
    
    private func runUpdaterMainloop() {
        if (mainloopPoolingTask != nil) {
            return;
        }
        
        mainloopPoolingTask = Task {
            while !Task.isCancelled {
                do {
                    try await implementation.mainloop()
                } catch {
                    print("Error:", error)
                }
                
                try? await Task.sleep(nanoseconds: UInt64(updaterTickInterval * 1_000_000_000))
            }
        }
    }
    
    deinit {
        self.updaterCleanup();
    }
}
