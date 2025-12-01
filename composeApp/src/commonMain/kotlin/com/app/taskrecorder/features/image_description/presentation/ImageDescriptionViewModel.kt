package com.app.taskrecorder.features.image_description.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.taskrecorder.core.audio.AudioPlayer
import com.app.taskrecorder.core.audio.AudioRecorder
import com.app.taskrecorder.core.audio.AudioRecordingManager
import com.app.taskrecorder.core.util.Resource
import com.app.taskrecorder.features.products.domain.model.Product
import com.app.taskrecorder.features.products.domain.usecase.GetProductsUseCase
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

data class ImageDescriptionState(
    val product: Product? = null,
    val imageUrl: String? = null,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val isSubmitting: Boolean = false,
    val noBackgroundNoise: Boolean = false,
    val noMistakes: Boolean = false,
    val noErrorsInMiddle: Boolean = false
)

class ImageDescriptionViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    audioRecorder: AudioRecorder,
    audioPlayer: AudioPlayer
) : ViewModel() {
    
    private val audioManager = AudioRecordingManager(audioRecorder, audioPlayer, viewModelScope)
    val audioState = audioManager.state
    
    private val _state = MutableStateFlow(ImageDescriptionState())
    val state: StateFlow<ImageDescriptionState> = _state.asStateFlow()
    
    private val audioRecorderRef = audioRecorder
    
    init {
        checkPermission()
        loadImage()
    }
    
    private fun checkPermission() {
        viewModelScope.launch {
            val hasPermission = audioRecorderRef.hasPermission()
            _state.update { it.copy(hasPermission = hasPermission) }
        }
    }
    
    fun requestPermission() {
        viewModelScope.launch {
            val granted = audioRecorderRef.requestPermission()
            _state.update { it.copy(hasPermission = granted) }
        }
    }
    
    private fun loadImage() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getProductsUseCase()) {
                is Resource.Success -> {
                    val product = result.data.randomOrNull()
                    val imageUrl = if (product != null) {
                        val allImages = mutableListOf<String>()
                        product.images.let { allImages.addAll(it) }
                        allImages.randomOrNull()
                    } else {
                        null
                    }
                    
                    _state.update { 
                        it.copy(
                            product = product,
                            imageUrl = imageUrl,
                            isLoading = false
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
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
            if (audioCurrentState.audioPath == null || currentState.imageUrl == null) return@launch
            
            _state.update { it.copy(isSubmitting = true) }
            
            audioManager.cleanup()
            
            val permanentPath = audioRecorderRef.moveToPermanentStorage(audioCurrentState.audioPath)
            
            if (permanentPath != null) {
                val task = Task(
                    id = 0,
                    taskType = TaskType.IMAGE_DESCRIPTION,
                    text = null,
                    imageUrl = currentState.imageUrl,
                    imagePath = null,
                    audioPath = permanentPath,
                    durationSec = audioCurrentState.recordingDuration / 1000,
                    timestamp = Clock.System.now().toEpochMilliseconds()
                )
                
                saveTaskUseCase(task)
            }
            
            _state.update { it.copy(isSubmitting = false) }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        audioManager.cleanup()
    }
}
