package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.nav.BottomNavBar
import com.example.weatherapp.ui.nav.BottomNavItem
import com.example.weatherapp.ui.nav.MainNavHost
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        setContent {
            WeatherAppTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.HomeButton,
        BottomNavItem.ListButton,
        BottomNavItem.MapButton
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bem-vindo/a!") },
                actions = {
                    val context = LocalContext.current
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController, items)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO Parte 3 */ }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavHost(navController, Modifier, viewModel)
        }
    }
}