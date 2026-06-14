package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    destination: Destination? = null, // Kalau null berarti tambah baru, kalau ada isinya berarti edit
    onBackClick: () -> Unit,
    onSaveClick: (Destination) -> Unit
) {
    // State untuk menampung ketikan form
    var name by remember { mutableStateOf(destination?.name ?: "") }
    var kabupaten by remember { mutableStateOf(destination?.kabupaten ?: "") }
    var category by remember { mutableStateOf(destination?.category ?: "") }
    var description by remember { mutableStateOf(destination?.description ?: "") }

    // Cek apakah ini mode Edit atau Tambah
    val isEditMode = destination != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Wisata" else "Tambah Wisata", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Batal")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Destinasi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = kabupaten,
                onValueChange = { kabupaten = it },
                label = { Text("Kabupaten/Kota") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori (Alam, Budaya, Buatan)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Wisata") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Bungkus ketikan jadi object Destination baru
                    val newDest = Destination(
                        id = destination?.id ?: java.util.UUID.randomUUID().toString(),
                        name = name,
                        kabupaten = kabupaten,
                        category = category,
                        description = description,
                        imageUrl = destination?.imageUrl ?: "",
                        latitude = destination?.latitude ?: 0.0,
                        longitude = destination?.longitude ?: 0.0,

                        // --- PERBAIKAN DI SINI ---
                        dutaPick = destination?.dutaPick ?: "",  // Diubah jadi String kosong
                        facilities = destination?.facilities ?: "", // Diubah jadi String kosong
                        rating = destination?.rating ?: 0.0,
                        reviewCount = destination?.reviewCount ?: 0,
                        galleryUrls = emptyList()
                    )
                    onSaveClick(newDest)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotBlank() && kabupaten.isNotBlank()
            ) {
                Text(if (isEditMode) "Simpan Perubahan" else "Tambah Destinasi")
            }
        }
    }
}