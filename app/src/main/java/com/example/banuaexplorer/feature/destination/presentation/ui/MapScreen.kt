package com.example.banuaexplorer.feature.destination.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.banuaexplorer.BuildConfig
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.util.getCurrentLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun MapScreen(viewModel: DestinationViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val destinations by viewModel.destinations.collectAsState()
    val filteredDestinations by viewModel.filteredDestinations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLocationGranted by viewModel.isLocationPermissionGranted.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val routePoints by viewModel.routePoints.collectAsState()
    val routeDistance by viewModel.routeDistance.collectAsState()
    val routeDuration by viewModel.routeDuration.collectAsState()
    val distanceKm = routeDistance / 1000.0

    val totalMinutes = ceil(routeDuration / 60.0).toInt()
    val hour = totalMinutes / 60
    val minute = totalMinutes % 60

    val durationText = if (hour > 0) {
        String.format(stringResource(R.string.jam_menit), hour, minute)
    } else {
        String.format(stringResource(R.string.hanya_menit), totalMinutes)
    }

    val cameraPositionState = rememberCameraPositionState {
        val initialLatLng = userLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(-3.4413, 114.8295)
        position = CameraPosition.fromLatLngZoom(initialLatLng, if (userLocation != null) 15f else 11f)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearMapState()
            viewModel.onSearchQueryChange("")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updateLocationPermissionStatus(granted)
        if (granted) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateUserLocation(lat, lng)
                coroutineScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                }
            }
        }
    }

    LaunchedEffect(routePoints) {
        if (routePoints.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            routePoints.forEach { builder.include(it) }
            cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(builder.build(), 150))
        }
    }

    val selectedDestination by viewModel.selectedMapDestination.collectAsState()

    LaunchedEffect(isLocationGranted) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        viewModel.updateLocationPermissionStatus(hasPermission)
        if (hasPermission) {
            getCurrentLocation(context) { lat, lng ->
                viewModel.updateUserLocation(lat, lng)
                if (selectedDestination == null && routePoints.isEmpty()) {
                    coroutineScope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                    }
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(selectedDestination) {
        selectedDestination?.let { dest ->
            if (routePoints.isEmpty()) {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(dest.latitude, dest.longitude), 16f))
            }
        }
    }

    LaunchedEffect(userLocation, selectedDestination) {
        if (userLocation != null && selectedDestination != null) {
            viewModel.fetchRoute(apiKey = BuildConfig.ORS_API_KEY, start = userLocation!!, end = LatLng(selectedDestination!!.latitude, selectedDestination!!.longitude))
        }
    }

    LaunchedEffect(searchQuery, filteredDestinations) {
        if (searchQuery.isNotBlank() && filteredDestinations.size == 1) {
            val dest = filteredDestinations.first()
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(dest.latitude, dest.longitude), 16f))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, properties = MapProperties(isMyLocationEnabled = isLocationGranted)) {
            destinations.forEach { destination ->
                MarkerInfoWindow(
                    state = MarkerState(
                        position = LatLng(
                            destination.latitude,
                            destination.longitude
                        )
                    ),
                    title = destination.name,
                    snippet = destination.kabupaten,
                    onClick = {
                        viewModel.selectDestinationForMap(destination)
                        false
                    }
                )
            }

            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = MaterialTheme.colorScheme.primary,
                    width = 12f
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, start = 24.dp, end = 24.dp).align(Alignment.TopCenter), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text(stringResource(R.string.cari_di_peta), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
            )
        }

        if (routeDistance > 0) {
            Card(modifier = Modifier.align(Alignment.TopCenter).padding(top = 120.dp, start = 24.dp, end = 24.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.informasi_perjalanan), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    selectedDestination?.let { dest ->
                        Text(text = dest.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = {
                            val uri = Uri.parse("google.navigation:q=${dest.latitude},${dest.longitude}")
                            val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                            if (intent.resolveActivity(context.packageManager) != null) context.startActivity(intent)
                        }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text(text = stringResource(R.string.buka_di_google_maps), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = String.format(stringResource(R.string.jarak_km), distanceKm), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                        Text(text = String.format(stringResource(R.string.estimasi_waktu), durationText), color = MaterialTheme.colorScheme.tertiary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp)) {
            Text(text = stringResource(R.string.di_sekitar_anda), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp).background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp))
            if (filteredDestinations.isNotEmpty()) {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filteredDestinations) { dest -> MapDestinationCard(dest, onClick = { viewModel.selectDestinationForMap(dest) }) }
                }
            } else if (searchQuery.isNotBlank()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(16.dp), contentAlignment = Alignment.Center) { Text(text = stringResource(R.string.no_destinations), color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
fun MapDestinationCard(destination: Destination, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), modifier = Modifier.width(280.dp).clickable { onClick() }) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = destination.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = destination.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                Text(text = destination.kabupaten, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) { Text(text = stringResource(R.string.arahkan), color = MaterialTheme.colorScheme.onTertiaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp, 4.dp)) }
            }
        }
    }
}