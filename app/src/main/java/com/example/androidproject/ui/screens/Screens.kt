package com.example.androidproject.ui.screens

sealed class Screens(val screen: String) {
    data object Home : Screens("Home")
    data object History : Screens("History")
    data object Weather : Screens("Weather")
    data object Profile : Screens("Profile")
    data object Settings : Screens("Settings")
}