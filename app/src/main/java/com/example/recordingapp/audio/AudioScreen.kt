package com.example.recordingapp.audio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AudioScreen (audioViewModel: AudioViewModel = viewModel()) {
    val uiState by audioViewModel.uiState.collectAsState()

    val context = LocalContext.current
    val hasRecordPermission: () -> Boolean = {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        audioViewModel.onPermissionResult(granted)
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                audioViewModel.toggleRecording(
                    hasPermission = hasRecordPermission(),
                    requestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                )
            }
        ) {
            Text(if (uiState.isRecording) "Stop recording" else "Start recording")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            enabled = !uiState.isRecording,
            onClick = {
                audioViewModel.togglePlayback()
            }
        ) {
            Text(if (uiState.isPlaying) "Stop playing" else "Play recording")
        }
    }
}