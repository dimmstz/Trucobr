package com.dimm.trucobr.game

/** One completed trick. A null winner represents a tie. */
data class TrickOutcome(val winningTeamId: String?)

sealed interface HandResult {
    data class Winner(val teamId: String, val tricksWon: Int) : HandResult
    data object Tie : HandResult
}

object TrucoMatchEngine {
    /**
     * Resolves one trick for either 1v1 or 2v2. Team membership is supplied by
     * the trusted game session; a tie is only declared when opposing teams'
     * best cards have equal strength.
     */
    fun resolveTrick(
        plays: List<PlayedCard>,
        teamByPlayer: Map<String, String>,
        vira: Card,
        rules: TrucoRules
    ): TrickOutcome {
        require(plays.size == teamByPlayer.size) {
            "Todos os jogadores da mesa devem jogar uma carta."
        }
        require(plays.map { it.playerId }.toSet().size == plays.size)
        require(plays.all { it.playerId in teamByPlayer })
        val teams = plays.map { teamByPlayer.getValue(it.playerId) }.toSet()
        require(teams.size == 2) { "A rodada precisa ter exatamente duas equipes." }

        val bestByTeam = plays.groupBy { teamByPlayer.getValue(it.playerId) }
            .mapValues { (_, teamPlays) ->
                teamPlays.maxWithOrNull { a, b ->
                    TrucoCardSystem.compare(a.card, b.card, vira, rules)
                }!!.card
            }
        val entries = bestByTeam.entries.toList()
        val comparison = TrucoCardSystem.compare(
            entries[0].value, entries[1].value, vira, rules
        )
        return when {
            comparison > 0 -> TrickOutcome(entries[0].key)
            comparison < 0 -> TrickOutcome(entries[1].key)
            else -> TrickOutcome(null)
        }
    }

    /** Resolves up to three completed tricks using the common best-of-three tie rules. */
    fun resolveHand(tricks: List<TrickOutcome>): HandResult {
        require(tricks.size in 1..3) { "A mão deve conter de uma a três vazas." }
        val first = tricks.getOrNull(0)?.winningTeamId
        val second = tricks.getOrNull(1)?.winningTeamId
        val third = tricks.getOrNull(2)?.winningTeamId

        if (first != null && first == second) {
            return HandResult.Winner(first, 2)
        }
        if (first != null && second == null) {
            return HandResult.Winner(first, 1)
        }
        if (first == null && second != null) {
            return HandResult.Winner(second, 1)
        }
        if (first != null && second != null && first != second) {
            return when {
                third == first -> HandResult.Winner(first, 2)
                third == second -> HandResult.Winner(second, 2)
                third == null -> HandResult.Winner(first, 1)
                else -> error("Resultado de terceira vaza inválido.")
            }
        }
        if (first == null && second == null && third != null) {
            return HandResult.Winner(third, 1)
        }
        if (tricks.size < 3) {
            error("A mão ainda não terminou.")
        }
        return HandResult.Tie
    }
}

data class MatchScore(val teamA: Int = 0, val teamB: Int = 0) {
    init {
        require(teamA >= 0 && teamB >= 0)
    }

    fun pointsFor(teamId: String): Int = when (teamId) {
        "A" -> teamA
        "B" -> teamB
        else -> throw IllegalArgumentException("Equipe desconhecida: $teamId")
    }

    fun add(teamId: String, points: Int): MatchScore {
        require(points >= 0)
        return when (teamId) {
            "A" -> copy(teamA = (teamA + points))
            "B" -> copy(teamB = (teamB + points))
            else -> throw IllegalArgumentException("Equipe desconhecida: $teamId")
        }
    }

    fun winner(targetScore: Int): String? = when {
        teamA >= targetScore && teamA > teamB -> "A"
        teamB >= targetScore && teamB > teamA -> "B"
        teamA >= targetScore && teamB >= targetScore && teamA == teamB -> null
        else -> null
    }
}

enum class SpecialHandDecision { PLAY, FOLD }

object TrucoScoring {
    fun isSpecialHand(score: MatchScore, rules: TrucoRules): Boolean =
        (score.teamA == rules.specialScore && score.teamB < rules.specialScore) ||
            (score.teamB == rules.specialScore && score.teamA < rules.specialScore)

    fun isIronHand(score: MatchScore, rules: TrucoRules): Boolean =
        score.teamA == rules.specialScore && score.teamB == rules.specialScore

    fun specialHandValue(rules: TrucoRules): Int = rules.specialHandValue

    fun applySpecialDecision(
        score: MatchScore,
        teamAtSpecialScore: String,
        decision: SpecialHandDecision,
        rules: TrucoRules
    ): MatchScore {
        require(teamAtSpecialScore == "A" || teamAtSpecialScore == "B")
        require(score.pointsFor(teamAtSpecialScore) == rules.specialScore)
        require(!isIronHand(score, rules))
        return when (decision) {
            SpecialHandDecision.PLAY -> score
            SpecialHandDecision.FOLD -> score.add(
                if (teamAtSpecialScore == "A") "B" else "A",
                rules.specialForfeitValue
            )
        }
    }

    fun awardHand(
        score: MatchScore,
        result: HandResult,
        handValue: Int,
        rules: TrucoRules
    ): MatchScore {
        require(handValue > 0)
        if (isIronHand(score, rules)) {
            val winner = (result as? HandResult.Winner)?.teamId
                ?: return score
            return if (winner == "A") score.copy(teamA = rules.targetScore)
            else score.copy(teamB = rules.targetScore)
        }
        val winner = (result as? HandResult.Winner)?.teamId ?: return score
        return score.add(winner, handValue)
    }
}

/** State machine for raise/accept/fold; only the opposing team may re-raise. */
data class TrucoBetState(
    val acceptedValue: Int,
    val pendingValue: Int? = null,
    val lastRaisedByTeamId: String? = null
) {
    companion object {
        fun initial(rules: TrucoRules) = TrucoBetState(rules.scoreLadder.first())
    }

    fun raise(teamId: String, rules: TrucoRules): TrucoBetState {
        require(teamId == "A" || teamId == "B")
        require(lastRaisedByTeamId != teamId) {
            "A equipe que aumentou por último deve aguardar a resposta adversária."
        }
        val currentOffer = pendingValue ?: acceptedValue
        val next = rules.scoreLadder.firstOrNull { it > currentOffer }
            ?: throw IllegalStateException("A aposta já está no limite.")
        return copy(pendingValue = next, lastRaisedByTeamId = teamId)
    }

    fun accept(): TrucoBetState {
        val pending = pendingValue ?: throw IllegalStateException("Não há pedido pendente.")
        return copy(acceptedValue = pending, pendingValue = null)
    }

    fun fold(): Int {
        check(pendingValue != null) { "Não há pedido pendente." }
        return acceptedValue
    }
}
