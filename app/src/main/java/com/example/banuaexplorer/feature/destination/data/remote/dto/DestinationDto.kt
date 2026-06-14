package com.example.banuaexplorer.feature.destination.data.remote.dto

data class DestinationDto(
    val id: String = "",
    val name: String = "",
    val kabupaten: String = "",
    val description: String = "",
    val facilities: String = "",
    val dutaPick: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "",    // Untuk tab filter (Alam, Budaya, Kuliner, dll)
    val rating: Double = 0.0,     // Nilai rating desimal (misal: 4.9)
    val reviewCount: Int = 0      // Jumlah ulasan (misal: 1200)
)