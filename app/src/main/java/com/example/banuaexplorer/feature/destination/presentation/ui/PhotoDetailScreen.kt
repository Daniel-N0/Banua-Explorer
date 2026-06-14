package com.example.banuaexplorer.feature.destination.presentation.ui // Pastikan package-nya sama

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PhotoDetailScreen(imageUrl: String, onDismiss: () -> Unit) {
    // State untuk ngatur level zoom (default 1f / ukuran asli)
    var scale by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Full Image",
            contentScale = ContentScale.Fit, // Biar foto gak kepotong
            modifier = Modifier
                .fillMaxSize()
                // Ini logika buat ngebaca cubitan jari (Pinch to Zoom)
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        // Batasi zoom maksimal 5x dan minimal 1x (ukuran asli)
                        scale = (scale * zoom).coerceIn(1f, 5f)
                    }
                }
                // Ini yang nerapin efek zoom ke gambarnya
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
        )

        // Tombol Close di pojok kanan atas
        IconButton(
            onClick = { onDismiss() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = Color.White
            )
        }
    }
}