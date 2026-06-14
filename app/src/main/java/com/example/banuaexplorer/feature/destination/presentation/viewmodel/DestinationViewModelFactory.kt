package com.example.banuaexplorer.feature.destination.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.banuaexplorer.feature.destination.domain.usecase.DestinationUseCase

class DestinationViewModelFactory(
    private val useCase: DestinationUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DestinationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DestinationViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}