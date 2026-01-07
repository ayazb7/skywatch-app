package com.skywatch.skywatch_app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.skywatch.skywatch_app.di.ProvideKoin
import com.skywatch.skywatch_app.presentation.screens.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        ProvideKoin {
            HomeScreen()
        }
    }
}