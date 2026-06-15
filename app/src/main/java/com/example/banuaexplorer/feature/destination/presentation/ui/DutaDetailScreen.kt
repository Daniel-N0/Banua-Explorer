package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.banuaexplorer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DutaDetailScreen(
    ambassadorId: String,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val banuaGreen = MaterialTheme.colorScheme.primary

    // Teks default ambil dari stringResource juga kalau bisa
    var name by remember { mutableStateOf("Memuat...") }
    var title by remember { mutableStateOf("...") }
    var imageUrl by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("Memuat data profil...") }
    var followers by remember { mutableStateOf("0") }
    var activities by remember { mutableStateOf("0") }
    var campaigns by remember { mutableStateOf("0") }
    var instagramUrl by remember { mutableStateOf("") }

    val errorNoName = stringResource(R.string.tidak_ada_nama)
    val errorNoBio = stringResource(R.string.bio_tidak_tersedia)

    LaunchedEffect(ambassadorId) {
        if (ambassadorId.isNotEmpty()) {
            val db = Firebase.firestore
            db.collection("ambassadors")
                .document(ambassadorId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        name = doc.getString("name") ?: errorNoName
                        title = doc.getString("title") ?: "Putra Pariwisata"
                        imageUrl = doc.getString("imageUrl") ?: ""
                        instagramUrl = doc.getString("instagramUrl") ?: "https://www.instagram.com/"
                        bio = doc.getString("bio") ?: errorNoBio
                        followers = doc.getLong("followers")?.toString() ?: "0"
                        activities = doc.getLong("activities")?.toString() ?: "0"
                        campaigns = doc.getLong("campaigns")?.toString() ?: "0"
                    }
                }
                .addOnFailureListener { e -> name = "Error: ${e.message}" }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profil_duta), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = "Kembali") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(160.dp).clip(CircleShape).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(model = imageUrl, contentDescription = "Foto $name", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
            Text(text = title.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = banuaGreen, modifier = Modifier.padding(top = 4.dp), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStatItem(value = activities, label = stringResource(R.string.stat_aktivitas), icon = Icons.Default.Star)
                ProfileStatItem(value = campaigns, label = stringResource(R.string.stat_kampanye), icon = Icons.Default.Language)
                ProfileStatItem(value = followers, label = stringResource(R.string.stat_pengikut), icon = Icons.Default.CameraAlt)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    Text(text = stringResource(R.string.tentang), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = bio, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp, textAlign = TextAlign.Justify)

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = banuaGreen)
                    ) {
                        Text(stringResource(R.string.kunjungi_instagram), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        }
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}