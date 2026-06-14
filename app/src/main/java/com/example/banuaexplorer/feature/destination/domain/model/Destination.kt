package com.example.banuaexplorer.feature.destination.domain.model

data class Destination(
    val id: String,
    val name: String,
    val kabupaten: String,
    val description: String,
    val facilities: String,
    val dutaPick: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    val category: String,
    val rating: Double,
    val reviewCount: Int
)