package com.example.banuaexplorer.feature.destination.data.repository

import com.example.banuaexplorer.feature.destination.data.local.dao.DestinationDao
import com.example.banuaexplorer.feature.destination.data.local.dao.FavoriteDao // <-- Tambahan import FavoriteDao
import com.example.banuaexplorer.feature.destination.data.local.dao.PartnerDao
import com.example.banuaexplorer.feature.destination.data.local.dao.ReviewDao
import com.example.banuaexplorer.feature.destination.data.mapper.toDomain
import com.example.banuaexplorer.feature.destination.data.mapper.toEntity
import com.example.banuaexplorer.feature.destination.data.remote.api.DestinationApi
import com.example.banuaexplorer.feature.destination.data.remote.api.PartnerApi
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Partner
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.example.banuaexplorer.feature.destination.domain.repository.DestinationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.banuaexplorer.feature.destination.data.local.entity.toEntity
import com.example.banuaexplorer.feature.destination.data.mapper.toFavoriteEntity
import com.example.banuaexplorer.feature.destination.data.mapper.toDomainModel


class DestinationRepositoryImpl(
    private val destinationDao: DestinationDao,
    private val partnerDao: PartnerDao,
    private val reviewDao: ReviewDao,
    private val favoriteDao: FavoriteDao,       // <--- TAMBAHAN FAVORITE DAO
    private val destinationApi: DestinationApi,
    private val partnerApi: PartnerApi
) : DestinationRepository {

    // --- Destinasi ---
    override fun getDestinationsFromLocal(): Flow<List<Destination>> =
        destinationDao.getAllDestinations().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshDestinations(): Result<Unit> = try {
        val remoteData = destinationApi.getAllDestinations()
        destinationDao.deleteAllDestinations()
        destinationDao.insertDestinations(remoteData.map { it.toEntity() })
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // --- Partner ---
    override fun getPartners(): Flow<List<Partner>> =
        partnerDao.getAllPartners().map { it.map { entity -> entity.toDomain() } }

    override suspend fun refreshPartners() {
        val remoteData = partnerApi.getPartners()
        partnerDao.clearPartners()
        partnerDao.insertPartners(remoteData.map { it.toEntity() })
    }

    // --- Review / Ulasan ---
    override fun getReviewsByDestination(destId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByDestination(destId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertReview(review: Review) {
        reviewDao.insertReview(review.toEntity())
    }


    // --- Favorite / Favorit (BARU) ---
    override fun getLocalFavorites(): Flow<List<Destination>> {
        return favoriteDao.getAllFavorites().map { list ->
            list.map { it.toDomainModel() }
        }
    }

    override suspend fun saveFavorite(destination: Destination) {
        favoriteDao.insertFavorite(destination.toFavoriteEntity())
    }

    override suspend fun removeFavorite(destination: Destination) {
        favoriteDao.deleteFavoriteById(destination.id)
    }

    override fun getReviews(destId: String): Flow<List<Review>> {
        return reviewDao.getReviewsByDestination(destId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addReview(review: Review) {
        reviewDao.insertReview(review.toEntity())
    }

    override suspend fun deleteReview(review: Review) {
        reviewDao.deleteReview(review.toEntity())
    }

    
}