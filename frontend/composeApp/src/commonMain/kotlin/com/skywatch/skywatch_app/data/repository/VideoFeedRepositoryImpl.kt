package com.skywatch.skywatch_app.data.repository

import com.skywatch.skywatch_app.domain.repository.VideoFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VideoFeedRepositoryImpl : VideoFeedRepository {
    private val _soundEnabled = MutableStateFlow(true)
    private val _muted = MutableStateFlow(false)
    
    override fun getVideoStream(): Flow<ByteArray> {
        // TODO: Implement webhook video stream when webhook integration is added
        return MutableStateFlow(ByteArray(0)).asStateFlow()
    }
    
    override suspend fun connectToWebhook(url: String) {
        // TODO: Implement webhook connection when webhook integration is added
    }
    
    override suspend fun disconnectFromWebhook() {
        // TODO: Implement webhook disconnection when webhook integration is added
    }
    
    override suspend fun toggleSound(enabled: Boolean) {
        _soundEnabled.value = enabled
    }
    
    override suspend fun toggleMute(muted: Boolean) {
        _muted.value = muted
    }
    
    override fun getSoundEnabled(): Flow<Boolean> = _soundEnabled.asStateFlow()
    
    override fun getMuted(): Flow<Boolean> = _muted.asStateFlow()
}

