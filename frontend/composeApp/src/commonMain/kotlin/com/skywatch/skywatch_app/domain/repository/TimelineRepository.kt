package com.skywatch.skywatch_app.domain.repository

import com.skywatch.skywatch_app.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

interface TimelineRepository {
    fun getTimelineEvents(): Flow<List<TimelineEvent>>
}

