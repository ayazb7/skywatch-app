package com.skywatch.skywatch_app.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class TimelineEvent (
    val id: String,
    val description: String,
    val timestamp: Instant?,
    val type: EventType = EventType.MOTION
)

enum class EventType {
    MOTION,
    PERSON_DETECTED,
    THREAT,
    PACKAGE,
    OTHER
}

