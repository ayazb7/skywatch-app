package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SkyWatchRepositoryImplTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val repository = SkyWatchRepositoryImpl()

    @Test
    fun getTimelineEvents_returnsExpectedEvents() = runTest {
        val events = repository.getTimelineEvents().first()

        assertEquals(5, events.size)
        assertEquals("Motion detected", events[0].description)
        assertEquals("21:56 PM", events[0].timestamp)
        assertEquals(EventType.MOTION, events[0].type)
    }

    @Test
    fun toggleSound_updatesSoundState() = runTest {
        // Initially sound should be enabled
        assertTrue(repository.getSoundEnabled().first())

        // Toggle to disabled
        repository.toggleSound(false)
        assertFalse(repository.getSoundEnabled().first())

        // Toggle back to enabled
        repository.toggleSound(true)
        assertTrue(repository.getSoundEnabled().first())
    }

    @Test
    fun toggleMute_updatesMuteState() = runTest {
        // Initially should not be muted
        assertFalse(repository.getMuted().first())

        // Toggle to muted
        repository.toggleMute(true)
        assertTrue(repository.getMuted().first())

        // Toggle back to unmuted
        repository.toggleMute(false)
        assertFalse(repository.getMuted().first())
    }

}

