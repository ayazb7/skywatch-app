package com.skywatch.skywatch_app.presentation.viewmodel

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.MediaRepository
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import com.skywatch.skywatch_app.domain.repository.VideoFeedRepository
import com.skywatch.skywatch_app.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

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
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, videoFeedRepo, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(emptyList<TimelineEvent>(), state.timelineEvents)
        assertTrue(state.isSoundOn)
        assertFalse(state.isMuted)
        assertFalse(state.isRecording)
        assertEquals("Friday 12th December 2025", state.selectedDate)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun observeRepository_updatesTimelineEvents() = runTest(testDispatcher) {
        val testTimestamp = Instant.parse("2025-12-12T10:00:00Z")
        val mockEvents = listOf(
            TimelineEvent("1", "Motion detected", testTimestamp, EventType.MOTION),
            TimelineEvent("2", "Package delivered", testTimestamp, EventType.PACKAGE)
        )
        val (repos, scope) = createMockRepositories(
            timelineEvents = flowOf(mockEvents)
        )
        val (timelineRepo, videoFeedRepo, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.timelineEvents.size)
        assertEquals("Motion detected", state.timelineEvents[0].description)
    }

    @Test
    fun observeRepository_updatesSoundState() = runTest(testDispatcher) {
        val soundEnabledFlow = MutableStateFlow(false)
        val (repos, scope) = createMockRepositories(
            soundEnabled = soundEnabledFlow
        )
        val (timelineRepo, videoFeedRepo, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isSoundOn)

        soundEnabledFlow.value = true
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isSoundOn)
    }

    @Test
    fun observeRepository_updatesMuteState() = runTest(testDispatcher) {
        val mutedFlow = MutableStateFlow(true)
        val (repos, scope) = createMockRepositories(
            muted = mutedFlow
        )
        val (timelineRepo, videoFeedRepo, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)

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

        val videoFeedRepo = object : VideoFeedRepository {
            override fun getVideoStream() = flowOf(ByteArray(0))
            override suspend fun connectToWebhook(url: String) {}
            override suspend fun disconnectFromWebhook() {}
            override suspend fun toggleSound(enabled: Boolean) {
                toggleCalled = true
                lastSoundValue = enabled
            }
            override suspend fun toggleMute(muted: Boolean) {}
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, _, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)
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

        val videoFeedRepo = object : VideoFeedRepository {
            override fun getVideoStream() = flowOf(ByteArray(0))
            override suspend fun connectToWebhook(url: String) {}
            override suspend fun disconnectFromWebhook() {}
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {
                toggleCalled = true
                lastMuteValue = muted
            }
            override fun getSoundEnabled() = MutableStateFlow(true)
            override fun getMuted() = MutableStateFlow(false)
        }
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, _, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)
        advanceUntilIdle()

        viewModel.toggleMute()
        advanceUntilIdle()

        assertTrue(toggleCalled)
        assertTrue(lastMuteValue!!) // Should toggle from false to true
    }

    @Test
    fun startRecording_updatesState() = runTest(testDispatcher) {
        var recordingStarted = false

        val mediaRepo = object : MediaRepository {
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {
                recordingStarted = true
            }
            override suspend fun stopRecording() {}
        }
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, videoFeedRepo, _) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)
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

        val mediaRepo = object : MediaRepository {
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {
                recordingStopped = true
            }
        }
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, videoFeedRepo, _) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)
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
        val (repos, scope) = createMockRepositories()
        val (timelineRepo, videoFeedRepo, mediaRepo) = repos
        val viewModel = HomeViewModel(timelineRepo, videoFeedRepo, mediaRepo, scope)
        advanceUntilIdle()

        val newDate = "Saturday 13th December 2025"
        viewModel.selectDate(newDate)

        assertEquals(newDate, viewModel.uiState.value.selectedDate)
    }

    private fun createMockRepositories(
        timelineEvents: kotlinx.coroutines.flow.Flow<List<TimelineEvent>> = flowOf<List<TimelineEvent>>(emptyList()),
        soundEnabled: kotlinx.coroutines.flow.Flow<Boolean> = MutableStateFlow(true),
        muted: kotlinx.coroutines.flow.Flow<Boolean> = MutableStateFlow(false)
    ): Pair<Triple<TimelineRepository, VideoFeedRepository, MediaRepository>, CoroutineScope> {
        val timelineRepo = object : TimelineRepository {
            override fun getTimelineEvents() = timelineEvents
        }
        val videoFeedRepo = object : VideoFeedRepository {
            override fun getVideoStream() = flowOf(ByteArray(0))
            override suspend fun connectToWebhook(url: String) {}
            override suspend fun disconnectFromWebhook() {}
            override suspend fun toggleSound(enabled: Boolean) {}
            override suspend fun toggleMute(muted: Boolean) {}
            override fun getSoundEnabled() = soundEnabled
            override fun getMuted() = muted
        }
        val mediaRepo = object : MediaRepository {
            override suspend fun takeScreenshot() {}
            override suspend fun startRecording() {}
            override suspend fun stopRecording() {}
        }
        val scope = CoroutineScope(SupervisorJob() + testDispatcher)
        return Pair(Triple(timelineRepo, videoFeedRepo, mediaRepo), scope)
    }
}

