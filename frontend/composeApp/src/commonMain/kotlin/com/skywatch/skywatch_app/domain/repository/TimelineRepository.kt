package com.skywatch.skywatch_app.domain.repository

import com.skywatch.skywatch_app.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

interface TimelineRepository {
    fun getTimelineEvents(): Flow<List<TimelineEvent>>

    /** Re-fetch events from the backend. */
    suspend fun refreshEvents()

    /** Fetch events filtered to a specific date (YYYY-MM-DD). */
    suspend fun getEventsForDate(date: String)
}
