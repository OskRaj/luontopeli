package com.example.luontopeli

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.luontopeli.data.remote.firebase.AuthManager
import com.example.luontopeli.data.repository.NatureSpotRepository
import com.example.luontopeli.ui.navigation.LuontopeliBottomBar
import com.example.luontopeli.ui.navigation.LuontopeliNavHost
import com.example.luontopeli.ui.theme.LuontopeliTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var repository: NatureSpotRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Kirjaudu anonyymisti ja synkronoi odottavat löydöt
        lifecycleScope.launch {
            if (!authManager.isSignedIn) {
                authManager.signInAnonymously()
            }
            // Yritetään synkronoida offline-tilassa otetut löydöt pilveen
            repository.syncPendingSpots()
        }

        setContent {
            LuontopeliTheme {
                LuontopeliApp()
            }
        }
    }
}

@Composable
fun LuontopeliApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            LuontopeliBottomBar(navController = navController)
        }
    ) { innerPadding ->
        LuontopeliNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
