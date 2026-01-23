package com.skywatch.skywatch_app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimelineEventTest {

    private val testTimestamp = Instant.parse("2025-12-12T10:00:00Z")

    @Test
    fun timelineEvent_createsWithAllProperties() {
        val event = TimelineEvent(
            id = "1",
            description = "Motion detected",
            timestamp = testTimestamp,
            type = EventType.MOTION
        )

        assertEquals("1", event.id)
        assertEquals("Motion detected", event.description)
        assertEquals(testTimestamp, event.timestamp)
        assertEquals(EventType.MOTION, event.type)
    }

    @Test
    fun timelineEvent_allowsNullTimestamp() {
        val event = TimelineEvent(
            id = "2",
            description = "Unknown event",
            timestamp = null,
            type = EventType.OTHER
        )

        assertEquals("2", event.id)
        assertEquals("Unknown event", event.description)
        assertNull(event.timestamp)
        assertEquals(EventType.OTHER, event.type)
    }

    @Test
    fun timelineEvent_usesDefaultEventType() {
        val event = TimelineEvent(
            id = "3",
            description = "Some event",
            timestamp = testTimestamp
        )

        assertEquals(EventType.MOTION, event.type)
    }

    @Test
    fun timelineEvent_equalityWorks() {
        val event1 = TimelineEvent("1", "Motion detected", testTimestamp, EventType.MOTION)
        val event2 = TimelineEvent("1", "Motion detected", testTimestamp, EventType.MOTION)
        val event3 = TimelineEvent("2", "Motion detected", testTimestamp, EventType.MOTION)

        assertEquals(event1, event2)
        assertFalse(event1 == event3)
    }
}

