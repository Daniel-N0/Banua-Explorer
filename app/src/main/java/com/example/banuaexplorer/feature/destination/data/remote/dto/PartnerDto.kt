package com.example.banuaexplorer.feature.destination.data.remote.dto

data class PartnerDto(
    val id: String = "",
    val name: String = "",
    val type: String = "",        // Contoh: "Sponsor Utama"
    val imageUrl: String = "",
    val description: String = "",
    val website: String = "",
    val phone: String = "",
    val email: String = ""
)