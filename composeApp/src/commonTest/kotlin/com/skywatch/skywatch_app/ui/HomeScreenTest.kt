package com.skywatch.skywatch_app.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class HomeScreenTest {

    @Test
    fun timelineEvent_createsWithDescriptionAndTimestamp() {
        val event = TimelineEvent("Motion detected", "21:56 PM")

        assertEquals("Motion detected", event.description)
        assertEquals("21:56 PM", event.timestamp)
    }

    @Test
    fun timelineEvent_allowsNullTimestamp() {
        val event = TimelineEvent("Unknown event", null)

        assertEquals("Unknown event", event.description)
        assertNull(event.timestamp)
    }

    @Test
    fun timelineEvent_timestampCanBePresent() {
        val event = TimelineEvent("Package delivered", "10:03 AM")

        assertNotNull(event.timestamp)
    }

    @Test
    fun timelineEvent_differentEventsAreNotEqual() {
        val event1 = TimelineEvent("Motion detected", "21:56 PM")
        val event2 = TimelineEvent("Package delivered", "10:03 AM")

        assertEquals(false, event1 == event2)
    }

    @Test
    fun timelineEvent_sameEventsAreEqual() {
        val event1 = TimelineEvent("Motion detected", "21:56 PM")
        val event2 = TimelineEvent("Motion detected", "21:56 PM")

        assertEquals(event1, event2)
    }
}

