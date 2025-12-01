package com.app.taskrecorder.platform.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.app.taskrecorder.core.audio.NoiseDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10

class AndroidNoiseDetector(private val context: Context) : NoiseDetector {
    
    private var audioRecord: AudioRecord? = null
    private var measureJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    
    private val _currentDecibel = MutableStateFlow(0f)
    override val currentDecibel: StateFlow<Float> = _currentDecibel.asStateFlow()
    
    private val _averageDecibel = MutableStateFlow(0f)
    override val averageDecibel: StateFlow<Float> = _averageDecibel.asStateFlow()
    
    private val decibelReadings = mutableListOf<Float>()
    
    override suspend fun startMeasuring(): Result<Unit> {
        return try {
            val sampleRate = 44100
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            
            audioRecord?.startRecording()
            decibelReadings.clear()
            
            measureJob = scope.launch {
                val buffer = ShortArray(bufferSize)
                while (isActive) {
                    val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                    if (read > 0) {
                        var sum = 0.0
                        for (i in 0 until read) {
                            sum += buffer[i] * buffer[i]
                        }
                        val rms = kotlin.math.sqrt(sum / read)
                        
                        val reference = 32767.0
                        val decibel = if (rms > 1.0) {
                            val db = 20 * log10(rms / reference) + 90
                            db.toFloat().coerceIn(0f, 60f)
                        } else {
                            0f
                        }
                        
                        _currentDecibel.value = decibel
                        decibelReadings.add(decibel)
                        
                        if (decibelReadings.size > 0) {
                            _averageDecibel.value = decibelReadings.average().toFloat()
                        }
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
        audioRecord?.apply {
            try {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    stop()
                }
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        audioRecord = null
    }
    
    override suspend fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun requestPermission(): Boolean {
        return hasPermission()
    }
}
