package com.skywatch.skywatch_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color definitions
private val LightGrayBackground = Color(0xFFF5F5F5)
private val GradientPink = Color(0xFFE91E63)
private val GradientBlue = Color(0xFF2196F3)
private val CardWhite = Color.White
private val TextBlack = Color(0xFF212121)
private val TextGray = Color(0xFF757575)
private val BorderGray = Color(0xFFE0E0E0)
private val RecordRed = Color(0xFFE53935)

@Composable
fun SkyWatchHomePage() {
    var isSoundOn by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Friday 12th December 2025") }

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
                TopNavigationBar()

                Spacer(modifier = Modifier.height(20.dp))

                // Video Feed Card
                VideoFeedCard(
                    isSoundOn = isSoundOn,
                    onSoundToggle = { isSoundOn = !isSoundOn }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Control Buttons Row
                ControlButtonsRow(
                    isMuted = isMuted,
                    onMuteToggle = { isMuted = !isMuted }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Date Scroll Section
                DateScrollSection(
                    selectedDate = selectedDate,
                    onPreviousDate = { /* Handle previous date */ },
                    onNextDate = { /* Handle next date */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Timeline Section
                TimelineSection()
            }
        }
    }
}

@Composable
fun TopNavigationBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Button
        IconButton(onClick = { /* Navigate to home */ }) {
            Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = "Home",
                tint = TextBlack,
                modifier = Modifier.size(28.dp)
            )
        }

        // Sky Watch Logo with gradient
        SkyWatchLogo()

        // Settings Button
        IconButton(onClick = { /* Open settings */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextBlack,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SkyWatchLogo() {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(GradientPink, GradientBlue)
    )

    Text(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    brush = gradientBrush,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            ) {
                append("sky")
            }
            withStyle(
                style = SpanStyle(
                    color = TextBlack,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp
                )
            ) {
                append("watch")
            }
        }
    )
}



@Composable
fun VideoFeedCard(
    isSoundOn: Boolean,
    onSoundToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 10f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF424242))
    ) {
        // Placeholder content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video Feed",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Live Feed",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }

        // Camera name label
        Text(
            text = "Front Door",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        )

        // Sound toggle button
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onSoundToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSoundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = if (isSoundOn) "Sound On" else "Sound Off",
                tint = TextBlack,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ControlButtonsRow(
    isMuted: Boolean,
    onMuteToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Mute Button
        ControlButton(
            icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = if (isMuted) "Unmute" else "Mute",
            onClick = onMuteToggle,
            isActive = isMuted
        )

        // Screenshot Button
        ControlButton(
            icon = Icons.Default.PhotoCamera,
            contentDescription = "Screenshot",
            onClick = { /* Take screenshot */ }
        )

        // Record Button
        ControlButton(
            icon = Icons.Default.FiberManualRecord,
            contentDescription = "Record",
            onClick = { /* Start recording */ },
            iconTint = RecordRed
        )
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    iconTint: Color = TextBlack
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .background(if (isActive) Color(0xFFFFF3F3) else CardWhite)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun DateScrollSection(
    selectedDate: String,
    onPreviousDate: () -> Unit,
    onNextDate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDate) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Previous Date",
                tint = TextGray
            )
        }

        Text(
            text = selectedDate,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextBlack,
            modifier = Modifier
                .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        IconButton(onClick = onNextDate) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Next Date",
                tint = TextGray
            )
        }
    }
}


@Composable
fun TimelineSection() {
    val events = listOf(
        TimelineEvent("Motion detected", "21:56 PM"),
        TimelineEvent("Daveraj detected", "15:45 PM"),
        TimelineEvent("Potential threat", "13:37 PM"),
        TimelineEvent("Package delivered", "10:03 AM"),
        TimelineEvent("Ayaz detected", "08:17 AM")
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        events.forEachIndexed { index, event ->
            TimelineEventItem(
                event = event,
                isLast = index == events.lastIndex
            )
        }
    }
}

data class TimelineEvent(
    val description: String,
    val timestamp: String?
)

@Composable
fun TimelineEventItem(
    event: TimelineEvent,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            // Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(TextBlack)
            )
            // Vertical line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(TextGray.copy(alpha = 0.3f))
                )
            }
        }

        // Event content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = if (isLast) 0.dp else 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = TextBlack
                )
                event.timestamp?.let { timestamp ->
                    Text(
                        text = timestamp,
                        fontSize = 12.sp,
                        color = TextGray,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }

    // Vertical dots between events (if not last)
    if (!isLast) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(TextGray.copy(alpha = 0.5f))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}