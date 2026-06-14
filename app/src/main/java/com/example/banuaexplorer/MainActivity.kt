package com.example.banuaexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// --- Import Destinasi ---
import com.example.banuaexplorer.feature.destination.presentation.ui.MainScreen
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModelFactory

// --- Import Auth & Profile (Fitur Lu) ---
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModelFactory

// --- Import Dark Mode (Fitur Daniel) ---
import com.example.banuaexplorer.datastore.ThemePreference
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModelFactory

import com.example.banuaexplorer.ui.theme.BanuaExplorerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    // 1. ViewModel untuk Destinasi
    private val viewModel: DestinationViewModel by viewModels {
        val app = application as BanuaExplorerApplication
        DestinationViewModelFactory(app.container.destinationUseCase)
    }

    // 2. ViewModel untuk Authentication (Fitur Lu)
    private val authViewModel: AuthViewModel by viewModels {
        val app = application as BanuaExplorerApplication
        AuthViewModelFactory(app.container.authUseCase)
    }

    // 3. ViewModel untuk Theme/Dark Mode (Fitur Daniel)
    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModelFactory(
            ThemePreference(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Cloudinary (Fitur Lu)
        try {
            val config = mapOf(
                "cloud_name" to "dzftert5l"
            )
            com.cloudinary.android.MediaManager.init(this, config)
        } catch (e: Exception) {
            // Menghindari crash jika Cloudinary sudah terinisialisasi
        }

        setContent {
            // Ambil state Dark Mode dari Daniel
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            BanuaExplorerTheme(
                darkTheme = isDarkMode // Terapkan Dark Mode
            ) {

                ManageSystemUI()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    // Masukkan ke-3 ViewModel ke MainScreen
                    MainScreen(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ManageSystemUI() {
    val systemUiController = rememberSystemUiController()
    // Pakai warna dari theme (Punya Daniel) biar aman pas ganti mode
    val banuaGreen = MaterialTheme.colorScheme.primary
    val isLightTheme = MaterialTheme.colorScheme.background.value == Color.White.value

    SideEffect {
        // Status Bar
        systemUiController.setStatusBarColor(
            color = banuaGreen,
            darkIcons = false
        )

        // Navigation Bar
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = isLightTheme
        )
    }
}