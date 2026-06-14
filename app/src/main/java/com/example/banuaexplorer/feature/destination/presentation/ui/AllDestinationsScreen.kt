package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDestinationsScreen(
    viewModel: DestinationViewModel,
    onBackClick: () -> Unit,
    onDestinationClick: (Destination) -> Unit
) {
    val destinations by viewModel.destinations.collectAsState()
    val banuaGreen = Color(0xFF006666)
    val backgroundGray = Color(0xFFF8F9FA)

    // --- STATE FILTER ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Kalimantan Selatan") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val regions = listOf(
        "Kalimantan Selatan", "Banjarmasin", "Banjarbaru", "Banjar",
        "Barito Kuala", "Tapin", "Hulu Sungai Selatan", "Hulu Sungai Tengah",
        "Hulu Sungai Utara", "Balangan", "Tabalong", "Tanah Laut",
        "Tanah Bumbu", "Kotabaru"
    )
    val categories = listOf("Alam", "Budaya", "Kuliner", "Sejarah", "Religi")
    var expandedRegion by remember { mutableStateOf(false) }

    // --- LOGIKA FILTER ---
    val filteredList = destinations.filter { dest ->
        val matchRegion = if (selectedRegion == "Kalimantan Selatan") true else dest.kabupaten.contains(selectedRegion, ignoreCase = true)
        val matchSearch = if (searchQuery.isEmpty()) true else dest.name.contains(searchQuery, ignoreCase = true)
        // Pastikan variabel 'kategori' sesuai dengan yang ada di model Destination kamu
        val matchCat = if (selectedCategory == null) true else dest.category.equals(selectedCategory, ignoreCase = true)

        matchRegion && matchSearch && matchCat
    }

    Scaffold(
        topBar = {
            // --- HEADER PAKET LENGKAP ---
            Surface(
                shadowElevation = 8.dp,
                color = banuaGreen,
                modifier = Modifier.clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            ) {
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    // 1. Baris Judul & Back (PERBAIKAN ALIGNMENT)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Menggunakan Icon dengan padding manual agar rata kiri presisi
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clip(CircleShape)
                                .clickable { onBackClick() }
                                .padding(8.dp) // Area sentuh
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Eksplor Wisata Banua",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }

                    // 2. Baris Filter Wilayah (Dropdown)
                    Box(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { expandedRegion = true }
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedRegion, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                        }
                        DropdownMenu(expanded = expandedRegion, onDismissRequest = { expandedRegion = false }) {
                            regions.forEach { region ->
                                DropdownMenuItem(
                                    text = { Text(region) },
                                    onClick = { selectedRegion = region; expandedRegion = false }
                                )
                            }
                        }
                    }

                    // 3. Search Bar (PERBAIKAN TEKS VERTICAL CENTER)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari nama tempat...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = banuaGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        // .height(50.dp) DIHAPUS agar teks otomatis center
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Filter Kategori (GAYA HOMEPAGE)
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
                                modifier = Modifier.clickable {
                                    // Logika Toggle: Klik lagi untuk reset (Pilih Semua)
                                    selectedCategory = if (selectedCategory == category) null else category
                                }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    // Putih solid kalau dipilih, Putih transparan kalau tidak
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                    shadowElevation = if (isSelected) 4.dp else 0.dp,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = category.take(1),
                                            // Teks hijau kalau dipilih, Putih kalau tidak
                                            color = if (isSelected) banuaGreen else Color.White,
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
                                    // Teks bawah harus putih agar kontras dengan background header
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        // --- CONTENT GRID ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGray)
                .padding(padding)
        ) {
            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Destinasi tidak ditemukan", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredList) { destination ->
                        DestinationGridItem(destination, onDestinationClick)
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationGridItem(destination: Destination, onClick: (Destination) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(destination) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Gambar Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF006666).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF006666))
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = destination.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = destination.kabupaten, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                }
            }
        }
    }
}