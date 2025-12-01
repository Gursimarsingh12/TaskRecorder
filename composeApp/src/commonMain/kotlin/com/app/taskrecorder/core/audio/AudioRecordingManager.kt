package com.app.taskrecorder.core.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class AudioRecordingState(
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0,
    val audioPath: String? = null,
    val isPlaying: Boolean = false,
    val playbackPosition: Long = 0,
    val errorMessage: String? = null
)

class AudioRecordingManager(
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(AudioRecordingState())
    val state: StateFlow<AudioRecordingState> = _state.asStateFlow()

    private var recordingJob: Job? = null
    private var playbackJob: Job? = null

    private fun resetPlaybackState() {
        _state.update {
            it.copy(
                isPlaying = false,
                playbackPosition = 0
            )
        }
    }

    fun startRecording() {
        playbackJob?.cancel()
        scope.launch { audioPlayer.stop() }

        recordingJob?.cancel()
        recordingJob = scope.launch {
            _state.update {
                it.copy(
                    errorMessage = null,
                    recordingDuration = 0,
                    isPlaying = false,
                    playbackPosition = 0
                )
            }

            val result = audioRecorder.startRecording()
            if (result.isFailure) {
                _state.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to start recording")
                }
                return@launch
            }

            launch {
                audioRecorder.isRecording.collect { r ->
                    _state.update { it.copy(isRecording = r) }
                }
            }

            launch {
                audioRecorder.recordingDuration.collect { d ->
                    _state.update { it.copy(recordingDuration = d) }
                }
            }
        }
    }

    fun stopRecording() {
        scope.launch {
            val result = audioRecorder.stopRecording()
            if (result.isSuccess) {
                val audioPath = result.getOrNull()
                val durationSec = _state.value.recordingDuration / 1000

                when {
                    durationSec < 10 -> {
                        _state.update {
                            it.copy(
                                audioPath = null,
                                errorMessage = "Recording too short (min 10 s).",
                                recordingDuration = 0
                            )
                        }
                    }
                    durationSec > 20 -> {
                        _state.update {
                            it.copy(
                                audioPath = null,
                                errorMessage = "Recording too long (max 20 s).",
                                recordingDuration = 0
                            )
                        }
                    }
                    else -> {
                        _state.update {
                            it.copy(audioPath = audioPath, errorMessage = null)
                        }
                    }
                }
            } else {
                _state.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message ?: "Failed to stop recording")
                }
            }
        }
    }

    fun togglePlayPause() {
        scope.launch {
            val audioPath = _state.value.audioPath ?: return@launch

            if (_state.value.isPlaying) {
                audioPlayer.pause()
                _state.update { it.copy(isPlaying = false) }
                playbackJob?.cancel()
                return@launch
            }

            audioPlayer.stop()
            audioPlayer.play(audioPath)

            val startPos = _state.value.playbackPosition
            if (startPos > 0) audioPlayer.seekTo(startPos)

            _state.update { it.copy(isPlaying = true) }

            playbackJob?.cancel()
            playbackJob = launch {
                while (isActive && _state.value.isPlaying) {
                    val duration = audioPlayer.duration.value
                    val position = audioPlayer.getCurrentPosition()

                    if (duration > 0 && position >= duration - 30) {
                        resetPlaybackState()
                        audioPlayer.stop()
                        break
                    }

                    _state.update { it.copy(playbackPosition = position) }
                    delay(100)
                }
            }
        }
    }

    fun seekTo(position: Long) {
        scope.launch {
            val duration = audioPlayer.duration.value
            if (duration > 0 && position >= duration - 30) {
                playbackJob?.cancel()
                resetPlaybackState()
                audioPlayer.stop()
                return@launch
            }

            _state.update { it.copy(playbackPosition = position) }

            if (_state.value.isPlaying) {
                audioPlayer.seekTo(position)
            }
        }
    }

    fun recordAgain() {
        playbackJob?.cancel()
        scope.launch {
            audioPlayer.stop()
            audioRecorder.deleteTempFile(_state.value.audioPath)
        }
        _state.update {
            it.copy(
                audioPath = null,
                recordingDuration = 0,
                errorMessage = null,
                isPlaying = false,
                playbackPosition = 0
            )
        }
    }

    fun moveToPermanentStorage(): String? {
        val path = _state.value.audioPath ?: return null
        return audioRecorder.moveToPermanentStorage(path)
    }

    fun cleanup() {
        recordingJob?.cancel()
        playbackJob?.cancel()
        scope.launch {
            audioPlayer.stop()
            audioRecorder.deleteTempFile(_state.value.audioPath)
        }
    }
}
