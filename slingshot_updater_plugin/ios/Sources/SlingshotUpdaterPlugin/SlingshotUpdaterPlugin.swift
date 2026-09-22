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
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = SlingshotUpdater()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
