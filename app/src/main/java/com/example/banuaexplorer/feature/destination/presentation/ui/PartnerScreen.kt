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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import com.example.banuaexplorer.feature.destination.domain.model.Partner
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.ui.theme.BanuaGreen
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.banuaexplorer.R

@Composable
fun PartnerScreen(
    viewModel: DestinationViewModel,
    onNavigateToDutaDetail: (String) -> Unit
) {
    val partners by viewModel.partners.collectAsState()
    val ambassadors by viewModel.ambassadors.collectAsState()

    var selectedGroup by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    if (selectedGroup != null) {
        val filteredAmbassadors = ambassadors.filter { ambassador ->
            if (selectedGroup == "Duta Kota Banjarmasin") {
                ambassador.kabupaten.contains("Banjarmasin", ignoreCase = true)
            } else if (selectedGroup == "Duta Kota Banjarbaru") {
                ambassador.kabupaten.contains("Banjarbaru", ignoreCase = true)
            } else {
                false
            }
        }

        DutaDetailGroupScreen(
            groupName = selectedGroup!!,
            ambassadorList = filteredAmbassadors,
            onBackClick = { selectedGroup = null },
            onNavigateToDutaDetail = onNavigateToDutaDetail
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BanuaGreen)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(stringResource(R.string.partnership_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BanuaGreen)
                }
                Icon(Icons.Default.Search, contentDescription = "Search", tint = BanuaGreen)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(stringResource(R.string.collabs_label), color = Color(0xFFF2C94C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.membangun_banua), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF001F1F))
                        Text(stringResource(R.string.bersama_mitra), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = BanuaGreen)
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.duta_daerah), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(stringResource(R.string.lihat_semua), color = BanuaGreen, fontSize = 12.sp)
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        item {
                            DutaGroupCard(
                                title = "Duta Kota Banjarbaru",
                                subtitle = "Representing the Idaman City",
                                badge = "PUTRA PUTRI PARIWISATA",
                                onClick = { selectedGroup = "Duta Kota Banjarbaru" }
                            )
                        }
                        item {
                            DutaGroupCard(
                                title = "Duta Kota Banjarmasin",
                                subtitle = "Representing the Thousand River City",
                                badge = "NANANG GALUH",
                                onClick = { selectedGroup = "Duta Kota Banjarmasin" }
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Text(stringResource(R.string.mitra_sponsor), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                }

                items(partners) { partner -> SponsorCard(partner) }

                item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun DutaDetailGroupScreen(
    groupName: String,
    ambassadorList: List<Ambassador>,
    onBackClick: () -> Unit,
    onNavigateToDutaDetail: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = BanuaGreen, modifier = Modifier.clickable { onBackClick() })
            Spacer(modifier = Modifier.width(16.dp))
            Text(groupName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BanuaGreen)
        }

        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 16.dp)) {
            Text(stringResource(R.string.daftar_anggota_resmi), color = Color(0xFFF2C94C), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(stringResource(R.string.duta_pariwisata_kebudayaan), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF001F1F))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(ambassadorList) { ambassador ->
                IndividualAmbassadorCard(ambassador = ambassador, onBioClick = { onNavigateToDutaDetail(ambassador.id) })
            }
            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun IndividualAmbassadorCard(ambassador: Ambassador, onBioClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = ambassador.imageUrl, contentDescription = null,
                modifier = Modifier.size(90.dp).clip(CircleShape).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(ambassador.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(String.format(stringResource(R.string.followers_count), ambassador.followers.toString()), fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp, bottom = 8.dp))
            Surface(
                color = BanuaGreen.copy(alpha = 0.1f), shape = CircleShape,
                modifier = Modifier.clickable { onBioClick() }
            ) {
                Text(stringResource(R.string.lihat_bio), color = BanuaGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun DutaGroupCard(title: String, subtitle: String, badge: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.width(310.dp).clickable { onClick() }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.LightGray)) {
                Surface(
                    color = BanuaGreen, shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 12.dp)
                ) {
                    Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SponsorCard(partner: Partner) {
    Card(
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF004D4D)), contentAlignment = Alignment.Center) {
                Text(partner.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(partner.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}