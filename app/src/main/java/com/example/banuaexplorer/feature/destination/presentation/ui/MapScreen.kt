package com.example.banuaexplorer.feature.destination.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.banuaexplorer.BuildConfig
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.util.getCurrentLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Polyline
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.CameraUpdateFactory
import kotlin.math.ceil
import android.content.Intent
import android.net.Uri

@Composable
fun MapScreen(viewModel: DestinationViewModel) {
    val context = LocalContext.current

    // --- MENGAMBIL STATE DARI VIEWMODEL ---
    val destinations by viewModel.destinations.collectAsState()
    val filteredDestinations by viewModel.filteredDestinations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLocationGranted by viewModel.isLocationPermissionGranted.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    // State rute yang diambil dari ViewModel (yang lama sudah dihapus)
    val routePoints by viewModel.routePoints.collectAsState()
    val routeDistance by viewModel.routeDistance.collectAsState()
    val routeDuration by viewModel.routeDuration.collectAsState()
    val distanceKm = routeDistance / 1000.0

    val totalMinutes = ceil(routeDuration / 60.0).toInt()

    val hour = totalMinutes / 60
    val minute = totalMinutes % 60

    val durationText =
        if (hour > 0) {
            "±${hour} jam ${minute} menit"
        } else {
            "±${totalMinutes} menit"
        }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-3.4413, 114.8295), 11f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updateLocationPermissionStatus(granted)
        if (granted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateUserLocation(lat, lng)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
            }
        }
    }

    LaunchedEffect(routePoints) {

        if (routePoints.isNotEmpty()) {

            val builder = LatLngBounds.Builder()

            routePoints.forEach {
                builder.include(it)
            }

            cameraPositionState.move(
                CameraUpdateFactory.newLatLngBounds(
                    builder.build(),
                    150
                )
            )
        }
    }

    LaunchedEffect(isLocationGranted) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.updateLocationPermissionStatus(hasPermission)

        if (hasPermission) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateUserLocation(lat, lng)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val selectedDestination by viewModel.selectedMapDestination.collectAsState()
    LaunchedEffect(selectedDestination) {
        selectedDestination?.let { dest ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(dest.latitude, dest.longitude), 16f
            )
        }
    }

    // PEMICU RUTE: Akan berjalan otomatis jika userLocation dan destinasi tujuan sudah ada nilainya
    LaunchedEffect(userLocation, selectedDestination) {
        if (userLocation != null && selectedDestination != null) {
            viewModel.fetchRoute(
                apiKey = BuildConfig.ORS_API_KEY,
                start = userLocation!!,
                end = LatLng(selectedDestination!!.latitude, selectedDestination!!.longitude)
            )
        }
    }

    LaunchedEffect(searchQuery, filteredDestinations) {
        if (searchQuery.isNotBlank() && filteredDestinations.size == 1) {
            val dest = filteredDestinations.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(dest.latitude, dest.longitude), 16f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. GOOGLE MAPS ---
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isLocationGranted)
        ) {
            destinations.forEach { destination ->
                Marker(
                    state = MarkerState(position = LatLng(destination.latitude, destination.longitude)),
                    title = destination.name,
                    snippet = destination.kabupaten
                )
            }

            // Menggambar garis rute jika datanya sudah didapatkan dari API
            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = Color.Blue,
                    width = 12f
                )
            }
        }

        // --- 2. FLOATING SEARCH BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Cari destinasi di peta...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }

        // --- KOTAK INFORMASI PERJALANAN ---
        if (routeDistance > 0) {

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(
                        top = 120.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Informasi Perjalanan",
                        style = MaterialTheme.typography.titleMedium,
                        color =  MaterialTheme.colorScheme.primary
                    )

                    selectedDestination?.let {
                        Text(
                            text = it.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF2B2D42)
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    selectedDestination?.let { destination ->

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}")
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp), // Buat tombol agak tinggi agar mudah diklik
                            shape = RoundedCornerShape(24.dp), // Buat ujungnya bulat sempurna ala kapsul
                            colors = ButtonDefaults.buttonColors(
                                containerColor =  MaterialTheme.colorScheme.primary // Beri warna hijau khas Banua
                            )
                        ) {
                            Text(
                                text = "Buka di Google Maps",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. MERAPIKAN TEKS JARAK & ESTIMASI KE KANAN-KIRI
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jarak: %.1f km".format(distanceKm),
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Estimasi: $durationText",
                            color = Color(0xFFB8860B), // Warna Saffron Gold yang agak gelap untuk aksen
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 3. FLOATING DESTINATION CARDS ---
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp)
        ) {
            Text(
                text = "Di Sekitar Anda",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF001F1F),
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            if (filteredDestinations.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDestinations) { dest ->
                        MapDestinationCard(
                            destination = dest,
                            onClick = {
                                viewModel.selectDestinationForMap(dest)
                            }
                        )
                    }
                }
            } else if (searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Destinasi tidak ditemukan", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun MapDestinationCard(destination: Destination, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background( MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint =  MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = destination.kabupaten,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFF2C94C).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Arahkan",
                        color = Color(0xFFB8860B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}