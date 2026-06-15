package com.example.banuaexplorer.feature.destination.presentation.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destination: Destination,
    reviews: List<Review>,
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

    var realGalleryUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var publicReviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var videoUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var instagramUrl by remember { mutableStateOf("") }
    var whatsappUrl by remember { mutableStateOf("") }

    val displayReviews = if (publicReviews.isNotEmpty()) publicReviews else reviews
    val averageRating = if (displayReviews.isNotEmpty()) displayReviews.sumOf { it.rating } / displayReviews.size else 0.0
    val formattedRating = if (averageRating > 0) String.format(java.util.Locale.US, "%.1f", averageRating) else "0.0"
    val context = LocalContext.current

    val currentUserAuth = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val activeUserName = currentUserAuth?.displayName ?: "Petualang Banua"

    Box(modifier = Modifier.fillMaxSize().background(backgroundGray)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(350.dp).background(banuaGreen),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.height(300.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(24.dp).padding(bottom = 100.dp)) {
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
                        Text(text = stringResource(id = R.string.minggu_buka), color = banuaGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = destination.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(stringResource(id = R.string.galeri_foto), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))

                    LaunchedEffect(destination.id) {
                        if (destination.id.isNotEmpty() && destination.id != "dummy_1") {
                            val db = Firebase.firestore
                            db.collection("destinations").whereEqualTo("id", destination.id).get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (!querySnapshot.isEmpty) {
                                        val doc = querySnapshot.documents[0]
                                        realGalleryUrls = doc.get("galleryUrls") as? List<String> ?: emptyList()
                                        videoUrls = doc.get("videoUrls") as? List<String> ?: emptyList()
                                        instagramUrl = doc.getString("instagramUrl") ?: ""
                                        whatsappUrl = doc.getString("whatsappUrl") ?: ""
                                    }
                                }

                            db.collection("reviews").whereEqualTo("destinationId", destination.id)
                                .addSnapshotListener { snapshot, error ->
                                    if (error != null) {
                                        Log.e("Firebase", "Gagal narik ulasan", error)
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null) {
                                        publicReviews = snapshot.documents.mapNotNull { it.toObject(Review::class.java) }.sortedByDescending { it.timestamp }
                                    }
                                }
                        }
                    }

                    if (realGalleryUrls.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(realGalleryUrls) { url ->
                                AsyncImage(
                                    model = url, contentDescription = null,
                                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)).clickable { selectedImage = url },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        Text(stringResource(id = R.string.memuat_galeri), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                if (instagramUrl.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
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
                                Text(
                                    String.format(stringResource(id = R.string.ulasan_count), displayReviews.size),
                                    fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { editingReview = null; showDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = banuaGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(stringResource(id = R.string.berikan_ulasan), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (displayReviews.isEmpty()) {
                        Text(stringResource(id = R.string.belum_ada_ulasan), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        displayReviews.forEach { review ->
                            ReviewCardUI(
                                review = review,
                                activeUserName = activeUserName,
                                onEdit = { editingReview = review; showDialog = true },
                                onDelete = {
                                    onDeleteReview(review)
                                    Firebase.firestore.collection("reviews").document(review.id).delete()
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (videoUrls.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(stringResource(id = R.string.video_relevan), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(videoUrls) { url ->
                                Box(
                                    modifier = Modifier.width(120.dp).height(200.dp).clip(RoundedCornerShape(16.dp)).clickable {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(model = destination.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(44.dp)) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
            Button(
                onClick = onRouteClick,
                modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = banuaGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.rute_perjalanan), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) }
            IconButton(onClick = onFavoriteClick) { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = if (isFavorite) Color.Red else Color.White, modifier = Modifier.size(28.dp)) }
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
                    rating = ratingResult, comment = commentResult, timestamp = System.currentTimeMillis()
                ) ?: Review(
                    id = java.util.UUID.randomUUID().toString(), destinationId = destination.id,
                    userName = activeUserName, userAvatarUrl = "", rating = ratingResult,
                    comment = commentResult, timestamp = System.currentTimeMillis()
                )
                onSaveReview(finalReview)
                Firebase.firestore.collection("reviews").document(finalReview.id).set(finalReview)
                showDialog = false
            }
        )
    }
}

@Composable
fun ReviewCardUI(review: Review, activeUserName: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isCurrentUser = review.userName == activeUserName || review.userName == "Anda"
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
                    Text(if (review.userName.isNotEmpty()) review.userName.take(1).uppercase() else "A", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = review.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = stringResource(id = R.string.baru_saja), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
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
                    Icon(Icons.Default.Star, contentDescription = null, tint = if (i <= review.rating.toInt()) Color(0xFFFFC107) else Color.LightGray, modifier = Modifier.size(14.dp))
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
        title = { Text(if (initialText.isEmpty()) stringResource(id = R.string.tulis_ulasan) else stringResource(id = R.string.edit_ulasan), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = Icons.Default.Star, contentDescription = null,
                            tint = if (i <= rating) Color(0xFFFFC107) else Color.LightGray,
                            modifier = Modifier.size(36.dp).clickable { rating = i.toDouble() }.padding(4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = text, onValueChange = { text = it },
                    placeholder = { Text(stringResource(id = R.string.pengalaman_placeholder)) },
                    modifier = Modifier.fillMaxWidth(), minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, text) }, enabled = text.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text(stringResource(id = R.string.simpan), color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.batal), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        }
    )
}