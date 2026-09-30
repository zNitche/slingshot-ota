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
        super.load();

        self.setupAppStateNotifications();
        
        self.pluignConfig = try? loadPluginConfig()
        self.implementation.configure(pluignConfig: pluignConfig!)
        
        self.updaterCleanup();
        self.runUpdaterMainloop();
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
                    let got_new_update = try await implementation.check_for_update()
                    
                    if (got_new_update) {
                        debugPrint("[SHT] setting up new revision/release")
                        try self.set_base_server_path_for_revision()
                    }
                } catch is CancellationError {
                    debugPrint("[SHT] mainloopPoolingTask cancelled")
                    break
                } catch {
                    debugPrint("[SHT][ERROR] mainloopPoolingTask:", error)
                }
                
                if (Task.isCancelled) {
                    break
                }
                
                do {
                    try await Task.sleep(for: .seconds(pluignConfig?.updaterTickInterval ?? 300))
                } catch {
                    break
                }
            }
        }
    }
    
    private func set_base_server_path_for_revision() throws {
        if (!(try doesRevisionNumberFileExist())) {
            return
        }
        
        let revisionDirURL = try getRevisionDir(type: .current);
        
        let indexPathURL = revisionDirURL.appending(path: "index.html")
        
        guard FileManager.default.fileExists(atPath: indexPathURL.path()) else {
            debugPrint("index.html doesn't exist at ", indexPathURL.path())
            return
        }
        
        self.bridge?.setServerBasePath(revisionDirURL.path())
        UserDefaults.standard.set(revisionDirURL.path(), forKey: "slingshot_revision_path")
    }
}
