package com.example.banuaexplorer.feature.destination.data.local.dao

import androidx.room.*
import com.example.banuaexplorer.feature.destination.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_destinations")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_destinations WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)
}