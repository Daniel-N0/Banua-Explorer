package com.example.banuaexplorer.feature.destination.data.remote.dto

data class AmbassadorDto(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val kabupaten: String = "",
    val imageUrl: String = "",
    val campaigns: Int = 0,
    val followers: Int = 0,
    val activities: Int = 0,
    val bio: String = ""
)