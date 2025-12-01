package com.app.taskrecorder.features.noise_test.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.taskrecorder.core.audio.NoiseDetector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoiseTestState(
    val currentDecibel: Float = 0f,
    val averageDecibel: Float = 0f,
    val isTesting: Boolean = false,
    val testCompleted: Boolean = false,
    val canProceed: Boolean = false,
    val hasPermission: Boolean = false,
    val errorMessage: String? = null
)

class NoiseTestViewModel(
    private val noiseDetector: NoiseDetector
) : ViewModel() {
    
    private val _state = MutableStateFlow(NoiseTestState())
    val state: StateFlow<NoiseTestState> = _state.asStateFlow()
    
    private var testJob: Job? = null
    
    init {
        checkPermission()
    }
    
    private fun checkPermission() {
        viewModelScope.launch {
            val hasPermission = noiseDetector.hasPermission()
            _state.update { it.copy(hasPermission = hasPermission) }
        }
    }
    
    fun requestPermission() {
        viewModelScope.launch {
            val granted = noiseDetector.requestPermission()
            _state.update { it.copy(hasPermission = granted) }
        }
    }
    
    fun startTest() {
        testJob?.cancel()
        _state.update { it.copy(currentDecibel = 0f, averageDecibel = 0f) }
        
        testJob = viewModelScope.launch {
            _state.update { it.copy(isTesting = true, testCompleted = false, errorMessage = null) }
            
            val result = noiseDetector.startMeasuring()
            if (result.isFailure) {
                _state.update { 
                    it.copy(
                        isTesting = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to start test"
                    )
                }
                return@launch
            }
            
            val decibelJob = launch {
                noiseDetector.currentDecibel.collect { decibel ->
                    _state.update { it.copy(currentDecibel = decibel) }
                }
            }
            
            val averageJob = launch {
                noiseDetector.averageDecibel.collect { average ->
                    _state.update { it.copy(averageDecibel = average) }
                }
            }
            
            delay(5000)
            
            decibelJob.cancel()
            averageJob.cancel()
            noiseDetector.stopMeasuring()
            
            val finalAverage = _state.value.averageDecibel
            val canProceed = finalAverage < 40f
            
            _state.update { 
                it.copy(
                    isTesting = false,
                    testCompleted = true,
                    canProceed = canProceed
                )
            }
        }
    }
    
    fun resetTest() {
        _state.update { NoiseTestState(hasPermission = it.hasPermission) }
    }
    
    override fun onCleared() {
        super.onCleared()
        testJob?.cancel()
        viewModelScope.launch {
            noiseDetector.stopMeasuring()
        }
    }
}
