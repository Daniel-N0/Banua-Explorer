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
import androidx.compose.ui.Modifier // <-- INI YANG BIKIN ERROR MODIFIER TADI
import androidx.compose.ui.graphics.Color
import com.example.banuaexplorer.feature.destination.presentation.ui.MainScreen
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModelFactory
import com.example.banuaexplorer.ui.theme.BanuaExplorerTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.banuaexplorer.datastore.ThemePreference
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModelFactory

class MainActivity : ComponentActivity() {

    // PERBAIKAN FINAL: Ambil useCase dari AppContainer (Arsitektur Manual DI milikmu)
    private val viewModel: DestinationViewModel by viewModels {
        val app = application as BanuaExplorerApplication
        // Sesuaikan 'container' dengan nama variabel di BanuaExplorerApplication kamu (bisa 'container' atau 'appContainer')
        DestinationViewModelFactory(app.container.destinationUseCase)
    }

    private val themeViewModel: ThemeViewModel by viewModels {
        ThemeViewModelFactory(
            ThemePreference(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            BanuaExplorerTheme(
                darkTheme = isDarkMode
            ) {

                ManageSystemUI()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {

                    MainScreen(
                        viewModel = viewModel,
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
    val banuaGreen = MaterialTheme.colorScheme.primary// Warna hijau ciri khas BanuaExplorer
    val isLightTheme = MaterialTheme.colorScheme.background.value == Color.White.value

    SideEffect {
        // 1. Mewarnai Status Bar (Area Atas) menjadi Hijau Banua
        systemUiController.setStatusBarColor(
            color = banuaGreen,
            darkIcons = false
        )

        // 2. Mewarnai Navigation Bar (Area Bawah) menjadi Transparan murni
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = isLightTheme
        )
    }
}