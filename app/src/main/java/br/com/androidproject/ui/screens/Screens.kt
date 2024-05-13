package br.com.androidproject.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val screen: String, val icon: ImageVector? = null) {
    data object Home : Screens("Home", Icons.Default.Home)
    data object History : Screens("History", Icons.Default.History)
    data object Weather : Screens("Weather", Icons.Default.SolarPower)
    data object Profile : Screens("Profile", Icons.Default.AccountCircle)
    data object Settings : Screens("Settings", Icons.Default.Settings)
}