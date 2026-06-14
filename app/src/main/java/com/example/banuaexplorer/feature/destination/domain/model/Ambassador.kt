package com.example.banuaexplorer.feature.destination.domain.model

data class Ambassador(
    val id: String,
    val name: String,
    val bio: String,
    val imageUrl: String,
    val followers: Int,
    val kabupaten: String // <--- Pastikan ini ada
    // (Boleh tambah title, activities, dll kalau nanti mau dipakai)
)