import Foundation

@objc public class Slingshot: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
