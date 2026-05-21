package com.example.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.screens.HomePage
import com.example.weatherapp.ui.screens.ListPage
import com.example.weatherapp.ui.screens.MapPage
import com.example.weatherapp.MainViewModel
@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(modifier = modifier, viewModel = viewModel) }
        composable<Route.List> { ListPage(modifier = modifier, viewModel = viewModel) }
        composable<Route.Map> { MapPage(modifier = modifier, viewModel = viewModel) }
    }
}