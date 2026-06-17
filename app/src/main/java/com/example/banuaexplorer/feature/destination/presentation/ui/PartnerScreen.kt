package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import com.example.banuaexplorer.feature.destination.domain.model.Partner
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel

@Composable
fun PartnerScreen(
    viewModel: DestinationViewModel,
    onBackClick: () -> Unit,
    onNavigateToDutaDetail: (String) -> Unit,
    onSponsorClick: () -> Unit
) {
    val partners by viewModel.partners.collectAsState()
    val ambassadors by viewModel.ambassadors.collectAsState()

    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Setup untuk auto-focus keyboard
    val focusRequester = remember { FocusRequester() }

    // Otomatis menaikkan keyboard saat isSearchActive = true
    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus()
        }
    }

    val filteredPartners = partners.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val filteredAmbassadorsForSearch = ambassadors.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.kabupaten.contains(searchQuery, ignoreCase = true)
    }

    if (selectedGroup != null) {
        val filteredAmbassadors =
            when (selectedGroup) {
                "ALL" -> ambassadors
                "Duta Kota Banjarmasin" ->
                    ambassadors.filter {
                        it.kabupaten.contains("Banjarmasin", true)
                    }

                "Duta Kota Banjarbaru" ->
                    ambassadors.filter {
                        it.kabupaten.contains("Banjarbaru", true)
                    }

                else -> emptyList()
            }

        DutaDetailGroupScreen(
            groupName = if (selectedGroup == "ALL") stringResource(R.string.seluruh_duta) else selectedGroup!!,
            ambassadorList = filteredAmbassadors,
            onBackClick = { selectedGroup = null },
            onNavigateToDutaDetail = onNavigateToDutaDetail
        )
    } else {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            // Header Section yang sudah diperbaiki
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                if (isSearchActive) {
                    // Tampilan saat Pencarian Aktif
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                isSearchActive = false
                                searchQuery = ""
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester), // Pemicu Auto-focus
                            placeholder = { Text(stringResource(R.string.search)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.clickable { searchQuery = "" }
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                } else {
                    // Tampilan Normal (Pencarian Tidak Aktif)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onBackClick() }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.partnership_title),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { isSearchActive = true }
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (searchQuery.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(text = stringResource(R.string.collabs_label), color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = stringResource(R.string.membangun_banua), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                            Text(text = stringResource(R.string.bersama_mitra), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.duta_daerah),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = stringResource(R.string.lihat_semua),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable {
                                    selectedGroup = "ALL"
                                }
                            )
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            item {
                                DutaGroupCard(title = stringResource(R.string.duta_banjarbaru), subtitle = stringResource(R.string.banjarbaru_subtitle), badge = stringResource(R.string.putra_putri_pariwisata), imageUrl = "https://images.unsplash.com/photo-1542640244-7e672d6cef4e?q=80&w=600", onClick = { selectedGroup = "Duta Kota Banjarbaru" })
                            }
                            item {
                                DutaGroupCard(title = stringResource(R.string.duta_banjarmasin), subtitle = stringResource(R.string.banjarmasin_subtitle), badge = stringResource(R.string.nanang_galuh), imageUrl = "https://images.unsplash.com/photo-1588668214407-6ea9a6d8c272?q=80&w=600", onClick = { selectedGroup = "Duta Kota Banjarmasin" })
                            }
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        Text(text = stringResource(R.string.mitra_sponsor), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    }

                    // --- 1. PERBAIKAN: Meneruskan onClick saat pencarian kosong ---
                    items(partners) { partner ->
                        SponsorCard(partner = partner, onClick = { onSponsorClick() })
                    }
                } else {
                    // Search Results
                    if (filteredAmbassadorsForSearch.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = stringResource(R.string.duta_daerah),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(filteredAmbassadorsForSearch) { ambassador ->
                            IndividualAmbassadorCard(ambassador) { onNavigateToDutaDetail(ambassador.id) }
                        }
                    }

                    if (filteredPartners.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = stringResource(R.string.mitra_sponsor),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        // --- 2. PERBAIKAN: Meneruskan onClick saat menggunakan pencarian ---
                        items(filteredPartners) { partner ->
                            SponsorCard(partner = partner, onClick = { onSponsorClick() })
                        }
                    }

                    if (filteredAmbassadorsForSearch.isEmpty() && filteredPartners.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(R.string.tidak_ada_mitra),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun DutaDetailGroupScreen(groupName: String, ambassadorList: List<Ambassador>, onBackClick: () -> Unit, onNavigateToDutaDetail: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, stringResource(R.string.back), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onBackClick() })
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = groupName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        }
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)) {
            Text(text = stringResource(R.string.daftar_anggota_resmi), color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(text = stringResource(R.string.duta_pariwisata_kebudayaan), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
            items(ambassadorList) { IndividualAmbassadorCard(it) { onNavigateToDutaDetail(it.id) } }
        }
    }
}

@Composable
fun IndividualAmbassadorCard(ambassador: Ambassador, onBioClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(model = ambassador.imageUrl, contentDescription = null, modifier = Modifier.size(90.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(12.dp))
            Text(ambassador.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
            Text(String.format(stringResource(R.string.followers_count), ambassador.followers.toString()), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
            Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.clickable { onBioClick() }) {
                Text(text = stringResource(R.string.lihat_bio), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun DutaGroupCard(title: String, subtitle: String, badge: String, imageUrl: String, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.width(310.dp).clickable { onClick() }) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                AsyncImage(model = imageUrl, contentDescription = title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp), modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 12.dp)) {
                    Text(text = badge, color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

// --- 3. PERBAIKAN: Menambahkan onClick dan fungsi clickable ke Modifier Card ---
@Composable
fun SponsorCard(partner: Partner, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            AsyncImage(model = partner.imageUrl, contentDescription = partner.name, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = partner.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}