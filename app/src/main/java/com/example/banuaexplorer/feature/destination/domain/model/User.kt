package com.example.banuaexplorer.feature.destination.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String = "" // Opsional, siapa tahu nanti mau nampilin foto profil
)