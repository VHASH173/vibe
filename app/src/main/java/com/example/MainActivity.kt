package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.ui.navigation.VibeStreamNav
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.VibeStreamViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: VibeStreamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (com.google.firebase.FirebaseApp.getApps(applicationContext).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setProjectId("tiktok-live-app-c6854")
                    .setApplicationId("com.example")
                    .setApiKey("AIzaSyMockKeyForInitializationOnly")
                    .setStorageBucket("tiktok-live-app-c6854.firebasestorage.app")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(applicationContext, options)
            }
        } catch (_: Exception) {
        }
        enableEdgeToEdge()
        com.example.ui.components.GiftEffectManager.prewarmAllAssets(
            applicationContext,
            lifecycleScope
        )
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            MyApplicationTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VibeStreamNav(viewModel = viewModel)
                }
            }
        }
    }
}
