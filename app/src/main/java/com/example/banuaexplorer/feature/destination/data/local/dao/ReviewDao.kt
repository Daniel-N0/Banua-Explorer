package com.example.banuaexplorer.feature.destination.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.banuaexplorer.feature.destination.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    // BROWSE & READ: Mengambil daftar ulasan untuk suatu destinasi tertentu (diurutkan dari yang terbaru)
    @Query("SELECT * FROM reviews WHERE destinationId = :destId ORDER BY timestamp DESC")
    fun getReviewsByDestination(destId: String): Flow<List<ReviewEntity>>

    // ADD: Menambah ulasan baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    // EDIT: Memperbarui ulasan yang sudah ada
    @Update
    suspend fun updateReview(review: ReviewEntity)

    // DELETE: Menghapus ulasan
    @Delete
    suspend fun deleteReview(review: ReviewEntity)

    // (Opsional) Menghapus semua ulasan jika destinasi dihapus
    @Query("DELETE FROM reviews WHERE destinationId = :destId")
    suspend fun deleteReviewsByDestination(destId: String)
}