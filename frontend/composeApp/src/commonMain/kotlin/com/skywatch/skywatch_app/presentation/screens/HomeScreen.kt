package com.skywatch.skywatch_app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.di.koinInject
import com.skywatch.skywatch_app.presentation.buttons.ControlButtonsRow
import com.skywatch.skywatch_app.viewmodel.HomeViewModel
import com.skywatch.skywatch_app.presentation.views.*

@OptIn(ExperimentalMaterial3Api::class)
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeContentPadding()
                .padding(horizontal = 16.dp),
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
                onSoundToggle = { viewModel.toggleSound() },
                isDemoMode = uiState.isDemoMode,
                latestDetection = uiState.latestDetection
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

            // Content area: loading / error / timeline
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GradientBlue)
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Something went wrong",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecordRed
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.refreshTimeline() },
                            colors = ButtonDefaults.buttonColors(containerColor = GradientBlue)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                uiState.timelineEvents.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No events recorded",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Everything looks quiet for this day.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray.copy(alpha = 0.7f)
                        )
                    }
                }

                else -> {
                    TimelineSection(
                        events = uiState.timelineEvents,
                        onEventClick = { viewModel.onEventClick(it) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Event Detail Bottom Sheet
        if (uiState.selectedEvent != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.dismissEventDetails() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                EventDetailSheet(
                    event = uiState.selectedEvent!!,
                    onDismiss = { viewModel.dismissEventDetails() }
                )
            }
        }
    }
}
