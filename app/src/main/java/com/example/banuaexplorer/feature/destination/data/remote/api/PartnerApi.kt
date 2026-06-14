package com.example.banuaexplorer.feature.destination.data.remote.api

import com.example.banuaexplorer.feature.destination.data.remote.dto.PartnerDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PartnerApi(private val firestore: FirebaseFirestore) {

    suspend fun getPartners(): List<PartnerDto> {
        return try {
            // Mengambil data dari koleksi "partners" di Firebase
            val snapshot = firestore.collection("partners").get().await()
            snapshot.toObjects(PartnerDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}