package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Review
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destination: Destination,
    reviews: List<Review>,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onRouteClick: () -> Unit, // <--- 1. TAMBAHKAN PARAMETER INI
    onSaveReview: (Review) -> Unit,
    onDeleteReview: (Review) -> Unit
) {
    val banuaGreen = Color(0xFF005959)
    val backgroundGray = Color(0xFFF8F9FA)
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<Review?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(backgroundGray)) {
// --- 1. GAMBAR BACKGROUND (HEADER) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFF2E8B57)),
            contentAlignment = Alignment.Center
        ) {
            // Menggunakan AsyncImage untuk memuat foto dari URL
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = "Foto ${destination.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Agar gambar penuh tanpa mengubah rasio
            )

            // Overlay gelap tipis agar tombol Back & Love tetap terlihat jelas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
        }

        // --- 2. KONTEN UTAMA (BOTTOM SHEET STYLE) ---
        // Dipindah ke atas agar posisinya di BAWAH tombol (karena dirender duluan)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(300.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .padding(bottom = 100.dp)
                ) {

                    // --- BAGIAN JUDUL & RATING BADGE ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                            Text(
                                text = destination.name,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF001F1F),
                                lineHeight = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = destination.kabupaten.uppercase(),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (destination.rating > 0) "${destination.rating}/5" else "5/5",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- JAM OPERASIONAL ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(banuaGreen))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Minggu : Buka 24 Jam", color = banuaGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- DESKRIPSI ---
                    Text(
                        text = destination.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- GALERI FOTO ---
                    Text("Galeri Foto", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001F1F))
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(4) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- TOMBOL SOSIAL MEDIA ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Buka IG */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = banuaGreen)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Instagram", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { /* TODO: Buka WA */ },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = banuaGreen)
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("WhatsApp", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // --- BAGIAN ULASAN (BREAD) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4.6", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF001F1F))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row {
                                    repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp)) }
                                }
                                Text("${reviews.size} ULASAN", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                            }
                        }

                        Button(
                            onClick = {
                                editingReview = null
                                showDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = banuaGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Berikan Ulasan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (reviews.isEmpty()) {
                        Text("Belum ada ulasan. Jadilah yang pertama!", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        reviews.forEach { review ->
                            ReviewCardUI(
                                review = review,
                                onEdit = {
                                    editingReview = review
                                    showDialog = true
                                },
                                onDelete = { onDeleteReview(review) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- VIDEO RELEVAN ---
                    Text("Video Relevan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF001F1F))
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) {
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.DarkGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(40.dp)) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 3. TOMBOL AKSI BAWAH (TRANSPARAN MELAYANG) ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = onRouteClick, // <--- 2. CUKUP PANGGIL LAMBDA INI BRO!
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = banuaGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Rute Perjalanan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- 4. TOMBOL BACK & FAVORITE (FLOATING TOP) ---
        // Dipindah ke PALING BAWAH di dalam Box agar menjadi PALING ATAS di layar (Z-Index tertinggi)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp) // Ukuran icon diperbesar sedikit
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorit",
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }

    // ... (Sisa fungsi ReviewInputDialog & ReviewCardUI tetap sama, tidak aku sertakan lagi agar tidak kepanjangan) ...
    // --- POP-UP DIALOG KETIK ULASAN ---
    if (showDialog) {
        ReviewInputDialog(
            initialText = editingReview?.comment ?: "",
            onDismiss = { showDialog = false },
            onSubmit = { comment ->
                val finalReview = editingReview?.copy(
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                ) ?: Review(
                    id = UUID.randomUUID().toString(),
                    destinationId = destination.id,
                    userName = "Anda",
                    userAvatarUrl = "",
                    rating = 5.0,
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                )
                onSaveReview(finalReview)
                showDialog = false
            }
        )
    }
}

@Composable
fun ReviewCardUI(review: Review, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isCurrentUser = review.userName == "Anda"
    val bgColor = if (isCurrentUser) Color(0xFFF0F4FF) else Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 0.dp else 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isCurrentUser) Color(0xFF005959) else Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(review.userName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Baru saja", fontSize = 11.sp, color = Color.Gray)
                }
                if (isCurrentUser) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF005959), modifier = Modifier.size(18.dp).clickable { onEdit() })
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp).clickable { onDelete() })
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = review.comment, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}

@Composable
fun ReviewInputDialog(initialText: String, onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf(initialText) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialText.isEmpty()) "Tulis Ulasan" else "Edit Ulasan", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text, onValueChange = { text = it }, placeholder = { Text("Bagaimana pengalamanmu di sini?") },
                modifier = Modifier.fillMaxWidth(), minLines = 3, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF005959))
            )
        },
        confirmButton = { Button(onClick = { onSubmit(text) }, enabled = text.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005959))) { Text("Simpan") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal", color = Color.Gray) } }
    )
}