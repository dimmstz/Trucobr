package com.dimm.trucobr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SettingsGreen = Color(0xFF0B2119)
private val SettingsGold = Color(0xFFD2B16D)
private val SettingsCream = Color(0xFFF5F0E4)

@Composable
fun SettingsScreen(
    preferences: GamePreferences,
    onPreferencesChanged: (GamePreferences) -> Unit,
    onBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = SettingsGreen) {
        Column(
            modifier = Modifier.fillMaxSize().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("CONFIGURAÇÕES", color = SettingsGold, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("Personalize sua mesa", color = SettingsCream.copy(alpha = .7f), fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            SettingSwitch("Efeitos sonoros", "Sons ao jogar e vencer vazas", preferences.soundEnabled) {
                onPreferencesChanged(preferences.copy(soundEnabled = it))
            }
            SettingSwitch("Vibração", "Resposta tátil ao jogar uma carta", preferences.vibrationEnabled) {
                onPreferencesChanged(preferences.copy(vibrationEnabled = it))
            }
            SettingSwitch("Animações", "Movimento e transições das cartas", preferences.animationsEnabled) {
                onPreferencesChanged(preferences.copy(animationsEnabled = it))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF153A2B))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("DIFICULDADE DO ADVERSÁRIO", color = SettingsGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Fácil", "Normal", "Difícil").forEach { level ->
                            Button(
                                onClick = { onPreferencesChanged(preferences.copy(botDifficulty = level)) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (preferences.botDifficulty == level) SettingsGold else Color(0xFF285440),
                                    contentColor = if (preferences.botDifficulty == level) SettingsGreen else SettingsCream
                                ),
                                modifier = Modifier.weight(1f)
                            ) { Text(level, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                    Text(
                        when (preferences.botDifficulty) {
                            "Fácil" -> "O adversário escolhe cartas aleatoriamente."
                            "Difícil" -> "O adversário tenta vencer gastando a menor carta possível."
                            else -> "O adversário equilibra risco e força das cartas."
                        },
                        color = SettingsCream.copy(alpha = .65f),
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SettingsGold, contentColor = SettingsGreen)
            ) { Text("SALVAR E VOLTAR", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = .055f), RoundedCornerShape(16.dp)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = SettingsCream, fontWeight = FontWeight.Bold)
            Text(subtitle, color = SettingsCream.copy(alpha = .58f), fontSize = 11.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
