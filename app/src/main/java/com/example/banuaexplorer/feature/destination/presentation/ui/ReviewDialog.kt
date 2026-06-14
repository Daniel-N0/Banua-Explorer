package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.banuaexplorer.feature.destination.domain.model.Review // Pastikan import Review ada
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

@Composable
fun ReviewDialog(
    destinationId: String, // WAJIB DITAMBAH: Biar tau ini ulasan buat wisata apa
    onDismiss: () -> Unit,
    onConfirm: (rating: Double, comment: String) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var comment by remember { mutableStateOf("") }
    val db = Firebase.firestore

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
            Button(onClick = {
                // 1. Jalankan fungsi bawaan buat update UI lokal
                onConfirm(rating, comment)

                // 2. Siapkan data Review-nya
                val newReview = Review(
                    id = UUID.randomUUID().toString(),
                    destinationId = destinationId,
                    userName = "Muhammad Ilham", // Nama asli lu langsung nampang di UI!
                    userAvatarUrl = "",
                    rating = rating,
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                )

                // 3. Push ke Firebase
                db.collection("reviews")
                    .document(newReview.id)
                    .set(newReview)
                    .addOnSuccessListener {
                        // Tutup dialog kalau berhasil kekirim ke cloud
                        onDismiss()
                    }
                    .addOnFailureListener {
                        // Kalau error, dialog juga ditutup aja biar ga nyangkut
                        onDismiss()
                    }
            }) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}