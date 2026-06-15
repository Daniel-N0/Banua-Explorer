package com.example.banuaexplorer.feature.destination.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.banuaexplorer.datastore.LanguagePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val preference: LanguagePreference
) : ViewModel() {

    val isEnglish: StateFlow<Boolean> = preference.isEnglishFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun setLanguage(isEnglish: Boolean) {
        viewModelScope.launch {
            preference.saveLanguageSetting(isEnglish)
        }
    }
}

class LanguageViewModelFactory(
    private val preference: LanguagePreference
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
            return LanguageViewModel(preference) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}