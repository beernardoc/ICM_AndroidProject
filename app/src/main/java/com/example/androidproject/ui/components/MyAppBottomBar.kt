package com.example.androidproject.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.androidproject.ui.screens.Screens
import com.example.androidproject.ui.screens.History
import com.example.androidproject.ui.screens.Home
import com.example.androidproject.ui.screens.Profile
import com.example.androidproject.ui.screens.Settings
import com.example.androidproject.ui.screens.Weather


class BottomAppBarViewModel : ViewModel() {
    var selectedIcon by mutableStateOf(Icons.Default.Home)
}


@Composable
fun MyBottomAppBar() {
    val navigatonController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val viewModel: BottomAppBarViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Gray,
            ) {
                IconButton(
                    onClick = {
                        viewModel.selectedIcon = Icons.Default.Home
                        navigatonController.navigate(Screens.Home.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedIcon == Icons.Default.Home) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )


                }

                IconButton(
                    onClick = {
                        viewModel.selectedIcon = Icons.Default.List
                        navigatonController.navigate(Screens.History.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.List,
                        contentDescription = "History",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedIcon == Icons.Default.List) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedIcon = Icons.Default.LocationOn
                        navigatonController.navigate(Screens.Weather.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Weather",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedIcon == Icons.Default.LocationOn) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedIcon = Icons.Default.AccountCircle
                        navigatonController.navigate(Screens.Profile.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedIcon == Icons.Default.AccountCircle) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedIcon = Icons.Default.Settings
                        navigatonController.navigate(Screens.Settings.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedIcon == Icons.Default.Settings) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )

                }


            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navigatonController,
            startDestination = Screens.Home.screen,
            modifier = Modifier.padding(paddingValues)
        )  {
            composable(Screens.Home.screen) {
                Home()
            }
            composable(Screens.History.screen) {
                History()
            }
            composable(Screens.Weather.screen) {
                Weather()
            }
            composable(Screens.Profile.screen) {
                Profile()
            }
            composable(Screens.Settings.screen) {
                Settings()
            }

        }
    }
}