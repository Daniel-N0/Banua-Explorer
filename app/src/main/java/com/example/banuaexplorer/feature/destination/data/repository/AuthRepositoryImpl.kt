package com.example.banuaexplorer.feature.destination.data.repository // <--- Package udah disesuaikan!

import com.example.banuaexplorer.feature.destination.domain.model.User // <-- Pastikan import model User-mu benar
import com.example.banuaexplorer.feature.auth.domain.repository.AuthRepository // <-- Pastikan import AuthRepository-mu benar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "User Banua",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                Result.success(user)
            } else {
                Result.failure(kotlin.Exception("Login gagal: User tidak ditemukan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = firebaseUser.email ?: "",
                    photoUrl = ""
                )
                Result.success(user)
            } else {
                Result.failure(kotlin.Exception("Gagal membuat akun"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(
                    User(
                        id = firebaseUser.uid,
                        name = firebaseUser.displayName ?: "User Banua",
                        email = firebaseUser.email ?: "",
                        photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                    )
                )
            } else {
                trySend(null)
            }
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }
}