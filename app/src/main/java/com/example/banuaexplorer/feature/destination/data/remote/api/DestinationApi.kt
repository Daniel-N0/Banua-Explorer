package com.example.banuaexplorer.feature.destination.data.remote.api

import com.example.banuaexplorer.feature.destination.data.remote.dto.DestinationDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinationApi(private val firestore: FirebaseFirestore) {

    suspend fun getAllDestinations(): List<DestinationDto> {
        return try {
            val snapshot = firestore.collection("destinations").get().await()
            snapshot.toObjects(DestinationDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}