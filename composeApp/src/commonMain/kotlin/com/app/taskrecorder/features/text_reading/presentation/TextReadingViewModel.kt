package com.app.taskrecorder.features.text_reading.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.taskrecorder.core.audio.AudioPlayer
import com.app.taskrecorder.core.audio.AudioRecorder
import com.app.taskrecorder.core.audio.AudioRecordingManager
import com.app.taskrecorder.core.util.Resource
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

class TextReadingViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val saveTaskUseCase: SaveTaskUseCase,
    audioRecorder: AudioRecorder,
    audioPlayer: AudioPlayer
) : ViewModel() {
    
    private val audioManager = AudioRecordingManager(audioRecorder, audioPlayer, viewModelScope)
    val audioState = audioManager.state
    
    private val _state = MutableStateFlow(TextReadingState())
    val state: StateFlow<TextReadingState> = _state.asStateFlow()
    
    private val audioRecorderRef = audioRecorder
    
    init {
        checkPermission()
        loadProduct()
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
    
    private fun loadProduct() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getProductsUseCase()) {
                is Resource.Success -> {
                    val product = result.data.randomOrNull()
                    _state.update { it.copy(product = product, isLoading = false) }
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
            if (audioCurrentState.audioPath == null || currentState.product == null) return@launch
            
            _state.update { it.copy(isSubmitting = true) }
            
            audioManager.cleanup()
            
            val permanentPath = audioRecorderRef.moveToPermanentStorage(audioCurrentState.audioPath)
            
            if (permanentPath != null) {
                val task = Task(
                    id = 0,
                    taskType = TaskType.TEXT_READING,
                    text = currentState.product.description,
                    imageUrl = null,
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
