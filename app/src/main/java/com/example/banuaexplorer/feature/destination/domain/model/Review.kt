package com.example.banuaexplorer.feature.destination.domain.model

data class Review(
    val id: String = "",
    val destinationId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val rating: Double = 0.0, // Pakai Double sesuai bawaan lu
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)