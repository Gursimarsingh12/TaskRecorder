package com.app.taskrecorder.platform.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreview(
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit,
    captureImage: Boolean,
    onCaptureComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    LaunchedEffect(captureImage) {
        if (captureImage) {
            capturePhoto(context, imageCapture, onImageCaptured, onError)
            onCaptureComplete()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    onError(e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

private suspend fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit
) {
    val photoFile = File(
        context.getExternalFilesDir(null),
        "Photos/${SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis())}.jpg"
    ).apply {
        parentFile?.mkdirs()
    }
    
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    
    suspendCoroutine { continuation ->
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri)
                    continuation.resume(Unit)
                }
                
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                    continuation.resume(Unit)
                }
            }
        )
    }
}
