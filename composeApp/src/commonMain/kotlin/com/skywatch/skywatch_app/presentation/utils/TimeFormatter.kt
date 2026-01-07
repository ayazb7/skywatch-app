package com.skywatch.skywatch_app.presentation.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Formats an Instant timestamp to a time string.
 * Format: "HH:mm a" (e.g. "10:03 AM")
 */
fun formatTimestamp(timestamp: Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    
    val amPm = if (hour < 12) "AM" else "PM"
    val minuteStr = minute.toString().padStart(2, '0')
    
    return "$hour12:$minuteStr $amPm"
}

