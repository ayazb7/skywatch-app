package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.data.network.api.EventsApi
import com.skywatch.skywatch_app.data.network.dto.toDomain
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * API-backed implementation of [TimelineRepository].
 *
 * Exposes a [StateFlow] of events that is updated whenever
 * [refreshEvents] or [getEventsForDate] is called.
 */
class TimelineRepositoryImpl(
    private val eventsApi: EventsApi
) : TimelineRepository {

    private val _events = MutableStateFlow<List<TimelineEvent>>(emptyList())

    override fun getTimelineEvents(): Flow<List<TimelineEvent>> = _events.asStateFlow()

    override suspend fun refreshEvents() {
        val dtos = eventsApi.getEvents()
        _events.value = dtos.map { it.toDomain() }
    }

    override suspend fun getEventsForDate(date: String) {
        val dtos = eventsApi.getEvents(date = date)
        _events.value = dtos.map { it.toDomain() }
    }
}
