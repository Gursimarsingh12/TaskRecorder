package com.app.taskrecorder.platform.audio

import com.app.taskrecorder.core.audio.AudioPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
class IOSAudioPlayer : AudioPlayer {
    
    private var audioPlayer: AVAudioPlayer? = null
    private var positionJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()
    
    override suspend fun play(filePath: String): Result<Unit> {
        return try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
            
            if (audioPlayer == null) {
                val fileUrl = NSURL.fileURLWithPath(filePath)
                audioPlayer = AVAudioPlayer(
                    contentsOfURL = fileUrl,
                    error = null
                )
                
                audioPlayer?.prepareToPlay()
                _duration.value = (audioPlayer?.duration?.times(1000))?.toLong() ?: 0L
            }
            
            audioPlayer?.play()
            _isPlaying.value = true

            positionJob?.cancel()
            positionJob = scope.launch {
                while (isActive && _isPlaying.value) {
                    val currentTime = audioPlayer?.currentTime ?: 0.0
                    _currentPosition.value = (currentTime * 1000).toLong()

                    if (!audioPlayer?.playing!!) {
                        _isPlaying.value = false
                        _currentPosition.value = 0
                        break
                    }
                    
                    delay(100)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pause() {
        audioPlayer?.pause()
        _isPlaying.value = false
        positionJob?.cancel()
    }
    
    override suspend fun stop() {
        audioPlayer?.stop()
        audioPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
        positionJob?.cancel()

        val audioSession = AVAudioSession.sharedInstance()
        audioSession.setActive(false, null)
    }
    
    override suspend fun seekTo(position: Long) {
        val timeInSeconds = position / 1000.0
        audioPlayer?.currentTime = timeInSeconds
        _currentPosition.value = position
    }
    
    override fun getCurrentPosition(): Long {
        val currentTime = audioPlayer?.currentTime ?: 0.0
        return (currentTime * 1000).toLong()
    }
}
