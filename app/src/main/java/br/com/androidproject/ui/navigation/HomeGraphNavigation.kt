package br.com.androidproject.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import br.com.androidproject.ui.screens.HomeScreen
import br.com.androidproject.ui.screens.Screens
import br.com.androidproject.ui.viewmodels.HistoryViewModel
import br.com.androidproject.ui.viewmodels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient

const val homeGraphRoute = "homeGraph"

fun NavGraphBuilder.homeGraph(
    mapViewModel: MapViewModel,
    historyViewModel: HistoryViewModel
) {
    navigation(
        startDestination = Screens.Home.screen,
        route = homeGraphRoute
    ) {
        composable(Screens.Home.screen) {
            HomeScreen(
                mapViewModel = mapViewModel,
                historyViewModel = historyViewModel
            )
        }
    }
}

fun NavHostController.navigateToHomeGraph(
    navOptions: NavOptions? = null
) {
    navigate(homeGraphRoute, navOptions)
}