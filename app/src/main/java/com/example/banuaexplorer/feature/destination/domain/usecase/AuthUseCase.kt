package com.example.banuaexplorer.feature.destination.domain.usecase

import com.example.banuaexplorer.feature.destination.domain.model.User
import com.example.banuaexplorer.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlin.text.isBlank

class AuthUseCase(private val repository: AuthRepository) {

    suspend fun login(email: String, password: String): Result<User> {
        // Di sini kamu bisa nambahin validasi ekstra, misal ngecek email kosong atau nggak
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(kotlin.Exception("Email dan password tidak boleh kosong!"))
        }
        return repository.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): Result<User> {
        if (name.isBlank() || email.isBlank() || password.length < 6) {
            return Result.failure(kotlin.Exception("Data tidak valid atau password kurang dari 6 karakter!"))
        }
        return repository.register(name, email, password)
    }

    suspend fun logout() {
        repository.logout()
    }

    fun getCurrentUser(): Flow<User?> {
        return repository.getCurrentUser()
    }
}