package com.app.taskrecorder.core.audio

import kotlinx.coroutines.flow.StateFlow

interface NoiseDetector {
    val currentDecibel: StateFlow<Float>
    val averageDecibel: StateFlow<Float>
    
    suspend fun startMeasuring(): Result<Unit>
    suspend fun stopMeasuring()
    suspend fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}
