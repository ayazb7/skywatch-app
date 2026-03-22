package com.skywatch.skywatch_app.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.presentation.utils.formatTimestamp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3Api::class)
@Composable
fun EventDetailSheet(
    event: TimelineEvent,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = when (event.type) {
                        EventType.PERSON_DETECTED -> "Person Identified"
                        EventType.THREAT -> "Threat Alert"
                        EventType.PACKAGE -> "Package Delivery"
                        else -> "Activity Detail"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.isThreat) RecordRed else TextBlack
                )
                event.timestamp?.let {
                    Text(
                        text = formatTimestamp(it),
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            }
            
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content based on type
        when (event.type) {
            EventType.PERSON_DETECTED -> {
                PersonDetailSection(event)
            }
            EventType.THREAT -> {
                ThreatDetailSection(event)
            }
            else -> {
                DefaultDetailSection(event)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { /* View Video clip */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GradientBlue)
            ) {
                Text("View Clip", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            OutlinedButton(
                onClick = { /* Share */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Share", color = TextGray)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PersonDetailSection(event: TimelineEvent) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(LightGrayBackground)
                .border(2.dp, BorderGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!event.matchedFaceImageUrl.isNullOrBlank()) {
                val imageUrl = if (event.matchedFaceImageUrl.startsWith("http")) {
                    event.matchedFaceImageUrl
                } else {
                    "http://10.0.2.2:8000${event.matchedFaceImageUrl}"
                }
                
                KamelImage(
                    resource = asyncPainterResource(imageUrl),
                    contentDescription = event.matchedFaceName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = TextGray)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = event.matchedFaceName ?: "Unrecognized Person",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Text(
            text = if (event.matchedFaceName != null) "Known Individual" else "Unknown Visitor",
            fontSize = 14.sp,
            color = TextGray
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightGrayBackground.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Padding(16.dp) {
                Text(
                    text = event.description,
                    fontSize = 14.sp,
                    color = TextBlack,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ThreatDetailSection(event: TimelineEvent) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(RecordRed.copy(alpha = 0.1f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = RecordRed)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Threat Confidence: ${event.threatConfidence}", fontWeight = FontWeight.Bold, color = RecordRed, fontSize = 14.sp)
                    Text("Security protocol initiated", color = RecordRed, fontSize = 12.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Text("AI Analysis", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.threatExplanation ?: "No detailed explanation available.",
            fontSize = 14.sp,
            color = TextBlack,
            lineHeight = 22.sp
        )
        
        if (!event.description.contains("Threat")) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Context: ${event.description}",
                fontSize = 13.sp,
                color = TextGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun DefaultDetailSection(event: TimelineEvent) {
    Column {
        Text(
            text = event.description,
            fontSize = 15.sp,
            color = TextBlack,
            lineHeight = 22.sp
        )
        
        if (!event.threatExplanation.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Additional Info", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = event.threatExplanation, fontSize = 14.sp, color = TextGray)
        }
    }
}

@Composable
fun Padding(all: androidx.compose.ui.unit.Dp, content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(all)) {
        content()
    }
}
