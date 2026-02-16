package com.skywatch.skywatch_app.presentation.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual fun openAppSettings() {
    val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
    if (settingsUrl != null) {
        UIApplication.sharedApplication.openURL(settingsUrl)
    }
}

