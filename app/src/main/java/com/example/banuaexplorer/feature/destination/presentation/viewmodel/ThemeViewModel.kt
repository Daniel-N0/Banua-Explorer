package com.example.banuaexplorer.feature.destination.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.banuaexplorer.datastore.ThemePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val preference: ThemePreference
) : ViewModel() {

    val isDarkMode = preference.darkModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setDarkMode(enable: Boolean) {
        viewModelScope.launch {
            preference.saveDarkMode(enable)
        }
    }
}