package com.example.banuaexplorer.feature.destination.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage // <-- Import untuk menampilkan gambar dari URL
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

@Composable
fun HomeScreen(
    viewModel: DestinationViewModel,
    onDestinationClick: (Destination) -> Unit,
    onProfileClick: () -> Unit,
    onAmbassadorClick: (Ambassador) -> Unit,
    onSeeAllAmbassadorClick: () -> Unit,
    onSeeAllClick: () -> Unit
) {
    val destinations by viewModel.destinations.collectAsState()
    val ambassadors by viewModel.ambassadors.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // --- STATE UNTUK FILTER, PENCARIAN, DAN KATEGORI ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Kalimantan Selatan") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // --- LOGIKA FILTER CERDAS ---
    val filteredDestinations = destinations.filter { destination ->
        val matchRegion = if (selectedRegion == "Kalimantan Selatan") true else destination.kabupaten.contains(selectedRegion, ignoreCase = true)
        val matchSearch = if (searchQuery.isEmpty()) true else destination.name.contains(searchQuery, ignoreCase = true)
        val matchCategory = if (selectedCategory == null) true else destination.category.equals(selectedCategory, ignoreCase = true)

        matchRegion && matchSearch && matchCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Adaptif Dark Mode Daniel
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(240.dp))

            CategorySection(
                selectedCategory = selectedCategory,
                onCategoryClick = { category ->
                    selectedCategory = if (selectedCategory == category) null else category
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DestinationRecommendationSection(
                destinations = filteredDestinations,
                onDestinationClick = onDestinationClick,
                onSeeAllClick = onSeeAllClick
            )

            Spacer(modifier = Modifier.height(32.dp))
            EventBannerSection()
            Spacer(modifier = Modifier.height(32.dp))

            TourPackageSection(onPackageClick = { packageName ->
                Toast.makeText(context, "Detail $packageName akan segera hadir!", Toast.LENGTH_SHORT).show()
            })

            Spacer(modifier = Modifier.height(32.dp))

            AmbassadorSection(
                ambassadors = ambassadors,
                onAmbassadorClick = onAmbassadorClick,
                onSeeAllClick = onSeeAllAmbassadorClick
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            HomeHeader(
                onProfileIconClick = onProfileClick,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedRegion = selectedRegion,
                onRegionChange = { selectedRegion = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(
    onProfileIconClick: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedRegion: String,
    onRegionChange: (String) -> Unit
) {
    val regions = listOf(
        "Kalimantan Selatan", "Banjarmasin", "Banjarbaru", "Banjar",
        "Barito Kuala", "Tapin", "Hulu Sungai Selatan", "Hulu Sungai Tengah",
        "Hulu Sungai Utara", "Balangan", "Tabalong", "Tanah Laut",
        "Tanah Bumbu", "Kotabaru"
    )
    var expanded by remember { mutableStateOf(false) }

    // Logika Firebase Lu
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val fullName = currentUser?.displayName ?: "Petualang"
    val profilePhotoUrl = currentUser?.photoUrl?.toString() ?: ""
    val firstName = fullName.split(" ").firstOrNull() ?: "Petualang"

    // Logika Waktu Lu
    val greetingTime = getGreetingMessage()
    val todayDate = getCurrentDate()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(MaterialTheme.colorScheme.primary) // Adaptif Dark Mode
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Teks Dinamis Lu dibalut Warna Daniel
                    Text(
                        text = "$greetingTime, $firstName!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = todayDate,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dropdown Wilayah
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { expanded = true }
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Jelajahi wisata di $selectedRegion",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Pilih Daerah",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            regions.forEach { region ->
                                DropdownMenuItem(
                                    text = { Text(region, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        onRegionChange(region)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Foto Profil Dinamis Lu dibalut Warna Daniel
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
                        .clickable { onProfileIconClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePhotoUrl.isNotBlank()) {
                        AsyncImage(
                            model = profilePhotoUrl,
                            contentDescription = "Profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = firstName.take(1).uppercase(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pencarian
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari destinasi wisata...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CategorySection(
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf("Alam", "Budaya", "Kuliner", "Sejarah", "Religi")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onCategoryClick(category) }
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = category.take(1),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DestinationRecommendationSection(destinations: List<Destination>, onDestinationClick: (Destination) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Rekomendasi Destinasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "Lihat Semua",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (destinations.isEmpty()) {
            Text("Tidak ada destinasi ditemukan.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 24.dp))
        } else {
            LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(destinations.size) { index ->
                    val destination = destinations[index]
                    Card(modifier = Modifier.width(220.dp).clickable { onDestinationClick(destination) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column {
                            // FOTO DESTINASI DARI DATABASE
                            AsyncImage(
                                model = destination.imageUrl,
                                contentDescription = destination.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(destination.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(destination.kabupaten, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventBannerSection() {
    val dummyEvents = listOf(
        EventItem("Festival Pasar Terapung 2026", "Siring Menara Pandang", "COMING SOON"),
        EventItem("Pesona Budaya Banjar", "Taman Budaya Kalsel", "HARI INI"),
        EventItem("Lomba Jukung Tradisional", "Sungai Martapura", "MINGGU DEPAN")
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Event Mendatang", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(dummyEvents) { event ->
                EventCard(event = event)
            }
        }
    }
}

@Composable
fun EventCard(event: EventItem) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(280.dp).height(100.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.location, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFC107)) {
                Text(text = event.status, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
            }
        }
    }
}

data class EventItem(val title: String, val location: String, val status: String)

@Composable
fun TourPackageSection(onPackageClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Penawaran Paket Wisata", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(12.dp))

        val dummyPackages = listOf("3H2M Susur Loksado" to "Rp 1.200.000", "2H1M Pasar Terapung" to "Rp 850.000")

        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(dummyPackages.size) { index ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { onPackageClick(dummyPackages[index].first) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(dummyPackages[index].first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(dummyPackages[index].second, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(color = Color(0xFFFFC107), shape = RoundedCornerShape(8.dp)) {
                                Text("Detail", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AmbassadorSection( ambassadors: List<Ambassador>, onAmbassadorClick: (Ambassador) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Duta Daerah", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("Lihat Semua", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onSeeAllClick() })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ambassadors) { ambassador ->
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .clickable {
                            onAmbassadorClick(ambassador)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AsyncImage(
                            model = ambassador.imageUrl,
                            contentDescription = ambassador.name,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = ambassador.name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Banjarbaru",
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- FUNGSI SENSOR WAKTU LU ---
fun getGreetingMessage(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 0..10 -> "Selamat Pagi"
        in 11..14 -> "Selamat Siang"
        in 15..17 -> "Selamat Sore"
        else -> "Selamat Malam"
    }
}

fun getCurrentDate(): String {
    val localeID = Locale("id", "ID")
    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", localeID)
    return formatter.format(java.util.Date())
}