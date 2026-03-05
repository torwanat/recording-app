package com.example.recordingapp.audio

import android.app.Application
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AudioViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AudioUiState())
    val uiState: StateFlow<AudioUiState> = _uiState.asStateFlow()

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private val outputFile: String by lazy {
        val file = File(getApplication<Application>().externalCacheDir, "record.3gp")
        file.absolutePath
    }

    fun onPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                permissionDenied = !granted
            )
        }
        if (granted) startRecording()
    }

    private fun startRecording() {
        recorder = MediaRecorder(getApplication<Application>().applicationContext)

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(outputFile)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
                start()
                _uiState.value = _uiState.value.copy(
                    isRecording = true
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            try {
                stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            release()
        }

        recorder = null
        _uiState.value = _uiState.value.copy(
            isRecording = false
        )
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(outputFile)
                prepare()
                start()
                _uiState.value = _uiState.value.copy(
                    isPlaying = true
                )

                setOnCompletionListener {
                    stopPlaying()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null

        _uiState.value = _uiState.value.copy(
            isPlaying = false
        )
    }

    fun toggleRecording(hasPermission: Boolean, requestPermission: () -> Unit) {
        if (_uiState.value.isRecording) {
            stopRecording()
        } else {
            if (hasPermission) {
                startRecording()
            } else {
                requestPermission()
            }
        }
    }

    fun togglePlayback() {
        if (_uiState.value.isPlaying) {
            stopPlaying()
        } else {
            startPlaying()
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}

data class AudioUiState(
    val isRecording: Boolean = false,
    val isPlaying: Boolean = false,
    val permissionDenied: Boolean = false
)