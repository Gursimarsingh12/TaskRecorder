package com.app.taskrecorder.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)
        when (status) {
            AVAuthorizationStatusAuthorized -> {
                onPermissionResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeAudio) { granted ->
                    onPermissionResult(granted)
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                onPermissionResult(false)
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun RequestCameraPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        when (status) {
            AVAuthorizationStatusAuthorized -> {
                onPermissionResult(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    onPermissionResult(granted)
                }
            }
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                onPermissionResult(false)
            }
        }
    }
}
