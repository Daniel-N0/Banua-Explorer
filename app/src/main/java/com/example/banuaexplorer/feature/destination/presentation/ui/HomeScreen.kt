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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import androidx.compose.foundation.lazy.items
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import androidx.compose.ui.text.style.TextOverflow
import com.example.banuaexplorer.ui.theme.BanuaGreen
val BackgroundGray = Color(0xFFF8F9FA)

@Composable
fun HomeScreen(
    viewModel: DestinationViewModel,
    onDestinationClick: (Destination) -> Unit,
    onProfileClick: () -> Unit,
    onAmbassadorClick: () -> Unit,
    onSeeAllClick: () -> Unit // <-- 1. TAMBAHKAN INI
) {
    val destinations by viewModel.destinations.collectAsState()
    val ambassadors by viewModel.ambassadors.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // --- STATE UNTUK FILTER, PENCARIAN, DAN KATEGORI ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Kalimantan Selatan") }
    var selectedCategory by remember { mutableStateOf<String?>(null) } // <-- State baru untuk Kategori

    // --- LOGIKA FILTER CERDAS (Mencakup ke-3 filter sekaligus) ---
    val filteredDestinations = destinations.filter { destination ->
        // 1. Filter Wilayah
        val matchRegion = if (selectedRegion == "Kalimantan Selatan") {
            true
        } else {
            destination.kabupaten.contains(selectedRegion, ignoreCase = true)
        }

        // 2. Filter Pencarian
        val matchSearch = if (searchQuery.isEmpty()) {
            true
        } else {
            destination.name.contains(searchQuery, ignoreCase = true)
        }

        // 3. Filter Kategori
        // PENTING: Pastikan di model data 'Destination' kamu sudah ada variabel 'kategori'
        val matchCategory = if (selectedCategory == null) {
            true
        } else {
            // Ganti 'kategori' dengan nama variabel di modelmu jika beda (misal: 'category')
            destination.category.equals(selectedCategory, ignoreCase = true)
        }

        matchRegion && matchSearch && matchCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(240.dp))

            // 👇 Oper state Kategori ke komponen UI-nya 👇
            CategorySection(
                selectedCategory = selectedCategory,
                onCategoryClick = { category ->
                    // Logika Toggle: Kalau diklik lagi kategori yang sama, filter dibatalkan
                    selectedCategory = if (selectedCategory == category) null else category
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DestinationRecommendationSection(
                destinations = filteredDestinations,
                onDestinationClick = onDestinationClick ,
                onSeeAllClick = onSeeAllClick // <-- 2. TAMBAHKAN INI
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
                onAmbassadorClick = onAmbassadorClick
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
    // Daftar 13 Kabupaten/Kota + Provinsi
    val regions = listOf(
        "Kalimantan Selatan", "Banjarmasin", "Banjarbaru", "Banjar",
        "Barito Kuala", "Tapin", "Hulu Sungai Selatan", "Hulu Sungai Tengah",
        "Hulu Sungai Utara", "Balangan", "Tabalong", "Tanah Laut",
        "Tanah Bumbu", "Kotabaru"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(BanuaGreen)
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
                    Text("Selamat Malam, Ilham!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    Text("Jumat, 12 Juni 2026", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- SISTEM DROPDOWN FILTER WILAYAH ---
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { expanded = true }
                                .padding(vertical = 4.dp)
                                // Opsional: tambah fillMaxWidth di dalam klik ini agar area sentuhnya luas
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Jelajahi wisata di $selectedRegion",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                // 1. TURUNKAN FONT SIZE JADI 15.sp ATAU 14.sp
                                fontSize = 15.sp,
                                maxLines = 1,
                                // 2. WAJIB: Kasih weight biar teks ngambil sisa ruang tapi ngalah sama Icon
                                modifier = Modifier.weight(1f),
                                // 3. WAJIB: Kasih Ellipsis buat jaga-jaga kalau di HP layar kecil banget
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Pilih Daerah",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            regions.forEach { region ->
                                DropdownMenuItem(
                                    text = { Text(region, color = BanuaGreen, fontWeight = FontWeight.Medium) },
                                    onClick = {
                                        onRegionChange(region)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { onProfileIconClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("I", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SISTEM PENCARIAN ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Cari destinasi wisata...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BanuaGreen) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    focusedTextColor = BanuaGreen,
                    unfocusedTextColor = BanuaGreen
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ... [KODE CATEGORY SECTION TETAP SAMA] ...
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
            // Cek apakah kategori ini sedang aktif ditekan
            val isSelected = category == selectedCategory

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Sensor klik untuk memilih kategori
                modifier = Modifier.clickable { onCategoryClick(category) }
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    // Ganti warna latar kalau terpilih
                    color = if (isSelected) BanuaGreen else Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = category.take(1),
                            // Ganti warna huruf kalau terpilih
                            color = if (isSelected) Color.White else BanuaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category,
                    fontSize = 12.sp,
                    // Huruf jadi lebih tebal kalau terpilih
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = BanuaGreen
                )
            }
        }
    }
}

// ... [KODE DESTINATION RECOMMENDATION SECTION TETAP SAMA] ...
@Composable
fun DestinationRecommendationSection(destinations: List<Destination>, onDestinationClick: (Destination) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Rekomendasi Destinasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Lihat Semua",
                color = BanuaGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    // Panggil navigasi ke halaman baru
                    onSeeAllClick()
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (destinations.isEmpty()) {
            Text("Tidak ada destinasi ditemukan.", color = Color.Gray, modifier = Modifier.padding(horizontal = 24.dp))
        } else {
            LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(destinations.size) { index ->
                    val destination = destinations[index]
                    Card(modifier = Modifier.width(220.dp).clickable { onDestinationClick(destination) }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(BanuaGreen))
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(destination.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(destination.kabupaten, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... [KODE EVENT BANNER SECTION & EVENT CARD TETAP SAMA] ...
@Composable
fun EventBannerSection() {
    val dummyEvents = listOf(
        EventItem("Festival Pasar Terapung 2026", "Siring Menara Pandang", "COMING SOON"),
        EventItem("Pesona Budaya Banjar", "Taman Budaya Kalsel", "HARI INI"),
        EventItem("Lomba Jukung Tradisional", "Sungai Martapura", "MINGGU DEPAN")
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Event Mendatang", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp))
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
    Surface(shape = RoundedCornerShape(16.dp), color = BanuaGreen, modifier = Modifier.width(280.dp).height(100.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = event.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = event.location, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFC107)) {
                Text(text = event.status, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp))
            }
        }
    }
}

// 👇 INI YANG HILANG DAN BIKIN ERROR (Data struktur Event) 👇
data class EventItem(val title: String, val location: String, val status: String)

@Composable
fun TourPackageSection(onPackageClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Penawaran Paket Wisata", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(12.dp))

        val dummyPackages = listOf("3H2M Susur Loksado" to "Rp 1.200.000", "2H1M Pasar Terapung" to "Rp 850.000")

        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(dummyPackages.size) { index ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { onPackageClick(dummyPackages[index].first) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(BanuaGreen), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(dummyPackages[index].first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(dummyPackages[index].second, color = BanuaGreen, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
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
fun AmbassadorSection(ambassadors: List<Ambassador>, onAmbassadorClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Duta Daerah", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Lihat Semua", color = BanuaGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onAmbassadorClick() })
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ambassadors) { ambassador ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onAmbassadorClick() }
                ) {
                    Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF1A1A1A)))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = ambassador.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Surface(color = Color(0xFFFFC107), shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = "DUTA", fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}