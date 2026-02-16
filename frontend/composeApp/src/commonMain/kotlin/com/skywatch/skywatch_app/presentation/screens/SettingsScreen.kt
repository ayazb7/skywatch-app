package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.skywatch.skywatch_app.di.koinInject
import com.skywatch.skywatch_app.viewmodel.SettingsViewModel
import com.skywatch.skywatch_app.presentation.views.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinInject(),
    onNavigateBack: () -> Unit = {},
    onNavigateToConfigureAI: () -> Unit = {}
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.close()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGrayBackground)
            .safeContentPadding()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextBlack
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // SkyWatch Logo
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    SkyWatchLogo()
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Settings Options
                SettingsOptionRow(
                    icon = Icons.Default.Settings,
                    title = "Configure AI",
                    onClick = onNavigateToConfigureAI
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsOptionRow(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    onClick = { /* TODO: Navigate to notifications */ }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingsOptionRow(
                    icon = Icons.Default.Edit,
                    title = "Appearance",
                    onClick = { /* TODO: Navigate to appearance */ }
                )
            }
        }
    }
}

@Composable
private fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TextBlack,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextBlack,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = TextGray,
            modifier = Modifier.size(20.dp)
        )
    }
}