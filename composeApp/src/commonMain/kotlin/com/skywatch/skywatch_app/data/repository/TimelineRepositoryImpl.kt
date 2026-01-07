package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimelineRepositoryImpl : TimelineRepository {
    private val mockTimelineEvents = listOf(
        TimelineEvent("1", "Motion detected", "21:56 PM", EventType.MOTION),
        TimelineEvent("2", "Daveraj detected", "15:45 PM", EventType.PERSON_DETECTED),
        TimelineEvent("3", "Potential threat", "13:37 PM", EventType.THREAT),
        TimelineEvent("4", "Package delivered", "10:03 AM", EventType.PACKAGE),
        TimelineEvent("5", "Ayaz detected", "08:17 AM", EventType.PERSON_DETECTED)
    )
    
    override fun getTimelineEvents(): Flow<List<TimelineEvent>> {
        return MutableStateFlow(mockTimelineEvents).asStateFlow()
    }
}

