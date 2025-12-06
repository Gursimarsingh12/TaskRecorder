package com.app.taskrecorder.platform.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.Foundation.*
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CameraPreviewComposable(
    onImageCaptured: (String) -> Unit,
    onError: (String) -> Unit,
    captureImage: Boolean,
    onCaptureComplete: () -> Unit,
    modifier: Modifier
) {
    val cameraManager = remember { IOSCameraManager() }
    
    LaunchedEffect(captureImage) {
        if (captureImage) {
            cameraManager.capturePhoto(
                onSuccess = { path -> onImageCaptured(path) },
                onError = { error -> onError(error) }
            )
            onCaptureComplete()
        }
    }
    
    UIKitView(
        factory = {
            val view = UIView()
            cameraManager.setupCamera(view)
            view
        },
        modifier = modifier,
        update = { view ->
            cameraManager.updatePreviewLayer(view)
        },
        onRelease = {
            cameraManager.cleanup()
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
class IOSCameraManager {
    private var captureSession: AVCaptureSession? = null
    private var photoOutput: AVCapturePhotoOutput? = null
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var photoCaptureDelegate: PhotoCaptureDelegate? = null
    
    fun setupCamera(view: UIView) {
        val session = AVCaptureSession()
        session.sessionPreset = AVCaptureSessionPresetPhoto
        
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        
        if (device != null) {
            val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
            
            if (input != null && session.canAddInput(input)) {
                session.addInput(input)
            }
            
            val output = AVCapturePhotoOutput()
            if (session.canAddOutput(output)) {
                session.addOutput(output)
                photoOutput = output
            }
            
            val preview = AVCaptureVideoPreviewLayer(session = session)
            preview.videoGravity = AVLayerVideoGravityResizeAspectFill
            preview.frame = view.layer.bounds
            view.layer.addSublayer(preview)
            
            previewLayer = preview
            captureSession = session
            
            dispatch_async(dispatch_get_global_queue(0L, 0u)) {
                session.startRunning()
            }
        }
    }
    
    fun updatePreviewLayer(view: UIView) {
        CATransaction.begin()
        CATransaction.setValue(true, kCATransactionDisableActions)
        previewLayer?.frame = view.layer.bounds
        CATransaction.commit()
    }
    
    fun capturePhoto(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val output = photoOutput ?: run {
            onError("Camera not initialized")
            return
        }
        
        val settings = AVCapturePhotoSettings.photoSettings()
        
        val delegate = PhotoCaptureDelegate(
            onSuccess = onSuccess,
            onError = onError
        )
        photoCaptureDelegate = delegate
        
        output.capturePhotoWithSettings(settings, delegate)
    }
    
    fun cleanup() {
        captureSession?.stopRunning()
        captureSession = null
        photoOutput = null
        previewLayer = null
        photoCaptureDelegate = null
    }
}

@OptIn(ExperimentalForeignApi::class)
class PhotoCaptureDelegate(
    private val onSuccess: (String) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), AVCapturePhotoCaptureDelegateProtocol {
    
    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhoto: AVCapturePhoto,
        error: NSError?
    ) {
        if (error != null) {
            dispatch_async(dispatch_get_main_queue()) {
                onError(error.localizedDescription)
            }
            return
        }
        
        val imageData = didFinishProcessingPhoto.fileDataRepresentation()
        
        if (imageData != null) {
            val fileManager = NSFileManager.defaultManager
            val documentDirectory = fileManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            
            val photosPath = "${documentDirectory?.path}/Photos"
            
            if (!fileManager.fileExistsAtPath(photosPath)) {
                fileManager.createDirectoryAtPath(
                    path = photosPath,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }
            
            val timestamp = NSDate().timeIntervalSince1970
            val fileName = "photo_$timestamp.jpg"
            val filePath = "$photosPath/$fileName"
            
            val success = imageData.writeToFile(filePath, true)
            
            dispatch_async(dispatch_get_main_queue()) {
                if (success) {
                    onSuccess(filePath)
                } else {
                    onError("Failed to save photo")
                }
            }
        } else {
            dispatch_async(dispatch_get_main_queue()) {
                onError("Failed to get image data")
            }
        }
    }
}
