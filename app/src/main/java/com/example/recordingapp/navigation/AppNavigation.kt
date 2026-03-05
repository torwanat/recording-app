package com.example.recordingapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.recordingapp.audio.AudioScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Routes.AUDIO, modifier = modifier) {
        composable(Routes.AUDIO) { AudioScreen() }
    }
}