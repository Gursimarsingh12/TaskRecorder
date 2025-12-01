package com.app.taskrecorder.features.photo_capture.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.taskrecorder.core.audio.AudioPlayer
import com.app.taskrecorder.core.audio.AudioRecorder
import com.app.taskrecorder.core.audio.AudioRecordingManager
import com.app.taskrecorder.core.camera.CameraController
import com.app.taskrecorder.features.task_history.domain.model.Task
import com.app.taskrecorder.features.task_history.domain.model.TaskType
import com.app.taskrecorder.features.task_history.domain.usecase.SaveTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class PhotoCaptureState(
    val imagePath: String? = null,
    val description: String = "",
    val hasCameraPermission: Boolean = false,
    val hasAudioPermission: Boolean = false,
    val isSubmitting: Boolean = false,
    val noBackgroundNoise: Boolean = false,
    val noMistakes: Boolean = false,
    val noErrorsInMiddle: Boolean = false
)

class PhotoCaptureViewModel(
    private val saveTaskUseCase: SaveTaskUseCase,
    audioRecorder: AudioRecorder,
    audioPlayer: AudioPlayer,
    private val cameraController: CameraController
) : ViewModel() {
    
    private val audioManager = AudioRecordingManager(audioRecorder, audioPlayer, viewModelScope)
    val audioState = audioManager.state
    
    private val _state = MutableStateFlow(PhotoCaptureState())
    val state: StateFlow<PhotoCaptureState> = _state.asStateFlow()
    
    private val audioRecorderRef = audioRecorder
    
    init {
        checkPermissions()
    }
    
    private fun checkPermissions() {
        viewModelScope.launch {
            val hasCameraPermission = cameraController.hasPermission()
            val hasAudioPermission = audioRecorderRef.hasPermission()
            _state.update { 
                it.copy(
                    hasCameraPermission = hasCameraPermission,
                    hasAudioPermission = hasAudioPermission
                )
            }
        }
    }
    
    fun requestCameraPermission() {
        viewModelScope.launch {
            val granted = cameraController.requestPermission()
            _state.update { it.copy(hasCameraPermission = granted) }
        }
    }
    
    fun requestAudioPermission() {
        viewModelScope.launch {
            val granted = audioRecorderRef.requestPermission()
            _state.update { it.copy(hasAudioPermission = granted) }
        }
    }
    
    fun onPhotoCaptured(path: String) {
        _state.update { it.copy(imagePath = path) }
    }
    
    fun onCameraError(error: String) {
        // Error will be shown via audioState.errorMessage if needed
    }
    
    fun retakePhoto() {
        audioManager.recordAgain()
        _state.update { 
            it.copy(
                imagePath = null,
                description = "",
                noBackgroundNoise = false,
                noMistakes = false,
                noErrorsInMiddle = false
            )
        }
    }
    
    fun updateDescription(text: String) {
        _state.update { it.copy(description = text) }
    }
    
    fun startRecording() = audioManager.startRecording()
    
    fun stopRecording() = audioManager.stopRecording()
    
    fun togglePlayPause() = audioManager.togglePlayPause()
    
    fun seekTo(position: Long) = audioManager.seekTo(position)
    
    fun stopAudioOnExit() {
        viewModelScope.launch {
            if (audioState.value.isPlaying) {
                audioManager.togglePlayPause()
            }
        }
    }
    
    fun setNoBackgroundNoise(checked: Boolean) {
        _state.update { it.copy(noBackgroundNoise = checked) }
    }
    
    fun setNoMistakes(checked: Boolean) {
        _state.update { it.copy(noMistakes = checked) }
    }
    
    fun setNoErrorsInMiddle(checked: Boolean) {
        _state.update { it.copy(noErrorsInMiddle = checked) }
    }
    
    fun recordAgain() {
        audioManager.recordAgain()
        _state.update { 
            it.copy(
                noBackgroundNoise = false,
                noMistakes = false,
                noErrorsInMiddle = false
            )
        }
    }
    
    @OptIn(ExperimentalTime::class)
    fun submitTask() {
        viewModelScope.launch {
            val currentState = _state.value
            val audioCurrentState = audioState.value
            if (currentState.imagePath == null) return@launch
            
            _state.update { it.copy(isSubmitting = true) }
            
            audioManager.cleanup()
            
            val permanentAudioPath = if (audioCurrentState.audioPath != null) {
                audioRecorderRef.moveToPermanentStorage(audioCurrentState.audioPath) ?: ""
            } else {
                ""
            }
            
            val task = Task(
                id = 0,
                taskType = TaskType.PHOTO_CAPTURE,
                text = currentState.description.ifBlank { null },
                imageUrl = null,
                imagePath = currentState.imagePath,
                audioPath = permanentAudioPath,
                durationSec = audioCurrentState.recordingDuration / 1000,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
            
            saveTaskUseCase(task)
            
            _state.update { it.copy(isSubmitting = false) }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        audioManager.cleanup()
    }
}
