package com.skywatch.skywatch_app.presentation.viewmodel

import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.SkyWatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SkyWatchRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        observeRepository()
    }
    
    private fun observeRepository() {
        viewModelScope.launch {
            repository.getTimelineEvents().collect { events ->
                _uiState.value = _uiState.value.copy(timelineEvents = events)
            }
        }
        viewModelScope.launch {
            repository.getSoundEnabled().collect { soundEnabled ->
                _uiState.value = _uiState.value.copy(isSoundOn = soundEnabled)
            }
        }
        viewModelScope.launch {
            repository.getMuted().collect { muted ->
                _uiState.value = _uiState.value.copy(isMuted = muted)
            }
        }
    }
    
    fun toggleSound() {
        viewModelScope.launch {
            repository.toggleSound(!_uiState.value.isSoundOn)
        }
    }
    
    fun toggleMute() {
        viewModelScope.launch {
            repository.toggleMute(!_uiState.value.isMuted)
        }
    }
    
    fun takeScreenshot() {
        viewModelScope.launch {
            repository.takeScreenshot()
        }
    }
    
    fun startRecording() {
        viewModelScope.launch {
            repository.startRecording()
            _uiState.value = _uiState.value.copy(isRecording = true)
        }
    }
    
    fun stopRecording() {
        viewModelScope.launch {
            repository.stopRecording()
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
}

data class HomeUiState(
    val timelineEvents: List<TimelineEvent> = emptyList(),
    val isSoundOn: Boolean = true,
    val isMuted: Boolean = false,
    val isRecording: Boolean = false,
    val selectedDate: String = "Friday 12th December 2025"
)

