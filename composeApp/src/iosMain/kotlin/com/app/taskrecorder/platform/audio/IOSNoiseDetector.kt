package com.app.taskrecorder.platform.audio

import com.app.taskrecorder.core.audio.NoiseDetector
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
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.timeIntervalSince1970
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log10

@OptIn(ExperimentalForeignApi::class)
class IOSNoiseDetector : NoiseDetector {
    
    private var audioRecorder: AVAudioRecorder? = null
    private var measureJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val _currentDecibel = MutableStateFlow(0f)
    override val currentDecibel: StateFlow<Float> = _currentDecibel.asStateFlow()
    
    private val _averageDecibel = MutableStateFlow(0f)
    override val averageDecibel: StateFlow<Float> = _averageDecibel.asStateFlow()
    
    private val decibelReadings = mutableListOf<Float>()
    
    override suspend fun startMeasuring(): Result<Unit> {
        return try {

            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryRecord, null)
            audioSession.setActive(true, null)

            val tempDir = NSTemporaryDirectory()
            val fileName = "temp_metering_${platform.Foundation.NSDate().timeIntervalSince1970}.m4a"
            val fileUrl = NSURL.fileURLWithPath("$tempDir$fileName")

            val settings = mapOf(
                "AVFormatIDKey" to platform.AVFAudio.kAudioFormatMPEG4AAC,
                "AVSampleRateKey" to 44100.0,
                "AVNumberOfChannelsKey" to 1,
                "AVEncoderAudioQualityKey" to platform.AVFAudio.AVAudioQualityLow
            )
            
            // Create recorder
            audioRecorder = AVAudioRecorder(
                URL = fileUrl,
                settings = settings,
                error = null
            )
            
            audioRecorder?.meteringEnabled = true
            audioRecorder?.record()
            
            decibelReadings.clear()

            measureJob = scope.launch {
                while (isActive) {
                    audioRecorder?.updateMeters()

                    val averagePower = audioRecorder?.averagePowerForChannel(0u) ?: -160f

                    val normalizedDb = ((averagePower + 160f) / 160f * 60f).coerceIn(0f, 60f)
                    
                    _currentDecibel.value = normalizedDb
                    decibelReadings.add(normalizedDb)
                    
                    if (decibelReadings.isNotEmpty()) {
                        _averageDecibel.value = decibelReadings.average().toFloat()
                    }

                    if (decibelReadings.size > 100) {
                        decibelReadings.removeAt(0)
                    }
                    
                    delay(100)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun stopMeasuring() {
        measureJob?.cancel()
        measureJob = null
        
        audioRecorder?.stop()
        audioRecorder = null

        val audioSession = AVAudioSession.sharedInstance()
        audioSession.setActive(false, null)
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
}
