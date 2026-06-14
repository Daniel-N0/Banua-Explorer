package com.example.banuaexplorer.feature.destination.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_destinations")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val name: String,
    val kabupaten: String,
    val description: String,
    val category: String,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val dutaPick: String,
    val facilities: String,
    val rating: Double,
    val reviewCount: Int
)