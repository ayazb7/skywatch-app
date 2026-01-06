package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.model.EventType
import com.skywatch.skywatch_app.domain.model.TimelineEvent
import com.skywatch.skywatch_app.domain.repository.SkyWatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SkyWatchRepositoryImpl : SkyWatchRepository {
    private val _soundEnabled = MutableStateFlow(true)
    private val _muted = MutableStateFlow(false)
    
    private val mockTimelineEvents = listOf(
        TimelineEvent("1", "Motion detected", "21:56 PM", EventType.MOTION),
        TimelineEvent("2", "Daveraj detected", "15:45 PM", EventType.PERSON_DETECTED),
        TimelineEvent("3", "Potential threat", "13:37 PM", EventType.THREAT),
        TimelineEvent("4", "Package delivered", "10:03 AM", EventType.PACKAGE),
        TimelineEvent("5", "Ayaz detected", "08:17 AM", EventType.PERSON_DETECTED)
    )
    
    override fun getTimelineEvents(): Flow<List<TimelineEvent>> {
        return MutableStateFlow(mockTimelineEvents).asStateFlow()
    }
    
    override suspend fun toggleSound(enabled: Boolean) {
        _soundEnabled.value = enabled
    }
    
    override suspend fun toggleMute(muted: Boolean) {
        _muted.value = muted
    }
    
    override suspend fun takeScreenshot() {
        // TODO: Implement screenshot functionality
    }
    
    override suspend fun startRecording() {
        // TODO: Implement recording functionality
    }
    
    override suspend fun stopRecording() {
        // TODO: Implement stop recording functionality
    }
    
    override fun getSoundEnabled(): Flow<Boolean> = _soundEnabled.asStateFlow()
    
    override fun getMuted(): Flow<Boolean> = _muted.asStateFlow()
}

