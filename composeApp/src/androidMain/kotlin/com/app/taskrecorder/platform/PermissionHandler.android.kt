package com.app.taskrecorder.platform

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }
    
    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.RECORD_AUDIO)
    }
}

@Composable
actual fun RequestCameraPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }
    
    LaunchedEffect(Unit) {
        launcher.launch(android.Manifest.permission.CAMERA)
    }
}
