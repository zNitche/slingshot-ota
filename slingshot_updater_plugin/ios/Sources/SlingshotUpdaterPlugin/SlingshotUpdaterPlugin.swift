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
    
    private let updaterTickInterval = 5.0;
    
    private var timer: Timer? = nil
    private var mainloopJob: DispatchWorkItem? = nil;
    
    private let implementation = SlingshotUpdater()
    public let pluginMethods: [CAPPluginMethod] = []
    
    override public func load() {
        self.updaterCleanup();
        self.runUpdaterMainloop();
        
        super.load();
    }
    
    private func updaterCleanup() {
        mainloopJob?.cancel();
        mainloopJob = nil;
        
        timer?.invalidate();
        timer = nil;
    }
 
    private func runUpdaterMainloop() {
        if (timer != nil || mainloopJob != nil) {
            return;
        }
        
        mainloopJob = DispatchWorkItem {
            self.timer = Timer.scheduledTimer(withTimeInterval: self.updaterTickInterval, repeats: true) { [self] _ in
                self.implementation.mainloop();
            }}
        
        DispatchQueue.main.async(execute: self.mainloopJob!);
    }
    
    deinit {
        self.updaterCleanup();
    }
}
