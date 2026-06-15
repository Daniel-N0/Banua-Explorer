package com.example.banuaexplorer.feature.destination.presentation.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.banuaexplorer.R
import com.example.banuaexplorer.feature.destination.domain.model.Destination
import com.example.banuaexplorer.feature.destination.domain.model.Review
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.AuthViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.DestinationViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.LanguageViewModel
import com.example.banuaexplorer.feature.destination.presentation.viewmodel.ThemeViewModel

@Composable
fun MainScreen(
    viewModel: DestinationViewModel,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel,
    languageViewModel: LanguageViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    val context = LocalContext.current
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val isEnglish by languageViewModel.isEnglish.collectAsState()

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Favorite.route,
        Screen.Map.route,
        Screen.Partner.route
    )

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = { fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.95f, animationSpec = tween(800)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) },
            popEnterTransition = { fadeIn(animationSpec = tween(700)) + scaleIn(initialScale = 0.95f, animationSpec = tween(800)) },
            popExitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            composable(Screen.Splash.route) {
                val currentUser by authViewModel.currentUser.collectAsState()
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(1500)
                    if (currentUser != null) navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                    else navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                }
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.banua_explorer), contentDescription = null, modifier = Modifier.size(300.dp))
                }
            }

            composable(Screen.Login.route) {
                val isLoading by authViewModel.isLoading.collectAsState()
                val errorMessage by authViewModel.errorMessage.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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
                    onForgotPasswordClick = { authViewModel.resetPassword(it) { _, msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() } }
                )
                if (isLoading) Dialog(onDismissRequest = {}) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            }

            composable(Screen.Register.route) {
                val isLoading by authViewModel.isLoading.collectAsState()
                val errorMessage by authViewModel.errorMessage.collectAsState()
                val currentUser by authViewModel.currentUser.collectAsState()

                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        authViewModel.resetState()
                    }
                }

                RegisterScreen(
                    onRegisterClick = { name, email, password -> authViewModel.register(name, email, password) },
                    onNavigateToLogin = { navController.popBackStack() }
                )
                if (isLoading) Dialog(onDismissRequest = {}) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onDestinationClick = { viewModel.selectDestination(it); navController.navigate(Screen.Detail.route) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onAmbassadorClick = { navController.navigate("duta_detail/${it.id}") },
                    onSeeAllAmbassadorClick = { navController.navigate(Screen.Partner.route) },
                    onSeeAllClick = { navController.navigate(Screen.AllDestinations.route) }
                )
            }

            composable(Screen.Favorite.route) {
                FavoriteScreen(viewModel = viewModel, onDestinationClick = { viewModel.selectDestination(it); navController.navigate(Screen.Detail.route) })
            }

            composable(Screen.Map.route) { MapScreen(viewModel = viewModel) }

            composable(Screen.Partner.route) {
                PartnerScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }, onNavigateToDutaDetail = { navController.navigate("duta_detail/$it") })
            }

            composable("duta_detail/{dutaId}") {
                DutaDetailScreen(ambassadorId = it.arguments?.getString("dutaId") ?: "", onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Detail.route) {
                val dest by viewModel.selectedDestination.collectAsState()
                val favs by viewModel.favoriteDestinations.collectAsState()

                // --- PERBAIKAN ERROR MERAH ---
                dest?.let { selectedDest ->
                    val reviews by viewModel.getReviews(selectedDest.id).collectAsState(emptyList())
                    DetailScreen(
                        destination = selectedDest,
                        reviews = reviews,
                        isFavorite = favs.any { f -> f.id == selectedDest.id },
                        onFavoriteClick = { viewModel.toggleFavorite(selectedDest) },
                        onBackClick = { navController.popBackStack() },
                        onRouteClick = {
                            viewModel.selectDestinationForMap(selectedDest)
                            navController.navigate(Screen.Map.route)
                        },
                        onSaveReview = { viewModel.addReview(it) },
                        onDeleteReview = { viewModel.deleteReview(it) }
                    )
                }
            }

            composable(Screen.Profile.route) {
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.navigateUp() },
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { themeViewModel.setDarkMode(it) },
                    isEnglish = isEnglish,
                    onLanguageChange = { languageViewModel.setLanguage(it) },
                    onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                    onLogoutClick = { authViewModel.logout(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } },
                    userName = currentUser?.displayName ?: "Petualang",
                    userEmail = currentUser?.email ?: "",
                    profilePictureUrl = currentUser?.photoUrl?.toString() ?: "",
                    // --- PERBAIKAN WARNING KUNING ---
                    onPhotoSelected = { authViewModel.uploadProfilePhoto(it) { s, _ -> if (s) Toast.makeText(context, "Sukses!", Toast.LENGTH_SHORT).show() } }
                )
            }

            composable(Screen.EditProfile.route) { EditProfileScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }) }

            composable(Screen.AllDestinations.route) {
                AllDestinationsScreen(viewModel = viewModel, onBackClick = { navController.popBackStack() }, onDestinationClick = { viewModel.selectDestination(it); navController.navigate(Screen.Detail.route) })
            }
        }

        AnimatedVisibility(
            visible = showBottomBar,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp)
        ) {
            CustomBottomNavigation(navController = navController, currentRoute = currentRoute)
        }
    }
}

@Composable
fun CustomBottomNavigation(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(30.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavItem(stringResource(R.string.nav_home), Icons.Default.Home, Screen.Home.route),
            NavItem(stringResource(R.string.nav_favorite), Icons.Default.FavoriteBorder, Screen.Favorite.route),
            NavItem(stringResource(R.string.nav_map), Icons.Default.LocationOn, Screen.Map.route),
            NavItem(stringResource(R.string.nav_partner), Icons.Default.Person, Screen.Partner.route)
        )
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { if (currentRoute != item.route) navController.navigate(item.route) { popUpTo(Screen.Home.route) { inclusive = false }; launchSingleTop = true } },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.onPrimary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary, unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

data class NavItem(val title: String, val icon: ImageVector, val route: String)