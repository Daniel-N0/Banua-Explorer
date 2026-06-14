package com.example.banuaexplorer.core.di

import android.content.Context
import androidx.room.Room
import com.example.banuaexplorer.feature.destination.domain.usecase.AuthUseCase
import com.example.banuaexplorer.feature.destination.data.local.AppDatabase
import com.example.banuaexplorer.feature.destination.data.remote.api.DestinationApi
import com.example.banuaexplorer.feature.destination.data.remote.api.PartnerApi
import com.example.banuaexplorer.feature.destination.data.repository.AuthRepositoryImpl
import com.example.banuaexplorer.feature.destination.data.repository.DestinationRepositoryImpl
import com.example.banuaexplorer.feature.destination.domain.repository.DestinationRepository
import com.example.banuaexplorer.feature.destination.domain.usecase.DestinationUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppContainer(private val context: Context) {

    // 1. Inisialisasi Firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    // 2. Inisialisasi API (Pipa penyedot)
    private val destinationApi = DestinationApi(firestore)
    private val partnerApi = PartnerApi(firestore)

    // 3. Inisialisasi Room
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "banua_explorer_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    // 4. Hubungkan ke Repository
    private val destinationRepository: DestinationRepository = DestinationRepositoryImpl(
        destinationDao = database.destinationDao(),
        partnerDao = database.partnerDao(),
        reviewDao = database.reviewDao(),
        favoriteDao = database.favoriteDao(),
        destinationApi = destinationApi,
        partnerApi = partnerApi
    )

    // Repository untuk Authentication
    private val authRepository = AuthRepositoryImpl(firebaseAuth)

    // 5. UseCase
    val destinationUseCase = DestinationUseCase(destinationRepository)

    // UseCase untuk Authentication
    val authUseCase = AuthUseCase(authRepository)
}