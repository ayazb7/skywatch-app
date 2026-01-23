package com.skywatch.skywatch_app.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoFeedRepositoryImplTest {

    private val repository = VideoFeedRepositoryImpl()

    @Test
    fun toggleSound_updatesSoundState() = runTest {
        assertTrue(repository.getSoundEnabled().first())

        repository.toggleSound(false)
        assertFalse(repository.getSoundEnabled().first())

        repository.toggleSound(true)
        assertTrue(repository.getSoundEnabled().first())
    }

    @Test
    fun toggleMute_updatesMuteState() = runTest {
        assertFalse(repository.getMuted().first())

        repository.toggleMute(true)
        assertTrue(repository.getMuted().first())

        repository.toggleMute(false)
        assertFalse(repository.getMuted().first())
    }
}

