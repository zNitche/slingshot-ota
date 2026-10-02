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
            "value": try? implementation.get_revision_number() ?? ""
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
                    try? validateRevisionForCurrentVersion();
                    
                    let got_new_update = try await implementation.check_for_update()
                    
                    if (got_new_update) {
                        debugPrint("[SHT] setting up new revision/release")
                        try self.set_slingshot_revision()
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
    
    private func reloadWebView() {
        if (pluignConfig?.reloadWebviewOnNewRelease ?? false) {
            debugPrint("[SHT] reloading webview")
            
            DispatchQueue.main.async {
                self.bridge?.webView?.reload()
            }
        }
    }
    
    private func validateRevisionForCurrentVersion() throws {
        let currentRevision = try readRevisionNumberFromFile()
        let appVersion = try getAppVersion()
        
        if (currentRevision.appVersion == appVersion) {
            return
        }
        
        debugPrint("[SHT] current revision target app version doesn't match app version, removing")
        
        UserDefaults.standard.removeObject(forKey: SLINGSHOT_REVISION_KEY)
        
        try? removeRevisionDir(type: .current)
        try? removeRevisionDir(type: .tmp)
        
        let revisionJsonURL = try? getSlingshotFilePath(pathItems: ["revision.json"])
        if (revisionJsonURL != nil) {
            try? FileManager.default.removeItem(at: revisionJsonURL!)
        }
        
        let capacitorDefaultURL = UserDefaults.standard.string(forKey: SLINGSHOT_CAPACITOR_DEFAULT_SERVER_PATH_KEY)
        
        if (capacitorDefaultURL == nil) {
            return
        }
        
        self.bridge?.setServerBasePath(capacitorDefaultURL!)
        reloadWebView()
    }
    
    private func set_slingshot_revision() throws {
        if (!(try doesRevisionNumberFileExist())) {
            return
        }
        
        let revisionNumber = try readRevisionNumberFromFile()
        try checkRevisionDirectory()
        
        UserDefaults.standard.set(revisionNumber.revisionNumber, forKey: SLINGSHOT_REVISION_KEY)
        
        let revisionDirURL = try getRevisionDir(type: .current)
        self.bridge?.setServerBasePath(revisionDirURL.path(percentEncoded: false))
        
        reloadWebView()
    }
}
