package com.vazdautsan.drwebtesttask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vazdautsan.drwebtesttask.presentation.navigation.AppNavHost
import com.vazdautsan.drwebtesttask.ui.theme.DrWebTestTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrWebTestTaskTheme {
                AppNavHost()
            }
        }
    }
}
