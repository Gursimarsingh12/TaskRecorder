package com.app.taskrecorder.platform.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.core.content.ContextCompat
import com.app.taskrecorder.core.audio.AudioRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class AndroidAudioRecorder(private val context: Context) : AudioRecorder {
    
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var startTime: Long = 0
    private var durationJob: kotlinx.coroutines.Job? = null
    private val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)
    
    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()
    
    private val _recordingDuration = MutableStateFlow(0L)
    override val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()
    
    override suspend fun startRecording(): Result<Unit> {
        return try {
            val tempDir = File(context.cacheDir, "temp_recordings").apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            
            outputFile = File(tempDir, "temp_recording_${System.currentTimeMillis()}.m4a")
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            
            startTime = System.currentTimeMillis()
            _isRecording.value = true
            _recordingDuration.value = 0L
            
            durationJob?.cancel()
            durationJob = scope.launch {
                while (isActive && _isRecording.value) {
                    _recordingDuration.value = System.currentTimeMillis() - startTime
                    kotlinx.coroutines.delay(100)
                }
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun stopRecording(): Result<String> {
        return try {
            durationJob?.cancel()
            
            val duration = System.currentTimeMillis() - startTime
            _recordingDuration.value = duration
            _isRecording.value = false
            
            val currentFile = outputFile
            
            mediaRecorder?.apply {
                try {
                    if (duration >= 1000) {
                        stop()
                    }
                } catch (e: Exception) {
                }
                release()
            }
            mediaRecorder = null
            
            if (duration < 1000) {
                currentFile?.delete()
                return Result.failure(Exception("Recording too short"))
            }
            
            val path = currentFile?.absolutePath ?: ""
            Result.success(path)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
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
    
    override fun moveToPermanentStorage(tempPath: String): String? {
        return try {
            val tempFile = File(tempPath)
            if (!tempFile.exists()) return null
            
            val recordingsDir = File(context.getExternalFilesDir(null), "Recordings").apply {
                if (!exists()) {
                    mkdirs()
                }
            }
            
            val permanentFile = File(recordingsDir, "recording_${System.currentTimeMillis()}.m4a")
            tempFile.copyTo(permanentFile, overwrite = true)
            tempFile.delete()
            
            permanentFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
    
    override fun deleteTempFile(path: String?) {
        try {
            path?.let {
                val file = File(it)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
        }
    }
}
