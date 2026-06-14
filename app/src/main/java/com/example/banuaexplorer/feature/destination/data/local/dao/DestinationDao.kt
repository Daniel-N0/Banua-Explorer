package com.example.banuaexplorer.feature.destination.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.banuaexplorer.feature.destination.data.local.entity.DestinationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinationDao {

    // 1. Mengambil seluruh data wisata untuk ditampilkan di Beranda (mendukung Flow secara Real-time)
    @Query("SELECT * FROM destinations")
    fun getAllDestinations(): Flow<List<DestinationEntity>>

    // 2. Memasukkan atau memperbarui data hasil sinkronisasi dari Firebase (Upsert)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestinations(destinations: List<DestinationEntity>)

    // 3. Menghapus seluruh data lokal saat user melakukan refresh total
    @Query("DELETE FROM destinations")
    suspend fun deleteAllDestinations()
}