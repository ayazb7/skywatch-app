@file:Suppress("DEPRECATION")
package com.skywatch.skywatch_app.presentation.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Formats an Instant timestamp to a time string.
 * Format: "HH:mm a" (e.g. "10:03 AM")
 */
@OptIn(ExperimentalTime::class)
fun formatTimestamp(timestamp: Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    
    val hour = localDateTime.hour
    val minute = localDateTime.minute
    
    // Determine AM/PM before converting hour to 12-hour format
    val amPm = if (hour < 12) "AM" else "PM"
    
    val hour12 = when {
        hour == 0 -> 12      // Midnight (0) -> 12
        hour > 12 -> hour - 12  // Afternoon (13-23) -> 1-11
        else -> hour         // Morning (1-12) -> 1-12
    }
    
    val minuteStr = minute.toString().padStart(2, '0')
    
    return "$hour12:$minuteStr $amPm"
}

