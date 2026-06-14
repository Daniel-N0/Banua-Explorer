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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.banuaexplorer.feature.destination.presentation.ui.MainScreen
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModelFactory
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModelFactory
import com.example.banuaexplorer.ui.theme.BanuaExplorerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    // ViewModel untuk Destinasi
    private val viewModel: DestinationViewModel by viewModels {
        val app = application as BanuaExplorerApplication
        DestinationViewModelFactory(app.container.destinationUseCase)
    }

    // ViewModel untuk Authentication
    private val authViewModel: AuthViewModel by viewModels {
        val app = application as BanuaExplorerApplication
        AuthViewModelFactory(app.container.authUseCase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Cloudinary
        try {
            val config = mapOf(
                "cloud_name" to "dzftert5l"
            )
            com.cloudinary.android.MediaManager.init(this, config)
        } catch (e: Exception) {
            // Menghindari crash jika Cloudinary sudah terinisialisasi
        }

        setContent {
            BanuaExplorerTheme {

                // Mengatur warna status bar dan navigation bar
                ManageSystemUI()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ManageSystemUI() {
    val systemUiController = rememberSystemUiController()
    val banuaGreen = Color(0xFF006666)
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