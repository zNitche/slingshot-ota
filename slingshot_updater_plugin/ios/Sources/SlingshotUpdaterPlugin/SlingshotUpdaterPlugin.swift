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
    
    private var timer: Timer? = nil

    private let implementation = SlingshotUpdater()

    public let pluginMethods: [CAPPluginMethod] = [
//        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    
    override public func load() {
        timer?.invalidate();
        
        timer = Timer.scheduledTimer(withTimeInterval: 2.0, repeats: true) { [self] _ in
            self.mainloop();
        }
    }
    
    private func mainloop() {
        print("counter");
    }

    deinit {
        timer?.invalidate();
    }
    
    // @objc func echo(_ call: CAPPluginCall) {
    //     let value = call.getString("value") ?? ""
    //     call.resolve([
    //         "value": implementation.echo(value)
    //     ])
    // }
}
