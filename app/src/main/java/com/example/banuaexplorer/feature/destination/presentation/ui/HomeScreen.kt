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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
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
    onAmbassadorClick: (Ambassador) -> Unit,
    onSeeAllAmbassadorClick: () -> Unit,
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

            CategorySection(selectedCategory = selectedCategory, onCategoryClick = { selectedCategory = if (selectedCategory == it) null else it })

            Spacer(modifier = Modifier.height(32.dp))

            DestinationRecommendationSection(destinations = filteredDestinations, onDestinationClick = onDestinationClick, onSeeAllClick = onSeeAllClick)

            Spacer(modifier = Modifier.height(32.dp))
            EventBannerSection()
            Spacer(modifier = Modifier.height(32.dp))

            val comingSoonFormat = stringResource(id = R.string.detail_coming_soon)
            TourPackageSection(onPackageClick = { Toast.makeText(context, String.format(comingSoonFormat, it), Toast.LENGTH_SHORT).show() })

            Spacer(modifier = Modifier.height(32.dp))

            AmbassadorSection(ambassadors = ambassadors, onAmbassadorClick = onAmbassadorClick, onSeeAllClick = onSeeAllAmbassadorClick)
        }

        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
            HomeHeader(onProfileIconClick = onProfileClick, searchQuery = searchQuery, onSearchChange = { searchQuery = it }, selectedRegion = selectedRegion, onRegionChange = { selectedRegion = it })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHeader(onProfileIconClick: () -> Unit, searchQuery: String, onSearchChange: (String) -> Unit, selectedRegion: String, onRegionChange: (String) -> Unit) {
    val regions = listOf("Kalimantan Selatan", "Banjarmasin", "Banjarbaru", "Banjar", "Barito Kuala", "Tapin", "Hulu Sungai Selatan", "Hulu Sungai Tengah", "Hulu Sungai Utara", "Balangan", "Tabalong", "Tanah Laut", "Tanah Bumbu", "Kotabaru")
    var expanded by remember { mutableStateOf(false) }

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val fullName = currentUser?.displayName ?: stringResource(id = R.string.greeting_petualang)
    val profilePhotoUrl = currentUser?.photoUrl?.toString() ?: ""
    val firstName = fullName.split(" ").firstOrNull() ?: stringResource(id = R.string.greeting_petualang)

    val greetingTime = getGreetingMessage()
    val todayDate = getCurrentDate()

    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)).background(MaterialTheme.colorScheme.primary)) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "$greetingTime, $firstName!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimary)
                    Text(text = todayDate, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Box {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { expanded = true }.fillMaxWidth()) {
                            Text(text = String.format(stringResource(id = R.string.explore_banua), selectedRegion), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, modifier = Modifier.weight(1f), overflow = TextOverflow.Ellipsis)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            regions.forEach { region -> DropdownMenuItem(text = { Text(region, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) }, onClick = { onRegionChange(region); expanded = false }) }
                        }
                    }
                }
                Box(modifier = Modifier.padding(start = 16.dp).size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)).clickable { onProfileIconClick() }, contentAlignment = Alignment.Center) {
                    if (profilePhotoUrl.isNotBlank()) { AsyncImage(model = profilePhotoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
                    else { Text(text = firstName.take(1).uppercase(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery, onValueChange = onSearchChange,
                placeholder = { Text(stringResource(id = R.string.search_placeholder), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent, focusedTextColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp), singleLine = true, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CategorySection(selectedCategory: String?, onCategoryClick: (String) -> Unit) {
    val categories = listOf(
        "Alam" to R.string.cat_alam,
        "Budaya" to R.string.cat_budaya,
        "Kuliner" to R.string.cat_kuliner,
        "Sejarah" to R.string.cat_sejarah,
        "Religi" to R.string.cat_religi
    )
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        categories.forEach { (categoryKey, stringResId) ->
            val isSelected = categoryKey == selectedCategory
            val categoryLabel = stringResource(id = stringResId)
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onCategoryClick(categoryKey) }) {
                Surface(shape = RoundedCornerShape(16.dp), color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, shadowElevation = 2.dp, modifier = Modifier.size(56.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(text = categoryLabel.take(1), color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = categoryLabel, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun DestinationRecommendationSection(destinations: List<Destination>, onDestinationClick: (Destination) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(id = R.string.rekomendasi_destinasi), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(stringResource(id = R.string.lihat_semua), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onSeeAllClick() })
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (destinations.isEmpty()) { Text(stringResource(id = R.string.no_destinations), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), modifier = Modifier.padding(horizontal = 24.dp)) }
        else { LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) { items(destinations) { dest ->
            Card(modifier = Modifier.width(220.dp).clickable { onDestinationClick(dest) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Column {
                    AsyncImage(model = dest.imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth().height(120.dp), contentScale = ContentScale.Crop)
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(dest.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(dest.kabupaten, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        } } }
    }
}

@Composable
fun EventBannerSection() {
    val dummyEvents = listOf(EventItem("Festival Pasar Terapung 2026", "Siring Menara Pandang", "COMING SOON"), EventItem("Pesona Budaya Banjar", "Taman Budaya Kalsel", "HARI INI"))
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.upcoming_events), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) { items(dummyEvents) { EventCard(it) } }
    }
}

@Composable
fun EventCard(event: EventItem) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(280.dp).height(100.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Text(event.location, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 11.sp, maxLines = 1)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFC107)) { Text(event.status, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(8.dp, 6.dp)) }
        }
    }
}

data class EventItem(val title: String, val location: String, val status: String)

@Composable
fun TourPackageSection(onPackageClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(id = R.string.tour_packages), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(12.dp))
        val pkgs = listOf("3H2M Susur Loksado" to "Rp 1.200.000", "2H1M Pasar Terapung" to "Rp 850.000")
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) { items(pkgs.size) { i ->
            Card(modifier = Modifier.width(260.dp).clickable { onPackageClick(pkgs[i].first) }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(pkgs[i].first, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(pkgs[i].second, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        } }
    }
}

@Composable
fun AmbassadorSection(ambassadors: List<Ambassador>, onAmbassadorClick: (Ambassador) -> Unit, onSeeAllClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(id = R.string.duta_daerah), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(stringResource(id = R.string.lihat_semua), color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable { onSeeAllClick() })
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ambassadors) { ambassador ->
                Card(modifier = Modifier.width(130.dp).clickable { onAmbassadorClick(ambassador) }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(model = ambassador.imageUrl, contentDescription = ambassador.name, modifier = Modifier.size(90.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(ambassador.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                        Surface(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(8.dp)) { Text("Banjarbaru", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(8.dp, 2.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun getGreetingMessage(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (h) {
        in 0..10 -> stringResource(id = R.string.good_morning)
        in 11..14 -> stringResource(id = R.string.good_afternoon)
        in 15..17 -> stringResource(id = R.string.good_evening)
        else -> stringResource(id = R.string.good_night)
    }
}

fun getCurrentDate(): String {
    return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date())
}