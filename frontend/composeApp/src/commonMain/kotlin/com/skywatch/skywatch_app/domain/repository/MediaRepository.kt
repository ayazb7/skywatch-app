package com.skywatch.skywatch_app.domain.repository

interface MediaRepository {
    suspend fun takeScreenshot()
    suspend fun startRecording()
    suspend fun stopRecording()
}

