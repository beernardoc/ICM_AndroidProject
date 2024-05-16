package br.com.androidproject.ui.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.androidproject.ui.viewmodels.MapViewModel

class HomeScreenViewModel : ViewModel() {
    var selectedPage by mutableStateOf(Screens.Home.screen)
}

@Composable
fun HomeScreen() {
    val navigatonController = rememberNavController()
    val viewModel: HomeScreenViewModel = viewModel()


    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Gray,
            ) {
                IconButton(
                    onClick = {
                        viewModel.selectedPage = Screens.Home.screen
                        navigatonController.navigate(Screens.Home.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedPage == Screens.Home.screen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )


                }

                IconButton(
                    onClick = {
                        viewModel.selectedPage = Screens.History.screen
                        navigatonController.navigate(Screens.History.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "History",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedPage == Screens.History.screen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedPage = Screens.Weather.screen
                        navigatonController.navigate(Screens.Weather.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Weather",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedPage == Screens.Weather.screen)  MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedPage = Screens.Profile.screen
                        navigatonController.navigate(Screens.Profile.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedPage == Screens.Profile.screen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                }

                IconButton(
                    onClick = {
                        viewModel.selectedPage = Screens.Settings.screen
                        navigatonController.navigate(Screens.Settings.screen) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.weight(1f)) {

                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp),
                        tint = if (viewModel.selectedPage == Screens.Settings.screen)  MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                val mapViewModel: MapViewModel = viewModel()
                MapScreen(
                    state = mapViewModel.state.value,
                    setupClusterManager = mapViewModel::setupClusterManager,
                    calculateZoneViewCenter = mapViewModel::calculateZoneLatLngBounds
                )
            }
            composable(Screens.History.screen) {
                HistoryScreen()
            }
            composable(Screens.Weather.screen) {
                WeatherScreen()
            }
            composable(Screens.Profile.screen) {
                ProfileScreen()
            }
            composable(Screens.Settings.screen) {
                SettingsScreen()
            }

        }
    }
}

