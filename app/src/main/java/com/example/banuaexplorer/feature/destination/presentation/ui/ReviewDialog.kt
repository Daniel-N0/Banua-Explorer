package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: (rating: Double, comment: String) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Berikan Ulasan") },
        text = {
            Column {
                Text("Rating: ${rating.toInt()} Bintang")
                Slider(
                    value = rating.toFloat(),
                    onValueChange = { rating = it.toDouble() },
                    valueRange = 1f..5f,
                    steps = 3
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Komentar") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(rating, comment) }) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}