package com.skywatch.skywatch_app.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class TimelineEvent @OptIn(ExperimentalTime::class) constructor(
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

