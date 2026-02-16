package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skywatch.skywatch_app.presentation.views.*

/**
 * Configure AI Settings screen - displays a menu of AI configuration options.
 * Each option navigates to its own dedicated screen.
 */
@Composable
fun ConfigureAIScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToFamiliarFaces: () -> Unit = {}
) {
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
                        text = "Configure AI",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextBlack
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable menu options
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ===== FAMILIAR FACES =====
                    ConfigureAIOptionRow(
                        icon = Icons.Default.Person,
                        title = "Familiar Faces",
                        description = "Add people you know so the AI can recognize them",
                        onClick = onNavigateToFamiliarFaces
                    )

                    // ===== PLACEHOLDER FOR FUTURE OPTIONS =====
                    // Add more options here as needed, for example:
                    //
                    // ConfigureAIOptionRow(
                    //     icon = Icons.Default.VolumeUp,
                    //     title = "Voice Settings",
                    //     description = "Configure voice alerts and announcements",
                    //     onClick = onNavigateToVoiceSettings
                    // )
                    //
                    // ConfigureAIOptionRow(
                    //     icon = Icons.Default.Notifications,
                    //     title = "Detection Alerts",
                    //     description = "Configure what triggers alerts",
                    //     onClick = onNavigateToDetectionAlerts
                    // )
                }
            }
        }
    }
}

/**
 * A clickable row for navigating to an AI configuration sub-screen.
 * Use this to add new AI configuration options with consistent styling.
 */
@Composable
private fun ConfigureAIOptionRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGrayBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GradientBlue,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Title and description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }

            // Arrow indicator
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

