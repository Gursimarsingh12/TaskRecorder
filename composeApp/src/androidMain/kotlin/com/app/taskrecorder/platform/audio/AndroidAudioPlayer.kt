package com.app.taskrecorder.platform.audio

import android.content.Context
import android.media.MediaPlayer
import com.app.taskrecorder.core.audio.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    
    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()
    
    override suspend fun play(filePath: String): Result<Unit> {
        return try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(filePath)
                    prepare()
                    
                    _duration.value = duration.toLong()
                    
                    setOnCompletionListener {
                        _isPlaying.value = false
                        _currentPosition.value = 0
                    }
                }
            }
            
            mediaPlayer?.start()
            _isPlaying.value = true
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pause() {
        mediaPlayer?.apply {
            if (isPlaying) {
                pause()
            }
        }
        _isPlaying.value = false
    }
    
    override suspend fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
    }
    
    override suspend fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
        _currentPosition.value = position
    }
    
    override fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }
}
