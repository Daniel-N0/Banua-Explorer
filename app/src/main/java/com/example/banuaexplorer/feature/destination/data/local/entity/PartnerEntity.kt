package com.example.banuaexplorer.feature.destination.data.local.entity // Sesuaikan package-mu

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // Sponsor Utama / Mitra
    val imageUrl: String,
    val description: String,
    val website: String,
    val phone: String,
    val email: String
)