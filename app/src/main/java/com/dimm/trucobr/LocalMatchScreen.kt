package com.dimm.trucobr

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dimm.trucobr.game.Card as PlayingCard
import com.dimm.trucobr.game.CardSuit
import com.dimm.trucobr.game.HandResult
import com.dimm.trucobr.game.MatchScore
import com.dimm.trucobr.game.PlayedCard
import com.dimm.trucobr.game.TrickOutcome
import com.dimm.trucobr.game.TrucoDeal
import com.dimm.trucobr.game.TrucoDealer
import com.dimm.trucobr.game.TrucoMatchEngine
import com.dimm.trucobr.game.TrucoRules
import com.dimm.trucobr.game.TrucoRuleset
import com.dimm.trucobr.game.TrucoScoring
import kotlin.random.Random

private val TableGreen = Color(0xFF14523B)
private val DarkGreen = Color(0xFF0B2119)
private val GoldAccent = Color(0xFFD2B16D)
private val CardIvory = Color(0xFFFFFCF4)
private val Ink = Color(0xFF18231D)

private data class LocalGameState(
    val rules: TrucoRules,
    val hands: Map<String, List<PlayingCard>>,
    val vira: PlayingCard,
    val tricks: List<TrickOutcome> = emptyList(),
    val score: MatchScore = MatchScore(),
    val message: String = "Sua vez! Escolha uma carta.",
    val lastTrick: String = "",
    val finished: Boolean = false
)

private fun newLocalGame(rules: TrucoRules): LocalGameState {
    val deal: TrucoDeal = TrucoDealer.deal(listOf("human", "bot"))
    return LocalGameState(
        rules = rules,
        hands = deal.hands,
        vira = deal.vira
    )
}

private fun playLocalCard(state: LocalGameState, card: PlayingCard): LocalGameState {
    if (state.finished) return state
    val humanHand = state.hands["human"].orEmpty()
    if (card !in humanHand) return state

    val remainingHuman = humanHand - card
    val botHand = state.hands["bot"].orEmpty()
    if (botHand.isEmpty()) return state
    val botCard = botHand[Random.nextInt(botHand.size)]
    val remainingBot = botHand - botCard

    val outcome = TrucoMatchEngine.resolveTrick(
        plays = listOf(
            PlayedCard("human", card),
            PlayedCard("bot", botCard)
        ),
        teamByPlayer = mapOf("human" to "A", "bot" to "B"),
        vira = state.vira,
        rules = state.rules
    )
    val updatedTricks = state.tricks + outcome
    val completed = updatedTricks.size == 3 ||
        (updatedTricks.size >= 2 && (
            (updatedTricks[0].winningTeamId != null &&
                updatedTricks[0].winningTeamId == updatedTricks[1].winningTeamId) ||
            (updatedTricks[0].winningTeamId != null &&
                updatedTricks[1].winningTeamId == null) ||
            (updatedTricks[0].winningTeamId == null &&
                updatedTricks[1].winningTeamId != null)
        ))

    val trickLabel = when (outcome.winningTeamId) {
        "A" -> "Você venceu a vaza"
        "B" -> "O adversário venceu a vaza"
        else -> "Vaza empatada"
    }
    val hands = mapOf("human" to remainingHuman, "bot" to remainingBot)

    if (!completed) {
        return state.copy(
            hands = hands,
            tricks = updatedTricks,
            message = "$trickLabel. Jogue a próxima carta.",
            lastTrick = "$card  ×  $botCard"
        )
    }

    val handResult = TrucoMatchEngine.resolveHand(updatedTricks)
    val awardedValue = if (TrucoScoring.isSpecialHand(state.score, state.rules)) {
        TrucoScoring.specialHandValue(state.rules)
    } else {
        1
    }
    val nextScore = TrucoScoring.awardHand(
        score = state.score,
        result = handResult,
        handValue = awardedValue,
        rules = state.rules
    )
    val winner = when (handResult) {
        is HandResult.Winner -> if (handResult.teamId == "A") "Você ganhou a mão!" else "O bot ganhou a mão."
        HandResult.Tie -> "Mão empatada. Ninguém pontuou."
    }
    val matchWinner = nextScore.winner(state.rules.targetScore)
    if (matchWinner != null) {
        return state.copy(
            hands = hands,
            tricks = updatedTricks,
            score = nextScore,
            message = if (matchWinner == "A") "PARTIDA VENCIDA! Você ganhou." else "Partida encerrada. O bot venceu.",
            lastTrick = "$card  ×  $botCard",
            finished = true
        )
    }

    val nextDeal = TrucoDealer.deal(listOf("human", "bot"))
    return state.copy(
        hands = nextDeal.hands,
        vira = nextDeal.vira,
        tricks = emptyList(),
        score = nextScore,
        message = "$winner Próxima mão: sua vez.",
        lastTrick = "$card  ×  $botCard"
    )
}

@Composable
fun LocalMatchScreen() {
    var selectedRuleset by remember { mutableStateOf(TrucoRuleset.PAULISTA) }
    var game by remember { mutableStateOf<LocalGameState?>(null) }
    val rules = if (selectedRuleset == TrucoRuleset.PAULISTA) {
        TrucoRules.paulista()
    } else {
        TrucoRules.mineiro()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkGreen) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TRUCO BR",
                color = GoldAccent,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            Text("MESA DE TREINO · OFFLINE", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(18.dp))

            if (game == null) {
                Text("Escolha a modalidade", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RulesButton("PAULISTA", selectedRuleset == TrucoRuleset.PAULISTA) {
                        selectedRuleset = TrucoRuleset.PAULISTA
                    }
                    RulesButton("MINEIRO", selectedRuleset == TrucoRuleset.MINEIRO) {
                        selectedRuleset = TrucoRuleset.MINEIRO
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    if (selectedRuleset == TrucoRuleset.PAULISTA) "12 pontos · mão de onze"
                    else "12 pontos · mão de dez (perfil configurável)",
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { game = newLocalGame(rules) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = DarkGreen)
                ) { Text("COMEÇAR PARTIDA", fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(26.dp))
                Text("Mesa de demonstração contra bot aleatório", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
            } else {
                val current = game!!
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(if (current.rules.ruleset == TrucoRuleset.PAULISTA) "PAULISTA" else "MINEIRO", color = GoldAccent, fontWeight = FontWeight.Bold)
                        Text("MELHOR DE 3 VAZAS", color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp)
                    }
                    OutlinedButton(onClick = { game = null }) { Text("SAIR", color = Color.White) }
                }
                Spacer(Modifier.height(12.dp))
                ScoreBoard(current.score.teamA, current.score.teamB)
                Spacer(Modifier.height(16.dp))
                Text("ADVERSÁRIO · BOT", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, letterSpacing = 1.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        PlayingCardView(
                            card = current.hands["bot"]?.getOrNull(index),
                            hidden = current.hands["bot"]?.getOrNull(index) != null
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TableGreen),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("VIRA", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        PlayingCardView(card = current.vira, hidden = false)
                        Spacer(Modifier.height(12.dp))
                        Text(current.message, color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        if (current.lastTrick.isNotBlank()) {
                            Text("Última vaza: ${current.lastTrick}", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                        Text("Vazas: ${current.tricks.size}/3", color = GoldAccent, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("SUAS CARTAS", color = GoldAccent, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    current.hands["human"].orEmpty().forEach { card ->
                        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                            Button(
                                onClick = { game = playLocalCard(current, card) },
                                enabled = !current.finished,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CardIvory,
                                    contentColor = Ink,
                                    disabledContainerColor = CardIvory.copy(alpha = 0.55f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                modifier = Modifier.width(88.dp).height(124.dp)
                            ) {
                                CardFace(card)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (current.finished) {
                    Button(
                        onClick = { game = newLocalGame(current.rules) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = DarkGreen)
                    ) { Text("NOVA PARTIDA") }
                }
                Text("Bot de demonstração: jogadas aleatórias, sem conexão online.", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RulesButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = DarkGreen)
    else ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
    if (selected) {
        Button(onClick = onClick, colors = colors) { Text(label, fontWeight = FontWeight.Bold) }
    } else {
        OutlinedButton(onClick = onClick, colors = colors) { Text(label) }
    }
}

@Composable
private fun ScoreBoard(player: Int, bot: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18392C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreValue("VOCÊ", player)
            Text("×", color = GoldAccent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            ScoreValue("BOT", bot)
        }
    }
}

@Composable
private fun ScoreValue(label: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp, letterSpacing = 1.sp)
        Text(score.toString(), color = GoldAccent, fontSize = 30.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun PlayingCardView(card: PlayingCard?, hidden: Boolean) {
    Card(
        modifier = Modifier.width(62.dp).height(88.dp).padding(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (hidden) Color(0xFF244D3D) else CardIvory)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (hidden) {
                Text("TRUCO", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.Black)
            } else if (card != null) {
                CardFace(card)
            }
        }
    }
}

@Composable
private fun CardFace(card: PlayingCard) {
    val red = card.suit == CardSuit.OUROS || card.suit == CardSuit.COPAS
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            card.rank.label,
            color = if (red) Color(0xFFB52D32) else Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            card.suit.symbol,
            color = if (red) Color(0xFFB52D32) else Ink,
            fontSize = 24.sp
        )
    }
}
