package com.app.taskrecorder.platform.audio

import com.app.taskrecorder.core.audio.AudioRecorder
import io.ktor.client.request.invoke
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.setActive
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalForeignApi::class)
class IOSAudioRecorder : AudioRecorder {
    
    private var audioRecorder: AVAudioRecorder? = null
    private var outputFileUrl: NSURL? = null
    private var startTime: Long = 0
    private var durationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    
    private val _recordingDuration = MutableStateFlow(0L)
    override val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()
    
    override suspend fun startRecording(): Result<Unit> {
        return try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryRecord, null)
            audioSession.setActive(true, null)

            val tempDir = NSTemporaryDirectory()
            val fileName = "temp_recording_${platform.Foundation.NSDate().timeIntervalSince1970}.m4a"
            outputFileUrl = NSURL.fileURLWithPath("$tempDir$fileName")

            val settings = mapOf(
                "AVFormatIDKey" to platform.AVFAudio.kAudioFormatMPEG4AAC,
                "AVSampleRateKey" to 44100.0,
                "AVNumberOfChannelsKey" to 1,
                "AVEncoderAudioQualityKey" to platform.AVFAudio.AVAudioQualityHigh
            )

            audioRecorder = AVAudioRecorder(
                URL = outputFileUrl!!,
                settings = settings,
                error = null
            )
            
            audioRecorder?.record()
            
            startTime = currentTimeMillis()
            _isRecording.value = true
            _recordingDuration.value = 0L

            durationJob?.cancel()
            durationJob = scope.launch {
                while (isActive && _isRecording.value) {
                    _recordingDuration.value = currentTimeMillis() - startTime
                    delay(100)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun stopRecording(): Result<String> {
        return try {
            durationJob?.cancel()
            
            val duration = currentTimeMillis() - startTime
            _recordingDuration.value = duration
            _isRecording.value = false
            
            val currentFileUrl = outputFileUrl
            
            audioRecorder?.stop()
            audioRecorder = null

            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setActive(false, null)
            
            if (duration < 1000) {
                currentFileUrl?.path?.let { path ->
                    NSFileManager.defaultManager.removeItemAtPath(path, null)
                }
                return Result.failure(Exception("Recording too short"))
            }
            
            val path = currentFileUrl?.path ?: ""
            Result.success(path)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun hasPermission(): Boolean {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)
        return status == AVAuthorizationStatusAuthorized
    }
    
    override suspend fun requestPermission(): Boolean = suspendCoroutine { continuation ->
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeAudio)
        when (status) {
            AVAuthorizationStatusAuthorized -> {
                continuation.resume(true)
            }
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeAudio) { granted ->
                    continuation.resume(granted)
                }
            }
            else -> {
                continuation.resume(false)
            }
        }
    }
    
    override fun moveToPermanentStorage(tempPath: String): String? {
        return try {
            val fileManager = NSFileManager.defaultManager

            val documentDirectory = fileManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            
            val recordingsPath = "${documentDirectory?.path}/Recordings"

            if (!fileManager.fileExistsAtPath(recordingsPath)) {
                fileManager.createDirectoryAtPath(
                    path = recordingsPath,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            }
            val fileName = "recording_${platform.Foundation.NSDate().timeIntervalSince1970}.m4a"
            val permanentPath = "$recordingsPath/$fileName"

            fileManager.copyItemAtPath(
                srcPath = tempPath,
                toPath = permanentPath,
                error = null
            )

            fileManager.removeItemAtPath(tempPath, null)
            
            permanentPath
        } catch (e: Exception) {
            null
        }
    }
    
    override fun deleteTempFile(path: String?) {
        try {
            path?.let {
                NSFileManager.defaultManager.removeItemAtPath(it, null)
            }
        } catch (e: Exception) {

        }
    }
    
    private fun currentTimeMillis(): Long {
        return (platform.Foundation.NSDate().timeIntervalSince1970 * 1000).toLong()
    }
}
