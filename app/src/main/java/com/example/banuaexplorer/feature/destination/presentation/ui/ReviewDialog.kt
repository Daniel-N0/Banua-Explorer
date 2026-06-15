package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

@Composable
fun ReviewDialog(
    destinationId: String,
    onDismiss: () -> Unit,
    onConfirm: (rating: Double, comment: String) -> Unit
) {
    var rating by remember { mutableDoubleStateOf(5.0) }
    var comment by remember { mutableStateOf("") }
    val db = Firebase.firestore

    // Ambil nama user dari Firebase Auth, kalau gagal/kosong baru pakai default nama lu
    val currentUserAuth = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val activeUserName = currentUserAuth?.displayName ?: "Muhammad Ilham"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.berikan_ulasan)) },
        text = {
            Column {
                Text(String.format(stringResource(R.string.rating_bintang), rating.toInt()))
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
                    label = { Text(stringResource(R.string.komentar)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // 1. Jalankan fungsi bawaan buat update UI lokal
                onConfirm(rating, comment)

                // 2. Siapkan data Review-nya dengan nama dinamis
                val newReview = Review(
                    id = UUID.randomUUID().toString(),
                    destinationId = destinationId,
                    userName = activeUserName, // Sekarang otomatis pakai nama user yang login!
                    userAvatarUrl = "",
                    rating = rating,
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                )

                // 3. Push ke Firebase
                db.collection("reviews")
                    .document(newReview.id)
                    .set(newReview)
                    .addOnSuccessListener { onDismiss() }
                    .addOnFailureListener { onDismiss() }
            }) {
                Text(stringResource(R.string.kirim))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.batal)) }
        }
    )
}