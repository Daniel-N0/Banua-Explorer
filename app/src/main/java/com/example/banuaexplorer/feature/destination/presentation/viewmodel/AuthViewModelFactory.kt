package com.example.banuaexplorer.feature.destination.presentation.viewmodel // Sesuaikan dengan package-mu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.banuaexplorer.feature.auth.domain.usecase.AuthUseCase
import kotlin.jvm.java

class AuthViewModelFactory(
    private val useCase: AuthUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(useCase) as T
        }
        throw kotlin.IllegalArgumentException("Unknown ViewModel class")
    }
}