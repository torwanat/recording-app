package com.example.recordingapp.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GalleryScreen(onNavigateToImage: () -> Unit, galleryViewModel: GalleryViewModel = viewModel()) {
    val uiState by galleryViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End,

                ) {
                Button(
                    onClick = onNavigateToImage
                ) {
                    Text("Back to camera")
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.photos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No photos taken yet")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.photos, key = { it.uri }) { photo ->
                        PhotoCard(
                            photo,
                            onDelete = { galleryViewModel.deletePhoto(photo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoCard(photo: Photo, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.uri)
                    .crossfade(true)
                    .build(),
                contentDescription = photo.displayName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = photo.displayName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                        .format(photo.dateTaken),
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
            }
        }
    }
}