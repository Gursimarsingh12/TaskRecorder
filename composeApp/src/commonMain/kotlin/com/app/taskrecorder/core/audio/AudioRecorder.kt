package com.app.taskrecorder.core.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioRecorder {
    val isRecording: StateFlow<Boolean>
    val recordingDuration: StateFlow<Long>
    
    suspend fun startRecording(): Result<Unit>
    suspend fun stopRecording(): Result<String>
    suspend fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
    fun moveToPermanentStorage(tempPath: String): String?
    fun deleteTempFile(path: String?)
}
