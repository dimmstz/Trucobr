package com.dimm.trucobr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FeltGreen = Color(0xFF123A2B)
private val DeepGreen = Color(0xFF0B2119)
private val Gold = Color(0xFFD2B16D)
private val Ivory = Color(0xFFF5F0E4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrucoBrTheme {
                TrucoBrWelcomeScreen()
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

@Composable
private fun TrucoBrWelcomeScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = DeepGreen) {
        Box(
            modifier = Modifier.fillMaxSize().background(DeepGreen).padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "TRUCO",
                    color = Ivory,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 5.sp
                )
                Text(
                    text = "BR",
                    color = Gold,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 9.sp
                )
                Box(
                    modifier = Modifier
                        .background(FeltGreen, RoundedCornerShape(18.dp))
                        .padding(horizontal = 22.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Paulista • Mineiro",
                        color = Ivory,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "A base do jogo está pronta para começar.",
                    color = Ivory.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "FASE 1 · FUNDAÇÃO",
                    color = Gold,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
