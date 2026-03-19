package com.skywatch.skywatch_app.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.presentation.utils.formatTimestamp
import kotlin.time.ExperimentalTime

@Composable
fun TimelineSection(events: List<TimelineEvent>) {
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

@OptIn(ExperimentalTime::class)
@Composable
fun TimelineEventItem(
    event: TimelineEvent,
    isLast: Boolean
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
            dotColor = TextBlack
            eventIcon = Icons.AutoMirrored.Filled.DirectionsRun
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline indicator column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(TextGray.copy(alpha = 0.3f))
                )
            }
        }

        // Event card
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = if (isLast) 8.dp else 16.dp)
        ) {
            val borderColor = if (event.isThreat) RecordRed.copy(alpha = 0.6f) else BorderGray
            val bgColor = if (event.isThreat) RecordRed.copy(alpha = 0.05f) else CardWhite

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(bgColor, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(dotColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = eventIcon,
                            contentDescription = null,
                            tint = dotColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.description,
                            fontSize = 14.sp,
                            color = TextBlack,
                            lineHeight = 18.sp
                        )
                        if (event.isThreat && !event.threatExplanation.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = event.threatExplanation,
                                fontSize = 12.sp,
                                color = RecordRed,
                                lineHeight = 16.sp,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    event.timestamp?.let { timestamp ->
                        Text(
                            text = formatTimestamp(timestamp),
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}
