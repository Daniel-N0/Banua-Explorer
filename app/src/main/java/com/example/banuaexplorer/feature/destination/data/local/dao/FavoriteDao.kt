package com.example.banuaexplorer.feature.destination.data.local.dao

import androidx.room.*
import com.example.banuaexplorer.feature.destination.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    // Mengambil semua data yang di-love secara Real-Time (Flow)
    @Query("SELECT * FROM favorite_destinations")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    // Menyimpan data favorit baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    // Menghapus data dari daftar favorit
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
}