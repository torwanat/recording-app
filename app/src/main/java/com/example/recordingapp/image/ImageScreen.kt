package com.example.recordingapp.image

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun ImageScreen(onNavigateToAudio: () -> Unit, imageViewModel: ImageViewModel = viewModel()) {
    val uiState by imageViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCaptureHelper by remember { mutableStateOf<ImageCaptureHelper?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            imageViewModel.onPermissionResult(granted)
            if (granted) {
                coroutineScope.launch {
                    previewView?.let {
                        val helper = ImageCaptureHelper(context, it)
                        imageCaptureHelper = helper
                        helper.startCamera()
                    }
                }
            }
        }
    )

    LaunchedEffect(previewView) {
        val pv = previewView ?: return@LaunchedEffect
        val hasCamera = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCamera) {
            val helper = ImageCaptureHelper(context, pv)
            imageCaptureHelper = helper
            helper.startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).also { pv ->
                        previewView = pv
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = !uiState.permissionDenied,
                onClick = {
                    imageCaptureHelper?.takePhoto { path ->
                        imageViewModel.onPhotoTaken(path)
                    }
                    Toast.makeText(context, "Photo taken", Toast.LENGTH_SHORT).show()
            }) {
                Text("Take photo")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onNavigateToAudio) {
                Text("Back to audio")
            }

            if (uiState.permissionDenied) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("Camera permission denied")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Grant permission")
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            imageCaptureHelper?.release()
        }
    }
}