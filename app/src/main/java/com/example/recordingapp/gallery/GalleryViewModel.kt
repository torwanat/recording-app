package com.example.recordingapp.gallery

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(GalleryUiState(isLoading = true))
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        loadPhotos()
    }

    private fun loadPhotos() {
        viewModelScope.launch {
            val photos = mutableListOf<Photo>()
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )

            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?",
                arrayOf("%RecordingApp%"),
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateTakenColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        .buildUpon()
                        .appendPath(id.toString())
                        .build()

                    photos.add(
                        Photo(
                            uri = uri,
                            displayName = it.getString(displayNameColumn) ?: "Unknown",
                            dateTaken = it.getLong(dateTakenColumn)
                        )
                    )
                }
            }

            _uiState.value = _uiState.value.copy(
                photos = photos,
                isLoading = false
            )
        }
    }

    fun deletePhoto(photo: Photo) {
        val resolver = getApplication<Application>().contentResolver
        val uri = photo.uri

        val deleted = resolver.delete(uri, null, null)
        if (deleted > 0) {
            _uiState.value = _uiState.value.copy(
                photos = _uiState.value.photos.filter { it.uri != uri }
            )
        }
    }
}

data class GalleryUiState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false
)

data class Photo(
    val uri: Uri,
    val displayName: String,
    val dateTaken: Long,
)