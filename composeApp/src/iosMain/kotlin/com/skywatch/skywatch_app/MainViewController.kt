package com.skywatch.skywatch_app

import androidx.compose.ui.window.ComposeUIViewController
import com.skywatch.skywatch_app.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}