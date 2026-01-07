package com.skywatch.skywatch_app.presentation.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skywatch.skywatch_app.presentation.views.*

@Composable
fun ControlButtonsRow(
    isMuted: Boolean,
    isRecording: Boolean,
    onMuteToggle: () -> Unit,
    onScreenshotClick: () -> Unit,
    onRecordClick: () -> Unit
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
            onClick = onScreenshotClick
        )

        // Record Button
        ControlButton(
            icon = Icons.Default.FiberManualRecord,
            contentDescription = if (isRecording) "Stop Recording" else "Record",
            onClick = onRecordClick,
            isActive = isRecording,
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

