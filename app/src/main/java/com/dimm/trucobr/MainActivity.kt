package com.dimm.trucobr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private val DeepGreen = Color(0xFF0B2119)
private val Gold = Color(0xFFD2B16D)
private val Ivory = Color(0xFFF5F0E4)
private val FeltGreen = Color(0xFF123A2B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrucoBrTheme {
                var showMatch by remember { mutableStateOf(false) }
                if (showMatch) {
                    LocalMatchScreen(onBack = { showMatch = false })
                } else {
                    HomeScreen(onStartGame = { showMatch = true })
                }
            }
        }
    }
}

@Composable
private fun TrucoBrTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Gold,
            onPrimary = DeepGreen,
            secondary = Ivory,
            background = DeepGreen,
            surface = FeltGreen,
            onSurface = Ivory
        ),
        content = content
    )
}
