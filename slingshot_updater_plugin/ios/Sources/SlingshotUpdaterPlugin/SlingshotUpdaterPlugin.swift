import Foundation
import Capacitor
import UIKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SlingshotUpdaterPlugin)
public class SlingshotUpdaterPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SlingshotUpdaterPlugin"
    public let jsName = "SlingshotUpdater"
    
    private var isInBackground = false;

    private var pluignConfig: SlingshotConfig? = nil;
    
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
        self.setupAppStateNotifications();
            
        self.pluignConfig = try? loadPluginConfig()
        self.implementation.configure(pluignConfig: pluignConfig!)
        
        self.updaterCleanup();
        self.runUpdaterMainloop();
                
        super.load();
    }
    
    private func setupAppStateNotifications() {
        NotificationCenter.default.addObserver(
            forName: UIApplication.willEnterForegroundNotification,
            object: nil,
            queue: .main
        ) { _ in
            self.isInBackground = false
            self.runUpdaterMainloop()
        }

        NotificationCenter.default.addObserver(
            forName: UIApplication.didEnterBackgroundNotification,
            object: nil,
            queue: .main
        ) { _ in
            self.isInBackground = true
            self.updaterCleanup()
        }
    }
    
    private func updaterCleanup() {
        mainloopPoolingTask?.cancel();
        mainloopPoolingTask = nil;
    }
    
    private func runUpdaterMainloop() {
        if (isInBackground == true) {
            return
        }
        
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
                
                try? await Task.sleep(nanoseconds: (pluignConfig?.updaterTickInterval ?? 300) * 1_000_000_000)
            }
        }
    }
    
    deinit {
        self.updaterCleanup();
    }
}
