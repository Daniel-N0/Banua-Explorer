package com.example.banuaexplorer.feature.auth.domain.repository

import com.example.banuaexplorer.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Fungsi untuk login, mengembalikan Result (Sukses berisi User, atau Gagal berisi Error)
    suspend fun login(email: String, password: String): Result<User>

    // Fungsi untuk register
    suspend fun register(name: String, email: String, password: String): Result<User>

    // Fungsi untuk logout
    suspend fun logout()

    // Mengecek siapa user yang sedang login saat ini (Real-time)
    fun getCurrentUser(): Flow<User?>
}