package com.skywatch.skywatch_app.domain.model

data class TimelineEvent(
    val id: String,
    val description: String,
    val timestamp: String?,
    val type: EventType = EventType.MOTION
)

enum class EventType {
    MOTION,
    PERSON_DETECTED,
    THREAT,
    PACKAGE,
    OTHER
}

