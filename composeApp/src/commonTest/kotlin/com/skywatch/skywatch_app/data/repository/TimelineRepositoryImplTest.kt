package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.EventType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineRepositoryImplTest {

    private val repository = TimelineRepositoryImpl()

    @Test
    fun getTimelineEvents_returnsExpectedEvents() = runTest {
        val events = repository.getTimelineEvents().first()

        assertEquals(5, events.size)
        assertEquals("Motion detected", events[0].description)
        assertEquals(EventType.MOTION, events[0].type)
        assertEquals("Daveraj detected", events[1].description)
        assertEquals(EventType.PERSON_DETECTED, events[1].type)
    }
}

