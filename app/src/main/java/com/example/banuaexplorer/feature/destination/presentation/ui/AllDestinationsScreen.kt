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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel

@Composable
fun AllDestinationsScreen(
    viewModel: DestinationViewModel,
    onBackClick: () -> Unit,
    onDestinationClick: (Destination) -> Unit
) {
    // Hoisting state dari ViewModel
    val destinations by viewModel.destinations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Logika filter sebaiknya di ViewModel, tapi kita simpan di sini jika ViewModel belum diupdate
    val filteredList = remember(destinations, searchQuery, selectedRegion, selectedCategory) {
        destinations.filter { dest ->
            val matchRegion = if (selectedRegion == "Kalimantan Selatan") true 
                             else dest.kabupaten.contains(selectedRegion, ignoreCase = true)
            val matchSearch = if (searchQuery.isEmpty()) true 
                             else dest.name.contains(searchQuery, ignoreCase = true)
            val matchCat = if (selectedCategory == null) true 
                          else dest.category.equals(selectedCategory, ignoreCase = true)
            matchRegion && matchSearch && matchCat
        }
    }

    AllDestinationsContent(
        destinations = filteredList,
        searchQuery = searchQuery,
        selectedRegion = selectedRegion,
        selectedCategory = selectedCategory,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRegionSelect = viewModel::onRegionSelect,
        onCategorySelect = viewModel::onCategorySelect,
        onBackClick = onBackClick,
        onDestinationClick = onDestinationClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllDestinationsContent(
    destinations: List<Destination>,
    searchQuery: String,
    selectedRegion: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onRegionSelect: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onBackClick: () -> Unit,
    onDestinationClick: (Destination) -> Unit
) {
    Scaffold(
        topBar = {
            DestinationSearchHeader(
                searchQuery = searchQuery,
                selectedRegion = selectedRegion,
                selectedCategory = selectedCategory,
                onSearchQueryChange = onSearchQueryChange,
                onRegionSelect = onRegionSelect,
                onCategorySelect = onCategorySelect,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        DestinationGrid(
            destinations = destinations,
            modifier = Modifier.padding(padding),
            onDestinationClick = onDestinationClick
        )
    }
}

@Composable
private fun DestinationSearchHeader(
    searchQuery: String,
    selectedRegion: String,
    selectedCategory: String?,
    onSearchQueryChange: (String) -> Unit,
    onRegionSelect: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onBackClick: () -> Unit
) {
    val regions = remember { listOf("Kalimantan Selatan", "Banjarmasin", "Banjarbaru", "Banjar", "Barito Kuala", "Tapin", "Hulu Sungai Selatan", "Hulu Sungai Tengah", "Hulu Sungai Utara", "Balangan", "Tabalong", "Tanah Laut", "Tanah Bumbu", "Kotabaru") }
    
    // Gunakan Key (Internal) untuk logika dan StringRes untuk tampilan
    val categories = remember {
        listOf(
            Triple("Alam", R.string.cat_alam, R.drawable.ic_alam),
            Triple("Budaya", R.string.cat_budaya, R.drawable.ic_budaya),
            Triple("Kuliner", R.string.cat_kuliner, R.drawable.ic_kuliner),
            Triple("Sejarah", R.string.cat_sejarah, R.drawable.ic_sejarah),
            Triple("Religi", R.string.cat_religi, R.drawable.ic_religi)
        )
    }
    var expandedRegion by remember { mutableStateOf(false) }

    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            // Top Navigation Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.padding(start = 8.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimary)
                }
                Text(
                    text = stringResource(R.string.eksplor_wisata),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Region Selector
            Box(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expandedRegion = true }
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format(stringResource(R.string.explore_banua), selectedRegion),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                }
                DropdownMenu(expanded = expandedRegion, onDismissRequest = { expandedRegion = false }) {
                    regions.forEach { region ->
                        DropdownMenuItem(
                            text = { Text(region) },
                            onClick = { 
                                onRegionSelect(region)
                                expandedRegion = false 
                            }
                        )
                    }
                }
            }

            // Search Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(stringResource(R.string.search_placeholder), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Section
            CategoryFilterRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = onCategorySelect
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<Triple<String, Int, Int>>,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        categories.forEach { (categoryKey, stringRes, iconRes) ->
            val isSelected = categoryKey == selectedCategory
            CategoryItem(
                label = stringResource(id = stringRes),
                iconRes = iconRes,
                isSelected = isSelected,
                onClick = { onCategorySelect(if (isSelected) null else categoryKey) }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
            shadowElevation = if (isSelected) 4.dp else 0.dp,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun DestinationGrid(
    destinations: List<Destination>,
    modifier: Modifier = Modifier,
    onDestinationClick: (Destination) -> Unit
) {
    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (destinations.isEmpty()) {
            EmptyDestinationsPlaceholder()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(destinations, key = { it.id }) { destination ->
                    DestinationGridItem(destination, onDestinationClick)
                }
            }
        }
    }
}

@Composable
private fun EmptyDestinationsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.no_destinations),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun DestinationGridItem(destination: Destination, onClick: (Destination) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(destination) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Thumbnail
            if (destination.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

            } else {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = destination.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = destination.kabupaten,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
