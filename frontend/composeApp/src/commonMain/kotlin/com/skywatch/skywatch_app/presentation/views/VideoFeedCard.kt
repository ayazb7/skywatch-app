package com.skywatch.skywatch_app.presentation.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.presentation.utils.rememberImageFromUrl

@Composable
fun VideoFeedCard(
    isSoundOn: Boolean,
    onSoundToggle: () -> Unit,
    isDemoMode: Boolean = false,
    latestDetection: TimelineEvent? = null
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Build the full image URL from the screenshot path
    val screenshotUrl = latestDetection?.screenshotUrl?.let { path ->
        if (path.startsWith("http")) path
        else "http://10.0.2.2:8000$path"
    }
    
    val imageBitmap = screenshotUrl?.let { rememberImageFromUrl(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F0F0F))
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        // Doorbell screenshot image (when available)
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Doorbell feed",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Scanning Effect overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            GradientBlue.copy(alpha = scanAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Placeholder content (only when no image)
        if (imageBitmap == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Feed",
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // TOP BAR: Camera info & LIVE dot
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(RecordRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "LIVE",
                color = RecordRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Front Door",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Floating Detection Banner (supporting all event types)
        latestDetection?.let { detection ->
            val bannerColor = when (detection.type) {
                EventType.THREAT -> RecordRed
                EventType.PACKAGE -> Color(0xFFF39C12) // Orange for package
                EventType.PERSON_DETECTED -> GradientBlue
                else -> Color(0xFF27AE60) // Green for motion
            }
            
            val bannerText = when (detection.type) {
                EventType.THREAT -> "THREAT ALERT"
                EventType.PERSON_DETECTED -> "PERSON DETECTED"
                EventType.PACKAGE -> "PACKAGE DETECTED"
                EventType.MOTION -> "MOTION DETECTED"
                else -> "DETECTION ALERT"
            }

            val bannerIcon = when (detection.type) {
                EventType.THREAT -> Icons.Default.Warning
                EventType.PERSON_DETECTED -> Icons.Default.Person
                EventType.PACKAGE -> Icons.Default.Inventory
                else -> Icons.Default.MotionPhotosOn
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 45.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bannerColor.copy(alpha = 0.85f))
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = bannerIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bannerText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sound toggle button
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .clickable { onSoundToggle() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSoundOn) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = if (isSoundOn) "Sound On" else "Sound Off",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
