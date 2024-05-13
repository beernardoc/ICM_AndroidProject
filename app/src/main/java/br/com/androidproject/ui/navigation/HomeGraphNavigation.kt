package br.com.androidproject.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import br.com.androidproject.ui.screens.HomeScreen
import br.com.androidproject.ui.screens.Screens

const val homeGraphRoute = "homeGraph"

fun NavGraphBuilder.homeGraph(

) {
    navigation(
        startDestination = Screens.Home.screen,
        route = homeGraphRoute
    ) {
        composable(Screens.Home.screen) {
            HomeScreen()
        }
    }
}

fun NavHostController.navigateToHomeGraph(
    navOptions: NavOptions? = null
) {
    navigate(homeGraphRoute, navOptions)
}