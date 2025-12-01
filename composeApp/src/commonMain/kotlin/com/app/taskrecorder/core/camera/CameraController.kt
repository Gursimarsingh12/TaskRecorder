package com.app.taskrecorder.core.camera

interface CameraController {
    suspend fun capturePhoto(): Result<String>
    suspend fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}
