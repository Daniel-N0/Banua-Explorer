package com.example.banuaexplorer.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "language_settings")

class LanguagePreference(private val context: Context) {
    private val IS_ENGLISH_KEY = booleanPreferencesKey("is_english")

    // Membaca status bahasa secara real-time
    val isEnglishFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_ENGLISH_KEY] ?: false
    }

    // Menyimpan status bahasa
    suspend fun saveLanguageSetting(isEnglish: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ENGLISH_KEY] = isEnglish
        }
    }
}