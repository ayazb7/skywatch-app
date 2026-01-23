package com.skywatch.skywatch_app.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for video feed functionality via webhook.
 * Handles video stream, webhook connection, and video feed controls (sound/mute).
 */
interface VideoFeedRepository {
    fun getVideoStream(): Flow<ByteArray>
    suspend fun connectToWebhook(url: String)
    suspend fun disconnectFromWebhook()
    
    // Video feed controls
    suspend fun toggleSound(enabled: Boolean)
    suspend fun toggleMute(muted: Boolean)
    fun getSoundEnabled(): Flow<Boolean>
    fun getMuted(): Flow<Boolean>
}

