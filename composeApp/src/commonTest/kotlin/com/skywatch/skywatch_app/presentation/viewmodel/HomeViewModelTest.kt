package com.skywatch.skywatch_app.presentation.viewmodel

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.SkyWatchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultValues() = runTest(testDispatcher) {
        val mockRepository = createMockRepository()
        val viewModel = HomeViewModel(mockRepository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(emptyList<TimelineEvent>(), state.timelineEvents)
        assertTrue(state.isSoundOn)
        assertFalse(state.isMuted)
        assertFalse(state.isRecording)
        assertEquals("Friday 12th December 2025", state.selectedDate)
    }

    @Test
    fun observeRepository_updatesTimelineEvents() = runTest(testDispatcher) {
        val mockEvents = listOf(
            TimelineEvent("1", "Motion detected", "21:56 PM", EventType.MOTION),
            TimelineEvent("2", "Package delivered", "10:03 AM", EventType.PACKAGE)
        )
        val mockRepository = createMockRepository(
            timelineEvents = flowOf(mockEvents)
        )
        val viewModel = HomeViewModel(mockRepository)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.timelineEvents.size)
        assertEquals("Motion detected", state.timelineEvents[0].description)
    }

    @Test
    fun observeRepository_updatesSoundState() = runTest(testDispatcher) {
        val soundEnabledFlow = MutableStateFlow(false)
        val mockRepository = createMockRepository(
            soundEnabled = soundEnabledFlow
        )
        val viewModel = HomeViewModel(mockRepository)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSoundOn)

        soundEnabledFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSoundOn)
    }

    @Test
    fun observeRepository_updatesMuteState() = runTest(testDispatcher) {
        val mutedFlow = MutableStateFlow(true)
        val mockRepository = createMockRepository(
            muted = mutedFlow
        )
        val viewModel = HomeViewModel(mockRepository)

        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isMuted)

        mutedFlow.value = false
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isMuted)
    }

    @Test
    fun toggleSound_callsRepository() = runTest(testDispatcher) {
        var toggleCalled = false
        var lastSoundValue: Boolean? = null

        val mockRepository = object : SkyWatchRepository {
            override fun getTimelineEvents() = flowOf<List<TimelineEvent>>(emptyList())
            override suspend fun toggleSound(enabled: Boolean) {
                toggleCalled = true
                lastSoundValue = enabled
            }
            override suspend fun toggleMute(muted: Boolean) {}
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {}
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }

        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        viewModel.toggleSound()
        advanceUntilIdle()

        assertTrue(toggleCalled)
        assertFalse(lastSoundValue!!) // Should toggle from true to false
    }

    @Test
    fun toggleMute_callsRepository() = runTest(testDispatcher) {
        var toggleCalled = false
        var lastMuteValue: Boolean? = null

        val mockRepository = object : SkyWatchRepository {
            override fun getTimelineEvents() = flowOf<List<TimelineEvent>>(emptyList())
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {
                toggleCalled = true
                lastMuteValue = muted
            }
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {}
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }

        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        viewModel.toggleMute()
        advanceUntilIdle()

        assertTrue(toggleCalled)
        assertTrue(lastMuteValue!!) // Should toggle from false to true
    }

    @Test
    fun startRecording_updatesState() = runTest(testDispatcher) {
        var recordingStarted = false

        val mockRepository = object : SkyWatchRepository {
            override fun getTimelineEvents() = flowOf<List<TimelineEvent>>(emptyList())
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {}
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {
                recordingStarted = true
            }
            override suspend fun stopRecording() {}
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }

        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRecording)

        viewModel.startRecording()
        advanceUntilIdle()

        assertTrue(recordingStarted)
        assertTrue(viewModel.uiState.value.isRecording)
    }

    @Test
    fun stopRecording_updatesState() = runTest(testDispatcher) {
        var recordingStopped = false

        val mockRepository = object : SkyWatchRepository {
            override fun getTimelineEvents() = flowOf<List<TimelineEvent>>(emptyList())
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {}
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {
                recordingStopped = true
            }
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }

        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        // Start recording first
        viewModel.startRecording()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isRecording)

        // Stop recording
        viewModel.stopRecording()
        advanceUntilIdle()

        assertTrue(recordingStopped)
        assertFalse(viewModel.uiState.value.isRecording)
    }

    @Test
    fun selectDate_updatesState() = runTest(testDispatcher) {
        val mockRepository = createMockRepository()
        val viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        val newDate = "Saturday 13th December 2025"
        viewModel.selectDate(newDate)

        assertEquals(newDate, viewModel.uiState.value.selectedDate)
    }

    private fun createMockRepository(
        timelineEvents: kotlinx.coroutines.flow.Flow<List<TimelineEvent>> = flowOf<List<TimelineEvent>>(emptyList()),
        soundEnabled: kotlinx.coroutines.flow.Flow<Boolean> = MutableStateFlow(true),
        muted: kotlinx.coroutines.flow.Flow<Boolean> = MutableStateFlow(false)
    ): SkyWatchRepository {
        return object : SkyWatchRepository {
            override fun getTimelineEvents() = timelineEvents
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {}
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {}
            override fun getSoundEnabled() = soundEnabled
            override fun getMuted() = muted
        }
    }
}

