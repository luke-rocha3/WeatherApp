package com.example.weatherapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.local.LocalDatabase
import com.example.weatherapp.model.User
import com.example.weatherapp.repo.Repository
import com.example.weatherapp.monitor.ForecastMonitor
import com.example.weatherapp.ui.nav.BottomNavBar
import com.example.weatherapp.ui.nav.BottomNavItem
import com.example.weatherapp.ui.nav.MainNavHost
import com.example.weatherapp.ui.nav.Route
import com.example.weatherapp.ui.screens.CityDialog
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val fbDB = remember { FBDatabase() }
            val localDB = remember {
                val uid = Firebase.auth.currentUser?.uid ?: "anonymous"
                LocalDatabase(this, uid)
            }
            val repository = remember { Repository(fbDB, localDB) }
            val weatherService = remember { WeatherService(this) }
            val forecastMonitor = remember { ForecastMonitor(this) }
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository, weatherService, forecastMonitor)
            )

            val user = viewModel.user.collectAsStateWithLifecycle(null).value

            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    viewModel.city = intent.getStringExtra("city")
                    viewModel.page = Route.Home
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }

            WeatherAppTheme {
                MainScreen(viewModel = viewModel, user = user)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, user: User?) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.HomeButton,
        BottomNavItem.ListButton,
        BottomNavItem.MapButton
    )

    val showButton = viewModel.page == Route.List

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) CityDialog(
        onDismiss = { showDialog = false },
        onConfirm = { city ->
            if (city.isNotBlank()) viewModel.addCity(city)
            showDialog = false
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val name = user?.name ?: "[carregando...]"
                    Text("Bem-vindo/a! $name")
                },
                actions = {
                    IconButton(onClick = {
                        Firebase.auth.signOut()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(viewModel, navController, items)
        },
        floatingActionButton = {
            if (showButton) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar")
                }
            }
        }
    ) { innerPadding ->
        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavHost(navController, Modifier, viewModel)
        }
        LaunchedEffect(viewModel.page) {
            navController.navigate(viewModel.page) {
                navController.graph.startDestinationRoute?.let {
                    popUpTo(it) {
                        saveState = true
                    }
                }
                restoreState = true
                launchSingleTop = true
            }
        }
    }
}