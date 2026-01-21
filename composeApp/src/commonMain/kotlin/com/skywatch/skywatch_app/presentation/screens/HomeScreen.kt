package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.skywatch.skywatch_app.di.koinInject
import com.skywatch.skywatch_app.presentation.buttons.ControlButtonsRow
import com.skywatch.skywatch_app.viewmodel.HomeViewModel
import com.skywatch.skywatch_app.presentation.views.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinInject(),
    onNavigateToSettings: () -> Unit = {}
) {
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.close()
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Navigation Bar
                TopNavigationBar(
                    onSettingsClick = onNavigateToSettings
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Video Feed Card
                VideoFeedCard(
                    isSoundOn = uiState.isSoundOn,
                    onSoundToggle = { viewModel.toggleSound() }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Control Buttons Row
                ControlButtonsRow(
                    isMuted = uiState.isMuted,
                    isRecording = uiState.isRecording,
                    onMuteToggle = { viewModel.toggleMute() },
                    onScreenshotClick = { viewModel.takeScreenshot() },
                    onRecordClick = {
                        if (uiState.isRecording) {
                            viewModel.stopRecording()
                        } else {
                            viewModel.startRecording()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date Scroll Section
                DateScrollSection(
                    selectedDate = uiState.selectedDate,
                    onPreviousDate = { viewModel.navigateToPreviousDate() },
                    onNextDate = { viewModel.navigateToNextDate() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Timeline Section
                TimelineSection(events = uiState.timelineEvents)
            }
        }
    }
}

