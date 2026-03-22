package com.skywatch.skywatch_app.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.presentation.utils.formatTimestamp
import kotlin.time.ExperimentalTime

@Composable
fun TimelineSection(
    events: List<TimelineEvent>,
    onEventClick: (TimelineEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        events.forEachIndexed { index, event ->
            TimelineEventItem(
                event = event,
                isLast = index == events.lastIndex,
                onClick = { onEventClick(event) }
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun TimelineEventItem(
    event: TimelineEvent,
    isLast: Boolean,
    onClick: () -> Unit
) {
    // Choose dot colour & icon based on event type / threat status
    val dotColor: Color
    val eventIcon: ImageVector

    when {
        event.isThreat -> {
            dotColor = RecordRed
            eventIcon = Icons.Default.Warning
        }
        event.type == EventType.PERSON_DETECTED -> {
            dotColor = GradientBlue
            eventIcon = Icons.Default.Person
        }
        event.type == EventType.PACKAGE -> {
            dotColor = Color(0xFF4CAF50) // green
            eventIcon = Icons.Default.Inventory2
        }
        else -> {
            dotColor = TextGray
            eventIcon = Icons.AutoMirrored.Filled.DirectionsRun
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        // Timeline indicator column (Simplified for full card look)
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(dotColor)
        )

        // Event Content
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Background
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(dotColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = eventIcon,
                    contentDescription = null,
                    tint = dotColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Info
            Column(modifier = Modifier.weight(1f)) {
                val title = when (event.type) {
                    EventType.PERSON_DETECTED -> event.matchedFaceName ?: "Person Detected"
                    EventType.THREAT -> "Threat Alert"
                    EventType.PACKAGE -> "Package Detected"
                    EventType.MOTION -> "Motion Detected"
                    else -> "Activity Detected"
                }

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.isThreat) RecordRed else TextBlack
                )
                
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = TextGray,
                    maxLines = 1
                )
            }
            
            // Time and Chevron
            Column(horizontalAlignment = Alignment.End) {
                event.timestamp?.let { timestamp ->
                    Text(
                        text = formatTimestamp(timestamp),
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
