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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val StatsGreen = Color(0xFF0B2119)
private val StatsGold = Color(0xFFD2B16D)
private val StatsCream = Color(0xFFF5F0E4)

@Composable
fun StatsScreen(stats: GameStats, onReset: () -> Unit, onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = StatsGreen) {
        Column(
            modifier = Modifier.fillMaxSize().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("SEU DESEMPENHO", color = StatsGold, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text("Histórico das partidas neste dispositivo", color = StatsCream.copy(alpha = .65f), fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("PARTIDAS", stats.played.toString(), Modifier.weight(1f))
                StatTile("VITÓRIAS", stats.wins.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("DERROTAS", stats.losses.toString(), Modifier.weight(1f))
                StatTile("APROVEITAMENTO", "${stats.winRate}%", Modifier.weight(1f))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF153A2B)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("RANKING LOCAL", color = StatsGold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        when {
                            stats.played == 0 -> "Jogue sua primeira partida para começar seu histórico."
                            stats.winRate >= 70 -> "Craque da mesa! Continue assim."
                            stats.winRate >= 50 -> "Boa sequência. Você está acima de metade das partidas."
                            else -> "A prática leva à vitória. Treine e tente novas estratégias."
                        },
                        color = StatsCream.copy(alpha = .82f),
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B3434), contentColor = StatsCream)
            ) { Text("ZERAR HISTÓRICO") }
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = StatsGold, contentColor = StatsGreen)
            ) { Text("VOLTAR", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFF153A2B)), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(vertical = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = StatsGold, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Text(label, color = StatsCream.copy(alpha = .62f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
