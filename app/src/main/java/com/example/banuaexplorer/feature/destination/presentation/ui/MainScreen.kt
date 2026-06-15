package com.example.banuaexplorer.feature.destination.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.banuaexplorer.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Import Models & ViewModels
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModel

@Composable
fun MainScreen(
    viewModel: DestinationViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    // State untuk Dark Mode
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    // Cek kapan Bottom Navbar harus muncul
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Favorite.route,
        Screen.Map.route,
        Screen.Partner.route
    )

    // Box Utama pembungkus layar
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Adaptif Dark Mode
    ) {
        // Layar TV (NavHost)
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route, // Mulai dari Splash Screen Lu
            modifier = Modifier.fillMaxSize(),
            // Transisi Animasi Lu
            enterTransition = {
                fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.95f, animationSpec = tween(800))
            },
            exitTransition = { fadeOut(animationSpec = tween(700)) },
            popEnterTransition = {
                fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.95f, animationSpec = tween(800))
            },
            popExitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {

            // ==========================================
            // --- RUTE SPLASH SCREEN ---
            // ==========================================
            composable(Screen.Splash.route) {
                val currentUser by authViewModel.currentUser.collectAsState()

                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1500)
                    if (currentUser != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banua_explorer), // Pakai nama file yang baru
                        contentDescription = "Logo Banua Explorer",
                        modifier = Modifier.size(300.dp) // Ukuran logonya, bisa lu gede-kecilin
                    )
                }
            }

            // ==========================================
            // --- RUTE LOGIN & REGISTER ---
            // ==========================================
            composable(Screen.Login.route) {
                val isLoading by authViewModel.isLoading.collectAsState()
                val isLoginSuccess by authViewModel.isLoginSuccess.collectAsState()
                val errorMessage by authViewModel.errorMessage.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(isLoginSuccess) {
                    if (isLoginSuccess) {
                        Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        authViewModel.resetState()
                    }
                }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                }

                LoginScreen(
                    onLoginClick = { email, password -> authViewModel.login(email, password) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onForgotPasswordClick = { emailTxt ->
                        authViewModel.resetPassword(emailTxt) { _, message ->
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                )

                if (isLoading) {
                    Dialog(onDismissRequest = { }) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                }
            }

            composable(Screen.Register.route) {
                val isLoading by authViewModel.isLoading.collectAsState()
                val isLoginSuccess by authViewModel.isLoginSuccess.collectAsState()
                val errorMessage by authViewModel.errorMessage.collectAsState()

                LaunchedEffect(isLoginSuccess) {
                    if (isLoginSuccess) {
                        Toast.makeText(context, "Akun Berhasil Dibuat!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        authViewModel.resetState()
                    }
                }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                }

                RegisterScreen(
                    onRegisterClick = { name, email, password ->
                        authViewModel.register(name, email, password)
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )

                if (isLoading) {
                    Dialog(onDismissRequest = { }) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                }
            }

            // ==========================================
            // --- RUTE UTAMA (HOME, MAP, FAVORITE, PARTNER) ---
            // ==========================================
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onDestinationClick = { destination ->
                        viewModel.selectDestination(destination)
                        navController.navigate(Screen.Detail.route)
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onAmbassadorClick = {
                        ambassador -> navController.navigate("duta_detail/${ambassador.id}") {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },

                    onSeeAllAmbassadorClick = {
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
                        viewModel.selectDestination(destination)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }

            composable(Screen.Map.route) { MapScreen(viewModel = viewModel) }
            composable(Screen.Partner.route) {
                PartnerScreen(
                    viewModel = viewModel,

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onNavigateToDutaDetail = { id ->
                        navController.navigate("duta_detail/$id")
                    }
                )
            }

            // Tambahin ini di bawah composable rute-rute lu yang lain
            composable("duta_detail/{dutaId}") { backStackEntry ->
                // Tangkap ID yang dibawa dari rute
                val id = backStackEntry.arguments?.getString("dutaId") ?: "duta-001"

                DutaDetailScreen(
                    ambassadorId = id, // <--- INI DIA YANG DITAGIH SAMA ANDROID STUDIO!
                    onBackClick = { navController.popBackStack() }
                )
            }

            // ==========================================
            // --- RUTE DETAIL DESTINASI ---
            // ==========================================
            // ==========================================
            // --- RUTE DETAIL DESTINASI ---
            // ==========================================
            composable(Screen.Detail.route) {
                val destinations by viewModel.destinations.collectAsState()
                val favoriteDestinations by viewModel.favoriteDestinations.collectAsState()
                val selectedDestination by viewModel.selectedDestination.collectAsState()

                // 1. Ambil data destinasi yang sedang aktif
                val currentDestination = selectedDestination ?: destinations.firstOrNull() ?: Destination(
                    id = "dummy_1", name = "Memuat...", kabupaten = "Memuat...",
                    description = "Memuat...", category = "Wisata", imageUrl = "",
                    latitude = 0.0, longitude = 0.0, dutaPick = "", facilities = "",
                    rating = 0.0, reviewCount = 0, galleryUrls = emptyList()

                )

                // 2. Ambil data review dari database lokal (ROOM) via ViewModel
                val currentReviews by viewModel.getReviews(currentDestination.id).collectAsState(initial = emptyList<Review>())

                val isFavorite = favoriteDestinations.any { it.id == currentDestination.id }

                DetailScreen(
                    destination = currentDestination,
                    reviews = currentReviews, // <--- Data review disuntikkan ke sini
                    isFavorite = isFavorite,
                    onFavoriteClick = { viewModel.toggleFavorite(currentDestination) },
                    onBackClick = { navController.popBackStack() },
                    onRouteClick = {
                        viewModel.selectDestinationForMap(currentDestination)
                        navController.navigate(Screen.Map.route)
                    },
                    // 3. Eksekusi fungsi ADD / EDIT (BREAD)
                    onSaveReview = { review ->
                        viewModel.addReview(review)
                    },
                    // 4. Eksekusi fungsi DELETE (BREAD)
                    onDeleteReview = { review ->
                        viewModel.deleteReview(review)
                    }
                )
            }

            // ==========================================
            // --- RUTE PROFIL & EDIT PROFIL ---
            // ==========================================
            composable(Screen.Profile.route) {
                // Ambil Data User Firebase
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                val activeName = currentUser?.displayName ?: "Petualang Banua"
                val activeEmail = currentUser?.email ?: "email@kosong.com"
                val activePhotoUrl = currentUser?.photoUrl?.toString() ?: ""
                var uploadedPhotoUrl by remember { mutableStateOf(activePhotoUrl) }

                ProfileScreen(
                    viewModel = viewModel, // Dari Daniel
                    onBackClick = { navController.navigateUp() },
                    isDarkMode = isDarkMode, // Dari Daniel
                    onDarkModeChange = { themeViewModel.setDarkMode(it) }, // Dari Daniel
                    onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                    onLanguageClick = { /* Nanti */ },
                    onLogoutClick = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    // Data & Upload Cloudinary Lu
                    userName = activeName,
                    userEmail = activeEmail,
                    profilePictureUrl = uploadedPhotoUrl,
                    onPhotoSelected = { uri ->
                        Toast.makeText(context, "Mulai upload...", Toast.LENGTH_SHORT).show()
                        authViewModel.uploadProfilePhoto(uri) { isSuccess, resultUrl ->
                            if (isSuccess) {
                                Toast.makeText(context, "Upload Sukses!", Toast.LENGTH_SHORT).show()
                                uploadedPhotoUrl = resultUrl
                            } else {
                                Toast.makeText(context, "Gagal: $resultUrl", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.AllDestinations.route) {
                AllDestinationsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onDestinationClick = { destination ->
                        viewModel.selectDestination(destination)
                        navController.navigate(Screen.Detail.route)
                    }
                )
            }
        }

        // Navbar Melayang (Floating Bubble Navbar)
        AnimatedVisibility(
            visible = showBottomBar,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp)
        ) {
            CustomBottomNavigation(navController = navController, currentRoute = currentRoute)
        }
    }
}

@Composable
fun CustomBottomNavigation(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(30.dp)),
        containerColor = MaterialTheme.colorScheme.surface, // Adaptif Dark Mode
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
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}


data class NavItem(val title: String, val icon: ImageVector, val route: String)