package com.example.banuaexplorer.feature.destination.domain.model

data class Review(
    val id: String,
    val destinationId: String, // Untuk tahu ulasan ini buat wisata yang mana
    val userName: String,
    val userAvatarUrl: String, // Buat foto profil seperti di desain
    val rating: Double,
    val comment: String,
    val timestamp: Long // Untuk mengurutkan komentar terbaru
)