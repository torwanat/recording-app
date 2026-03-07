package com.example.recordingapp.image

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class ImageCaptureHelper (private val context: Context, private val previewView: PreviewView) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null

    suspend fun startCamera() {
        cameraProvider = withContext(Dispatchers.IO) {
            ProcessCameraProvider.getInstance(context).get()
        }
        imageCapture = ImageCapture.Builder().build()

        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(
            context as androidx.lifecycle.LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    fun takePhoto(onSuccess: (String) -> Unit) {
        val ic = imageCapture ?: return
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${name}_${UUID.randomUUID()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/RecordingApp")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        ic.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri?.toString()
                    if (savedUri != null) {
                        onSuccess(savedUri)
                    }
                }

                override fun onError(e: ImageCaptureException) {
                    e.printStackTrace()
                }
            }
        )
    }

    fun release() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
    }
}