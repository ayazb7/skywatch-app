package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

class TimelineRepositoryImpl : TimelineRepository {
    @OptIn(ExperimentalTime::class)
    private val mockTimelineEvents = listOf(
        TimelineEvent(
            id = "1",
            description = "Motion detected",
            timestamp = Instant.parse("2025-12-12T21:56:00Z"),
            type = EventType.MOTION
        ),
        TimelineEvent(
            id = "2",
            description = "Daveraj detected",
            timestamp = Instant.parse("2025-12-12T15:45:00Z"),
            type = EventType.PERSON_DETECTED
        ),
        TimelineEvent(
            id = "3",
            description = "Potential threat",
            timestamp = Instant.parse("2025-12-12T13:37:00Z"),
            type = EventType.THREAT
        ),
        TimelineEvent(
            id = "4",
            description = "Package delivered",
            timestamp = Instant.parse("2025-12-12T10:03:00Z"),
            type = EventType.PACKAGE
        ),
        TimelineEvent(
            id = "5",
            description = "Ayaz detected",
            timestamp = Instant.parse("2025-12-12T08:17:00Z"),
            type = EventType.PERSON_DETECTED
        )
    )
    
    override fun getTimelineEvents(): Flow<List<TimelineEvent>> {
        return MutableStateFlow(mockTimelineEvents).asStateFlow()
    }
}

