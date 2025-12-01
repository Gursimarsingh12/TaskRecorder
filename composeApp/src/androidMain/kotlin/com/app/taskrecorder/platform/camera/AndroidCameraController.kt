package com.app.taskrecorder.platform.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.app.taskrecorder.core.camera.CameraController

class AndroidCameraController(private val context: Context) : CameraController {
    
    override suspend fun capturePhoto(): Result<String> {
        return Result.failure(Exception("Not implemented - requires Activity context"))
    }
    
    override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun requestPermission(): Boolean {
        return hasPermission()
    }
}
