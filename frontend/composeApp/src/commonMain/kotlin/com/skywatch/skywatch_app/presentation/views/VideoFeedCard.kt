package com.skywatch.skywatch_app.presentation.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.TimelineEvent

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F0F0F))
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        // Scanning Effect (Always on)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            GradientBlue.copy(alpha = scanAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        // Placeholder content
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

        // Floating Detection Banner
        latestDetection?.let { detection ->
            val bannerColor = if (detection.isThreat) RecordRed else GradientBlue
            
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
                        imageVector = if (detection.isThreat) Icons.Default.Warning else Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (detection.isThreat) "THREAT ALERT" else "PERSON DETECTED",
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
