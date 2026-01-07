package com.skywatch.skywatch_app.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class TimelineEventTest {

    @Test
    fun timelineEvent_createsWithAllProperties() {
        val event = TimelineEvent(
            id = "1",
            description = "Motion detected",
            timestamp = "21:56 PM",
            type = EventType.MOTION
        )

        assertEquals("1", event.id)
        assertEquals("Motion detected", event.description)
        assertEquals("21:56 PM", event.timestamp)
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
            timestamp = "10:00 AM"
        )

        assertEquals(EventType.MOTION, event.type)
    }

    @Test
    fun timelineEvent_equalityWorks() {
        val event1 = TimelineEvent("1", "Motion detected", "21:56 PM", EventType.MOTION)
        val event2 = TimelineEvent("1", "Motion detected", "21:56 PM", EventType.MOTION)
        val event3 = TimelineEvent("2", "Motion detected", "21:56 PM", EventType.MOTION)

        assertEquals(event1, event2)
        assertFalse(event1 == event3)
    }
}

