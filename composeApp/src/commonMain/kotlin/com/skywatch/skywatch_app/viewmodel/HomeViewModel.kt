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

class HomeViewModel(
    private val timelineRepository: TimelineRepository,
    private val videoFeedRepository: VideoFeedRepository,
    private val mediaRepository: MediaRepository,
    private val viewModelScope: CoroutineScope
) : AutoCloseable {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        observeRepositories()
    }
    
    private fun observeRepositories() {
        viewModelScope.launch {
            timelineRepository.getTimelineEvents().collect { events ->
                _uiState.value = _uiState.value.copy(timelineEvents = events)
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
    
    fun navigateToPreviousDate() {
        // TODO: Implement date navigation logic
    }
    
    fun navigateToNextDate() {
        // TODO: Implement date navigation logic
    }
    
    /**
     * Cancels all coroutines and releases resources.
     */
    override fun close() {
        viewModelScope.cancel()
    }
}

data class HomeUiState(
    val timelineEvents: List<TimelineEvent> = emptyList(),
    val isSoundOn: Boolean = true,
    val isMuted: Boolean = false,
    val isRecording: Boolean = false,
    val selectedDate: String = "Friday 12th December 2025"
)

