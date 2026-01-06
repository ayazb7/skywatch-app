package com.skywatch.skywatch_app.domain.repository

import com.skywatch.skywatch_app.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

interface SkyWatchRepository {
    fun getTimelineEvents(): Flow<List<TimelineEvent>>
    suspend fun toggleSound(enabled: Boolean)
    suspend fun toggleMute(muted: Boolean)
    suspend fun takeScreenshot()
    suspend fun startRecording()
    suspend fun stopRecording()
    fun getSoundEnabled(): Flow<Boolean>
    fun getMuted(): Flow<Boolean>
}

