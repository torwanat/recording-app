package com.example.recordingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.recordingapp.navigation.AppNavigation
import com.example.recordingapp.ui.theme.RecordingAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppScaffold()
        }
    }
}

@Composable
fun AppScaffold() {
    val navController = rememberNavController()

    RecordingAppTheme {
        Scaffold { padding ->
            AppNavigation(navController = navController, modifier = Modifier.padding(padding))
        }
    }
}