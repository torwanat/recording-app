package com.example.recordingapp.image

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ImageViewModel (application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ImageUiState())
    val uiState: StateFlow<ImageUiState> = _uiState.asStateFlow()

    fun onPhotoTaken(photoPath: String) {
        _uiState.value = _uiState.value.copy(
            lastSavedPhotoPath = photoPath
        )
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(
            permissionDenied = !granted
        )
    }

}

data class ImageUiState (
    val permissionDenied: Boolean = false,
    val lastSavedPhotoPath: String? = null
)