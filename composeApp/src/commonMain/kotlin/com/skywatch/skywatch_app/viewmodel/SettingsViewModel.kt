package com.skywatch.skywatch_app.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

class SettingsViewModel(
    private val viewModelScope: CoroutineScope
) : AutoCloseable {
    override fun close() {
        viewModelScope.cancel()
    }
}