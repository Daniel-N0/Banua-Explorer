package com.example.banuaexplorer.feature.destination.presentation.ui

import android.widget.Toast
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Ambassador
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: DestinationViewModel,
    onDestinationClick: (Destination) -> Unit,
    onProfileClick: () -> Unit,
    onAmbassadorClick: (String) -> Unit, // Tetap menggunakan String (ID) sesuai best practice
    onSeeAllAmbassadorClick: () -> Unit, // Ditambahkan dari kode temanmu
    onSeeAllClick: () -> Unit
) {
    val destinations by viewModel.destinations.collectAsState()
    val ambassadors by viewModel.ambassadors.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("Kalimantan Selatan") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredDestinations = destinations.filter { destination ->
        val matchRegion = if (selectedRegion == "Kalimantan Selatan") true else destination.kabupaten.contains(selectedRegion, ignoreCase = true)
        val matchSearch = if (searchQuery.isEmpty()) true else destination.name.contains(searchQuery, ignoreCase = true)
        val matchCategory = if (selectedCategory == null) true else destination.category.equals(selectedCategory, ignoreCase = true)
        matchRegion && matchSearch && matchCategory
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(bottom = 100.dp)) {
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

            val comingSoonFormat = stringResource(id = R.string.detail_coming_soon)
            TourPackageSection(onPackageClick = { packageName ->
                Toast.makeText(context, String.format(comingSoonFormat, packageName), Toast.LENGTH_SHORT).show()
            })

            Spacer(modifier = Modifier.height(32.dp))

            AmbassadorSection(
                ambassadors = ambassadors,
                onAmbassadorClick = onAmbassadorClick,
                onSeeAllClick = onSeeAllAmbassadorClick // Terhubung dengan baik
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

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val fullName = currentUser?.displayName ?: stringResource(id = R.string.greeting_petualang)
    val profilePhotoUrl = currentUser?.photoUrl?.toString() ?: ""
    val firstName = fullName.split(" ").firstOrNull() ?: stringResource(id = R.string.greeting_petualang)

    val greetingTime = getGreetingMessage()
    val todayDate = getCurrentDate()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(MaterialTheme.colorScheme.primary)
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

                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { expanded = true }
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = String.format(stringResource(id = R.string.explore_banua), selectedRegion),
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(stringResource(id = R.string.search_placeholder), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
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