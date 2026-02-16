package com.skywatch.skywatch_app.presentation.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.skywatch.skywatch_app.ContextProvider

actual fun openAppSettings() {
    val context = ContextProvider.applicationContext
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

