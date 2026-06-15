package com.example.banuaexplorer.feature.destination.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
    onSaveClick: (Destination) -> Unit,
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Batal")
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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = kabupaten,
                onValueChange = { kabupaten = it },
                label = { Text("Kabupaten/Kota") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategori (Alam, Budaya, Buatan)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Wisata") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // Bungkus ketikan jadi object Destination baru
                    val newDest = Destination(
                        id = destination?.id ?: UUID.randomUUID().toString(),
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
                enabled = name.isNotBlank() && kabupaten.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isEditMode) "Simpan Perubahan" else "Tambah Destinasi")
            }
        }
    }
}