package com.example.banuaexplorer.feature.destination.domain.model

data class Partner(
    val id: String,
    val name: String,
    val type: String,
    val imageUrl: String,
    val description: String,
    val website: String,
    val phone: String,
    val email: String
)