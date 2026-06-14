package com.example.banuaexplorer.feature.destination.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.banuaexplorer.datastore.ThemePreference

class ThemeViewModelFactory(
    private val preference: ThemePreference
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            return ThemeViewModel(preference) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}