package com.example.banuaexplorer.feature.destination.presentation.ui

sealed class Screen(val route: String) {

    // Authentication
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")

    // Bottom Navigation
    object Home : Screen("home")
    object Favorite : Screen("favorite")
    object Map : Screen("map")
    object Partner : Screen("partner")

    // Full Screen
    object Detail : Screen("detail")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object AllDestinations : Screen("all_destinations")
}