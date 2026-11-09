package com.dimm.trucobr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HomeForest = Color(0xFF0B2119)
private val HomeFelt = Color(0xFF123A2B)
private val HomeGold = Color(0xFFD2B16D)
private val HomeCream = Color(0xFFF5F0E4)

@Composable
fun HomeScreen(onStartGame: () -> Unit, onSettings: () -> Unit = {}) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val motion = rememberInfiniteTransition(label = "hero-cards")
    val float by motion.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card-float"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = HomeForest) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppMark(Modifier.size(46.dp))
                Spacer(Modifier.size(12.dp))
                Column {
                    Text("TRUCO BR", color = HomeCream, fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text("O jogo começa na mesa", color = HomeGold.copy(alpha = .85f), fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }
            Spacer(Modifier.height(28.dp))
            AnimatedVisibility(visible = entered) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = HomeFelt)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Brush.linearGradient(listOf(Color(0xFF1C5A40), HomeFelt, Color(0xFF0D2D21))))
                            .padding(horizontal = 22.dp, vertical = 26.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("VALENDO!", color = HomeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("A mesa é sua.", color = HomeCream, fontSize = 30.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            Text("Chame a carta. Peça seis. Faça história.", color = HomeCream.copy(alpha = .75f), fontSize = 13.sp, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HeroCard("A", "♠", -12f, float)
                                Spacer(Modifier.size(8.dp))
                                HeroCard("7", "♦", 0f, -float * .7f)
                                Spacer(Modifier.size(8.dp))
                                HeroCard("A", "♥", 12f, float * .55f)
                            }
                            Spacer(Modifier.height(22.dp))
                            Button(
                                onClick = onStartGame,
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HomeGold, contentColor = HomeForest)
                            ) {
                                Text("JOGAR AGORA", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("SUA PARTIDA, SEU ESTILO", color = HomeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            FeatureTile("♣", "Truco paulista e mineiro", "Escolha a modalidade e jogue no seu ritmo.")
            Spacer(Modifier.height(10.dp))
            FeatureTile("✦", "Uma mesa feita para o truco", "Cartas, placar e mesa com identidade brasileira.")
            Spacer(Modifier.height(10.dp))
            FeatureTile("⌁", "Jogue sem internet", "Treine offline contra o adversário virtual.")
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onSettings,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = .08f), contentColor = HomeCream)
            ) { Text("⚙  CONFIGURAÇÕES", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(24.dp))
            Text("TRUCO BR  •  FEITO PARA QUEM GOSTA DO JOGO", color = HomeCream.copy(alpha = .42f), fontSize = 9.sp, letterSpacing = 1.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AppMark(modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(14.dp)).background(HomeGold), contentAlignment = Alignment.Center) {
        Text("♠", color = HomeForest, fontSize = 31.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun HeroCard(rank: String, suit: String, rotation: Float, lift: Float) {
    val red = suit == "♦" || suit == "♥"
    Card(
        modifier = Modifier.size(width = 76.dp, height = 112.dp).graphicsLayer {
            rotationZ = rotation
            translationY = lift
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HomeCream),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(rank, color = if (red) Color(0xFFB52D32) else HomeForest, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(suit, color = if (red) Color(0xFFB52D32) else HomeForest, fontSize = 30.sp)
        }
    }
}

@Composable
private fun FeatureTile(symbol: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = .055f)).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(HomeGold.copy(alpha = .14f)), contentAlignment = Alignment.Center) {
            Text(symbol, color = HomeGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.size(14.dp))
        Column {
            Text(title, color = HomeCream, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(description, color = HomeCream.copy(alpha = .62f), fontSize = 11.sp)
        }
    }
}
