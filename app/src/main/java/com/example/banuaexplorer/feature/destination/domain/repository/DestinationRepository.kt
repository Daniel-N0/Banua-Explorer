package com.example.banuaexplorer.feature.destination.domain.repository

import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Partner // <-- TAMBAHKAN INI
import kotlinx.coroutines.flow.Flow
import com.example.banuaexplorer.feature.destination.domain.model.Review


interface DestinationRepository {
    fun getDestinationsFromLocal(): Flow<List<Destination>>
    suspend fun refreshDestinations(): Result<Unit>

    // Ini harusnya sekarang sudah tidak merah
    fun getPartners(): Flow<List<Partner>>
    suspend fun refreshPartners()

    fun getReviewsByDestination(destId: String): Flow<List<Review>>
    suspend fun insertReview(review: Review)
    suspend fun deleteReview(review: Review)

    fun getLocalFavorites(): Flow<List<Destination>>
    suspend fun saveFavorite(destination: Destination)
    suspend fun removeFavorite(destination: Destination)
}