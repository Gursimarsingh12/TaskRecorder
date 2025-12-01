package com.app.taskrecorder.core.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val isPlaying: StateFlow<Boolean>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    
    suspend fun play(filePath: String): Result<Unit>
    suspend fun pause()
    suspend fun stop()
    suspend fun seekTo(position: Long)
    fun getCurrentPosition(): Long
}
