@file:Suppress("DEPRECATION")
package com.skywatch.skywatch_app.viewmodel

import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.MediaRepository
import com.skywatch.skywatch_app.domain.repository.TimelineRepository
import com.skywatch.skywatch_app.domain.repository.VideoFeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HomeViewModel(
    private val timelineRepository: TimelineRepository,
    private val videoFeedRepository: VideoFeedRepository,
    private val mediaRepository: MediaRepository,
    private val viewModelScope: CoroutineScope
) : AutoCloseable {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** The currently selected date used for filtering events. */
    private var currentDate: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    
    private var pollingJob: kotlinx.coroutines.Job? = null
    
    init {
        _uiState.value = _uiState.value.copy(
            selectedDate = formatDateForDisplay(currentDate),
            isDemoMode = true
        )
        observeRepositories()
        loadEvents()
        startLivePolling()
    }

    private fun startLivePolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                try {
                    // Refresh current date events for the timeline
                    timelineRepository.getEventsForDate(currentDate.toString())
                    
                    // Check for very recent events (last 10 seconds) for the HUD
                    val latest = _uiState.value.timelineEvents.firstOrNull()
                    if (latest != null) {
                        val now = Clock.System.now().toEpochMilliseconds()
                        val eventTime = latest.timestamp?.toEpochMilliseconds() ?: 0
                        if (now - eventTime < 60000) { // 60 seconds threshold for demo
                            _uiState.value = _uiState.value.copy(latestDetection = latest)
                        } else if (_uiState.value.latestDetection != null) {
                            _uiState.value = _uiState.value.copy(latestDetection = null)
                        }
                    }
                } catch (e: Exception) {
                    // Silent fail for polling
                }
                kotlinx.coroutines.delay(3000) // Poll every 3 seconds
            }
        }
    }

    
    private fun observeRepositories() {
        viewModelScope.launch {
            timelineRepository.getTimelineEvents().collect { events ->
                _uiState.value = _uiState.value.copy(
                    timelineEvents = events,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            videoFeedRepository.getSoundEnabled().collect { soundEnabled ->
                _uiState.value = _uiState.value.copy(isSoundOn = soundEnabled)
            }
        }
        viewModelScope.launch {
            videoFeedRepository.getMuted().collect { muted ->
                _uiState.value = _uiState.value.copy(isMuted = muted)
            }
        }
    }

    /** Initial load / manual pull-to-refresh. */
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                timelineRepository.getEventsForDate(currentDate.toString())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load events"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun refreshTimeline() = loadEvents()
    
    fun toggleSound() {
        viewModelScope.launch {
            videoFeedRepository.toggleSound(!_uiState.value.isSoundOn)
        }
    }
    
    fun toggleMute() {
        viewModelScope.launch {
            videoFeedRepository.toggleMute(!_uiState.value.isMuted)
        }
    }
    
    fun takeScreenshot() {
        viewModelScope.launch {
            mediaRepository.takeScreenshot()
        }
    }
    
    fun startRecording() {
        viewModelScope.launch {
            mediaRepository.startRecording()
            _uiState.value = _uiState.value.copy(isRecording = true)
        }
    }
    
    fun stopRecording() {
        viewModelScope.launch {
            mediaRepository.stopRecording()
            _uiState.value = _uiState.value.copy(isRecording = false)
        }
    }
    
    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun onEventClick(event: TimelineEvent) {
        _uiState.value = _uiState.value.copy(selectedEvent = event)
    }

    fun dismissEventDetails() {
        _uiState.value = _uiState.value.copy(selectedEvent = null)
    }
    
    fun navigateToPreviousDate() {
        currentDate = currentDate.minus(1, DateTimeUnit.DAY)
        _uiState.value = _uiState.value.copy(selectedDate = formatDateForDisplay(currentDate))
        loadEvents()
    }
    
    fun navigateToNextDate() {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        _uiState.value = _uiState.value.copy(selectedDate = formatDateForDisplay(currentDate))
        loadEvents()
    }

    override fun close() {
        viewModelScope.cancel()
    }

    // ── Helpers ──────────────────────────────────────────────

    private fun formatDateForDisplay(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.name.lowercase()
            .replaceFirstChar { it.uppercase() }
        val month = date.month.name.lowercase()
            .replaceFirstChar { it.uppercase() }
        val day = date.dayOfMonth
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        return "$dayOfWeek $day$suffix $month ${date.year}"
    }
}

data class HomeUiState(
    val timelineEvents: List<TimelineEvent> = emptyList(),
    val isSoundOn: Boolean = true,
    val isMuted: Boolean = false,
    val isRecording: Boolean = false,
    val selectedDate: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isDemoMode: Boolean = false,
    val latestDetection: TimelineEvent? = null,
    val selectedEvent: TimelineEvent? = null
)
