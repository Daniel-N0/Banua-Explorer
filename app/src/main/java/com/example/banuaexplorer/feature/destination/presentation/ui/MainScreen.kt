package com.example.banuaexplorer.feature.destination.presentation.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import androidx.compose.runtime.collectAsState
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModel
import com.example.banuaexplorer.ui.theme.BanuaGreen

@Composable
fun MainScreen(viewModel: DestinationViewModel,  themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    // Cek kapan Bottom Navbar harus muncul (Di halaman profil otomatis disembunyikan)
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Favorite.route,
        Screen.Map.route,
        Screen.Partner.route
    )

    // Box Utama pembungkus layar
    Box(modifier = Modifier.fillMaxSize()) {

        // Layar TV (NavHost)
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onDestinationClick = { destination ->
                        viewModel.selectDestination(destination)
                        navController.navigate(Screen.Detail.route)
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onAmbassadorClick = {
                        navController.navigate(Screen.Partner.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onSeeAllClick = { navController.navigate(Screen.AllDestinations.route) }
                )
            }

            composable(Screen.Favorite.route) {
                FavoriteScreen(
                    viewModel = viewModel,
                    onDestinationClick = { destination ->

                        // Simpan destinasi yang dipilih
                        viewModel.selectDestination(destination)

                        // Pindah ke Detail
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(viewModel = viewModel)
            }
            composable(Screen.Partner.route) {
                PartnerScreen(viewModel = viewModel)
            }
            composable(Screen.Detail.route) {
                val destinations by viewModel.destinations.collectAsState()
                val favoriteDestinations by viewModel.favoriteDestinations.collectAsState()
                val selectedDestination by viewModel.selectedDestination.collectAsState()

                val currentDestination = selectedDestination ?: destinations.firstOrNull() ?: Destination(
                    id = "dummy_1",
                    name = "Memuat...",
                    kabupaten = "Memuat...",
                    description = "Memuat...",
                    category = "Wisata",
                    imageUrl = "",
                    latitude = 0.0,
                    longitude = 0.0,
                    dutaPick = "",
                    facilities = "",
                    rating = 0.0,
                    reviewCount = 0
                )

                val isFavorite =
                    favoriteDestinations.any { it.id == currentDestination.id }

                DetailScreen(
                    destination = currentDestination,
                    reviews = emptyList(),

                    isFavorite = isFavorite,

                    onFavoriteClick = { viewModel.toggleFavorite(currentDestination) },
                    onBackClick = { navController.popBackStack() },
                    onRouteClick = {
                        Log.d("ROUTE", "Button Route ditekan")

                        viewModel.selectDestinationForMap(currentDestination)

                        Log.d("ROUTE", "Destinasi = ${currentDestination.name}")

                        navController.navigate(Screen.Map.route)
                    },
                    onSaveReview = { },
                    onDeleteReview = { }
                )
            }

            //  TAMBAHAN: Daftarkan layar ProfileScreen di sini
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },

                    isDarkMode = isDarkMode,

                    onDarkModeChange = {
                        themeViewModel.setDarkMode(it)
                    },

                    onEditProfileClick = {
                        navController.navigate(Screen.EditProfile.route)
                    },

                    onLanguageClick = {
                        // nanti
                    },

                    onLogoutClick = {
                        // nanti
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

                composable(Screen.AllDestinations.route) {
                    AllDestinationsScreen(
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() }, // Fungsi tombol back
                        onDestinationClick = { destination ->
                            // Nanti kalau di klik kartunya, pindah ke Detail
                            navController.navigate(Screen.Detail.route)
                        }
                    )
                }

        }

        // Navbar Melayang (Floating Bubble Navbar)
        if (showBottomBar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            ) {
                CustomBottomNavigation(navController = navController, currentRoute = currentRoute)
            }
        }
    }
}


@Composable
fun CustomBottomNavigation(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(30.dp)),
        containerColor = Color.White, // Warna background navbar diset putih sesuai Figma
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavItem("Home", Icons.Default.Home, Screen.Home.route),
            NavItem("Favorite", Icons.Default.FavoriteBorder, Screen.Favorite.route),
            NavItem("Map", Icons.Default.LocationOn, Screen.Map.route),
            NavItem("Partner", Icons.Default.Person, Screen.Partner.route)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = BanuaGreen,
                    indicatorColor = BanuaGreen,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}


data class NavItem(val title: String, val icon: ImageVector, val route: String)