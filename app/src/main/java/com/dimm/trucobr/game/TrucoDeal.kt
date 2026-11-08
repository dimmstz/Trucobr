package com.dimm.trucobr.game

import kotlin.random.Random

data class TrucoDeal(
    /** All dealt hands are private server-side state; never broadcast this object to every player. */
    val hands: Map<String, List<Card>>,
    val vira: Card,
    val remainingCards: List<Card>
)

object TrucoDealer {
    fun deal(
        playerIds: List<String>,
        random: Random = Random.Default
    ): TrucoDeal {
        require(playerIds.size == 2 || playerIds.size == 4) {
            "A partida deve ter 2 ou 4 jogadores."
        }
        require(playerIds.all { it.isNotBlank() }) {
            "Os identificadores dos jogadores não podem estar vazios."
        }
        require(playerIds.toSet().size == playerIds.size) {
            "Os identificadores dos jogadores devem ser únicos."
        }

        val deck = TrucoCardSystem.fullDeck().shuffled(random)
        val hands = playerIds.associateWith { mutableListOf<Card>() }.toMutableMap()

        var cardIndex = 0
        repeat(3) {
            playerIds.forEach { playerId ->
                hands.getValue(playerId).add(deck[cardIndex++])
            }
        }

        val vira = deck[cardIndex++]
        return TrucoDeal(
            hands = hands.mapValues { it.value.toList() },
            vira = vira,
            remainingCards = deck.drop(cardIndex)
        )
    }
}
