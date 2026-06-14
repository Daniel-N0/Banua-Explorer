package com.example.banuaexplorer.feature.destination.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "destinations")
data class DestinationEntity(
    @PrimaryKey val id: String,
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