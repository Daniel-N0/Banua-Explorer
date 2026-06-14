package com.example.banuaexplorer.feature.destination.presentation.ui

sealed class Screen(val route: String) {
    // Rute Bottom Navigation
    object Home : Screen("home")
    object Favorite : Screen("favorite")
    object Map : Screen("map")
    object Partner : Screen("partner")

    // Rute Full Screen
    object Detail : Screen("detail")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")

    // 👇 TAMBAHKAN BARIS INI BRO 👇
    object AllDestinations : Screen("all_destinations")
}