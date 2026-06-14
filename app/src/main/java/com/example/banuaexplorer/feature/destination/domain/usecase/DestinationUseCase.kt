package com.example.banuaexplorer.feature.destination.domain.usecase

import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Partner // Import Partner
import com.example.banuaexplorer.feature.destination.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.Flow
import com.example.banuaexplorer.feature.destination.domain.model.Review

class DestinationUseCase(private val repository: DestinationRepository) {

    // 1. Fungsi Destinasi (Sudah ada sebelumnya)
    fun getDestinations(): Flow<List<Destination>> {
        return repository.getDestinationsFromLocal()
    }

    suspend fun refreshDestinations(): Result<Unit> {
        return repository.refreshDestinations()
    }

    // 2. Fungsi Partner (TAMBAHAN BARU SUPAYA VIEWMODEL GAK ERROR)
    fun getPartners(): Flow<List<Partner>> {
        return repository.getPartners()
    }

    suspend fun refreshPartners() {
        repository.refreshPartners()
    }

    fun getReviews(destId: String): Flow<List<Review>> {
        return repository.getReviewsByDestination(destId)
    }

    suspend fun addReview(review: Review) {
        repository.insertReview(review)
    }

    suspend fun removeReview(review: Review) {
        repository.deleteReview(review)
    }

    // Contoh penambahan fungsi di Repository / UseCase kamu:
    fun getLocalFavorites(): kotlinx.coroutines.flow.Flow<List<Destination>> {
        return repository.getLocalFavorites()
    }

    suspend fun saveFavorite(destination: Destination) {
        repository.saveFavorite(destination)
    }

    suspend fun removeFavorite(destination: Destination) {
        repository.removeFavorite(destination)
    }


    suspend fun deleteReview(review: Review) = repository.deleteReview(review)
}