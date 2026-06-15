package com.example.banuaexplorer.feature.destination.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destination: Destination,
    reviews: List<Review>, // Ini data lokal (Room)
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onRouteClick: () -> Unit,
    onSaveReview: (Review) -> Unit,
    onDeleteReview: (Review) -> Unit
) {
    val banuaGreen = MaterialTheme.colorScheme.primary
    val backgroundGray = MaterialTheme.colorScheme.background
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var editingReview by remember { mutableStateOf<Review?>(null) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    // State buat nampung data dari Firebase
    var realGalleryUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var publicReviews by remember { mutableStateOf<List<Review>>(emptyList()) }

    // GABUNGAN DATA: Kalau Firebase ada, pake Firebase. Kalau kosong/offline, pake lokal.
    val displayReviews = if (publicReviews.isNotEmpty()) publicReviews else reviews
    val averageRating = if (displayReviews.isNotEmpty()) displayReviews.sumOf { it.rating } / displayReviews.size else 0.0
    val formattedRating = if (averageRating > 0) String.format(java.util.Locale.US, "%.1f", averageRating) else "0.0"
    val context = LocalContext.current
    var videoUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var instagramUrl by remember { mutableStateOf("") }
    var whatsappUrl by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(backgroundGray)) {
        // --- 1. GAMBAR BACKGROUND (HEADER) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFF2E8B57)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = "Foto ${destination.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
        }

        // --- 2. KONTEN UTAMA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(300.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface
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
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 32.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = destination.kabupaten.uppercase(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$formattedRating/5",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(banuaGreen))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Minggu : Buka 24 Jam", color = banuaGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = destination.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- GALERI FOTO & ULASAN REALTIME FIREBASE ---
                    Text("Galeri Foto", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    LaunchedEffect(destination.id) {
                        if (destination.id.isNotEmpty() && destination.id != "dummy_1") {
                            val db = Firebase.firestore

                            // Tarik Galeri
                            db.collection("destinations")
                                .whereEqualTo("id", destination.id)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val urls = querySnapshot.documents[0].get("galleryUrls") as? List<String> ?: emptyList()
                                        realGalleryUrls = urls

                                        // --- TAMBAHIN DUA BARIS INI BRO ---
                                        val vUrls = querySnapshot.documents[0].get("videoUrls") as? List<String> ?: emptyList()
                                        videoUrls = vUrls
                                        // ----------------------------------

                                        instagramUrl =
                                            querySnapshot.documents[0].getString("instagramUrl") ?: ""

                                        whatsappUrl =
                                            querySnapshot.documents[0].getString("whatsappUrl") ?: ""
                                    }
                                }

                            // Tarik Ulasan REAL-TIME (Snapshot Listener)
                            db.collection("reviews")
                                .whereEqualTo("destinationId", destination.id)
                                .addSnapshotListener { snapshot, error ->
                                    if (error != null) {
                                        Log.e("Firebase", "Gagal narik ulasan", error)
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null) {
                                        val fetchedReviews = snapshot.documents.mapNotNull {
                                            it.toObject(Review::class.java)
                                        }.sortedByDescending { it.timestamp }
                                        publicReviews = fetchedReviews
                                    }
                                }
                        }
                    }

                    if (realGalleryUrls.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(realGalleryUrls) { url ->
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Foto Destinasi",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { selectedImage = url },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Text("Memuat galeri...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (instagramUrl.isNotBlank()) {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(instagramUrl)
                                    )
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = banuaGreen)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Instagram", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = {
                                if (whatsappUrl.isNotBlank()) {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(whatsappUrl)
                                    )
                                    context.startActivity(intent)
                                }
                            },
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
                            Text(formattedRating, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row {
                                    for (i in 1..5) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (i <= kotlin.math.round(averageRating).toInt()) Color(0xFFFFC107) else Color.LightGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text("${displayReviews.size} ULASAN", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
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

                    if (displayReviews.isEmpty()) {
                        Text("Belum ada ulasan. Jadilah yang pertama!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        displayReviews.forEach { review ->
                            ReviewCardUI(
                                review = review,
                                onEdit = {
                                    editingReview = review
                                    showDialog = true
                                },
                                onDelete = {
                                    onDeleteReview(review) // Hapus Lokal
                                    Firebase.firestore.collection("reviews").document(review.id).delete() // Hapus Cloud
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (videoUrls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Video Relevan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Looping otomatis membaca data dari Firebase
                            items(videoUrls) { url ->
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            // Eksekusi buka link saat Box diklik
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // --- 1. GAMBAR BACKGROUND (THUMBNAIL) ---
                                    // Kita pakai foto destinasi biar nyambung sama halamannya
                                    AsyncImage(
                                        model = destination.imageUrl,
                                        contentDescription = "Thumbnail Video",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // --- 2. OVERLAY GELAP ---
                                    // Biar gambar destinasinya agak gelap & ikon play-nya kelihatan jelas
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f))
                                    )

                                    // --- 3. IKON PLAY DI TENGAH ---
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.8f), // Bikin putih transparan biar elegan
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.Black, // Play warna hitam biar kontras
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Button(
                onClick = onRouteClick,
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
                    modifier = Modifier.size(28.dp)
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

        if (selectedImage != null) {
            PhotoDetailScreen(imageUrl = selectedImage!!) { selectedImage = null }
        }
    }

    if (showDialog) {
        ReviewInputDialog(
            initialText = editingReview?.comment ?: "",
            initialRating = editingReview?.rating ?: 5.0,
            onDismiss = { showDialog = false },
            onSubmit = { ratingResult, commentResult ->
                val finalReview = editingReview?.copy(
                    rating = ratingResult,
                    comment = commentResult,
                    timestamp = System.currentTimeMillis()
                ) ?: Review(
                    id = java.util.UUID.randomUUID().toString(),
                    destinationId = destination.id,
                    userName = "Muhammad Ilham", // Sinkron dengan username di ReviewCardUI
                    userAvatarUrl = "",
                    rating = ratingResult,
                    comment = commentResult,
                    timestamp = System.currentTimeMillis()
                )

                onSaveReview(finalReview)

                Firebase.firestore.collection("reviews")
                    .document(finalReview.id)
                    .set(finalReview)

                showDialog = false
            }
        )
    }
}

@Composable
fun ReviewCardUI(review: Review, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Karena lu save pake nama asli, cek kepemilikannya juga harus pakai nama asli
    val isCurrentUser = review.userName == "Muhammad Ilham" || review.userName == "Anda"
    val bgColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 0.dp else 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    // Pakai huruf pertama dari nama buat avatar
                    Text(if (review.userName.isNotEmpty()) review.userName.take(1).uppercase() else "A", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Baru saja", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                if (isCurrentUser) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp).clickable { onEdit() })
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp).clickable { onDelete() })
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i <= review.rating.toInt()) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = review.comment, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
        }
    }
}

@Composable
fun ReviewInputDialog(
    initialText: String,
    initialRating: Double = 5.0,
    onDismiss: () -> Unit,
    onSubmit: (Double, String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var rating by remember { mutableDoubleStateOf(initialRating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialText.isEmpty()) "Tulis Ulasan" else "Edit Ulasan", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = i.toDouble() }
                                .padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("Bagaimana pengalamanmu di sini?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF005959))
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, text) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        }
    )
}