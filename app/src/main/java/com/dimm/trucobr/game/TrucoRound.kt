package com.dimm.trucobr.game

data class PlayedCard(val playerId: String, val card: Card)

sealed interface RoundResult {
    data class Winner(val playerId: String, val card: Card) : RoundResult
    data class Tie(val cards: List<PlayedCard>) : RoundResult
}

object TrucoRoundResolver {
    /**
     * Resolves one trick only. Full hand tie-breaks and score progression are
     * intentionally kept out until each regional table profile is fully specified.
     */
    fun resolve(
        plays: List<PlayedCard>,
        vira: Card,
        rules: TrucoRules
    ): RoundResult {
        require(plays.size >= 2) { "Uma rodada precisa de pelo menos duas cartas." }
        require(plays.all { it.playerId.isNotBlank() }) {
            "Toda carta jogada precisa ter um jogador identificado."
        }
        require(plays.map { it.playerId }.toSet().size == plays.size) {
            "Cada jogador pode jogar apenas uma carta por rodada."
        }

        var best = plays.first()
        var tied = mutableListOf(best)

        for (play in plays.drop(1)) {
            when (TrucoCardSystem.compare(play.card, best.card, vira, rules)) {
                in 1..Int.MAX_VALUE -> {
                    best = play
                    tied = mutableListOf(play)
                }
                0 -> tied.add(play)
            }
        }

        return if (tied.size > 1) RoundResult.Tie(tied) else RoundResult.Winner(best.playerId, best.card)
    }
}
