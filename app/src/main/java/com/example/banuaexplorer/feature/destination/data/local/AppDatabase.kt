package com.example.banuaexplorer.feature.destination.data.local // Sesuaikan package-mu

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.banuaexplorer.feature.destination.data.local.dao.DestinationDao
import com.example.banuaexplorer.feature.destination.data.local.dao.PartnerDao
import com.example.banuaexplorer.feature.destination.data.local.dao.ReviewDao
import com.example.banuaexplorer.feature.destination.data.local.dao.FavoriteDao // <-- Import FavoriteDao
import com.example.banuaexplorer.feature.destination.data.local.entity.DestinationEntity
import com.example.banuaexplorer.feature.destination.data.local.entity.PartnerEntity
import com.example.banuaexplorer.feature.destination.data.local.entity.ReviewEntity
import com.example.banuaexplorer.feature.destination.data.local.entity.FavoriteEntity // <-- Import FavoriteEntity

// 1. Tambahkan FavoriteEntity ke dalam array dan naikkan versi ke 5
@Database(
    entities = [
        DestinationEntity::class,
        PartnerEntity::class,
        ReviewEntity::class,
        FavoriteEntity::class // <-- Tambahan tabel Favorit
    ],
    version = 5, // <-- Naikkan ke versi 5
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun destinationDao(): DestinationDao

    abstract fun partnerDao(): PartnerDao

    abstract fun reviewDao(): ReviewDao

    // 2. Tambahkan fungsi Dao untuk Favorite
    abstract fun favoriteDao(): FavoriteDao // <-- Akses ke tabel Favorit
}