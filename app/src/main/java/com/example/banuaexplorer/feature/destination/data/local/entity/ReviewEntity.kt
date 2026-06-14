package com.example.banuaexplorer.feature.destination.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.banuaexplorer.feature.destination.domain.model.Review

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val destinationId: String,
    val userName: String,
    val userAvatarUrl: String,
    val rating: Double,
    val comment: String,
    val timestamp: Long
) {
    // Fungsi Mapper (Konversi ke Model Domain)
    fun toDomain() = Review(
        id = id,
        destinationId = destinationId,
        userName = userName,
        userAvatarUrl = userAvatarUrl,
        rating = rating,
        comment = comment,
        timestamp = timestamp
    )
}

// Fungsi Ekstensi (Konversi dari Domain ke Entity)
fun Review.toEntity() = ReviewEntity(
    id = id,
    destinationId = destinationId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    comment = comment,
    timestamp = timestamp
)