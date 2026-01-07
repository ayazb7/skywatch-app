package com.skywatch.skywatch_app.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.TimelineEvent

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
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(TextBlack)
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

