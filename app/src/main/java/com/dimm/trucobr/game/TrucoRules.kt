package com.dimm.trucobr.game

/**
 * Regional profile. The Mineiro defaults below are a configurable house baseline;
 * this project intentionally keeps vira-based manilhas in both profiles.
 */
enum class TrucoRuleset {
    PAULISTA,
    MINEIRO
}

enum class CardSuit(val symbol: String) {
    OUROS("♦"),
    ESPADAS("♠"),
    COPAS("♥"),
    PAUS("♣")
}

/** Strength order used by the vira-based card system, from weakest to strongest. */
enum class CardRank(val label: String) {
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    QUEEN("Q"),
    JACK("J"),
    KING("K"),
    ACE("A"),
    TWO("2"),
    THREE("3")
}

data class Card(val rank: CardRank, val suit: CardSuit) {
    override fun toString(): String = "${rank.label}${suit.symbol}"
}

data class TrucoRules(
    val ruleset: TrucoRuleset,
    /** Suit order from weakest to strongest when both cards are manilhas. */
    val manilhaSuitOrder: List<CardSuit> = listOf(
        CardSuit.OUROS,
        CardSuit.ESPADAS,
        CardSuit.COPAS,
        CardSuit.PAUS
    ),
    val scoreLadder: List<Int> = listOf(1, 3, 6, 9, 12),
    val targetScore: Int = 12,
    val specialScore: Int = 11,
    val specialHandValue: Int = 3,
    val specialForfeitValue: Int = 1
) {
    init {
        require(manilhaSuitOrder.size == CardSuit.entries.size &&
            manilhaSuitOrder.toSet().size == CardSuit.entries.size) {
            "A ordem das manilhas deve conter os quatro naipes, sem repetição."
        }
        require(scoreLadder.isNotEmpty() && scoreLadder.first() > 0)
        require(scoreLadder.zipWithNext().all { (a, b) -> b > a })
        require(scoreLadder.last() == targetScore)
        require(specialScore in 1 until targetScore)
        require(specialHandValue > 0 && specialForfeitValue > 0)
    }

    companion object {
        fun paulista(): TrucoRules = TrucoRules(TrucoRuleset.PAULISTA)

        /**
         * Mineiro scoring baseline (2-4-6-10-12 and mão de dez).
         * Vira-based manilhas are retained as explicitly required by this app.
         */
        fun mineiro(): TrucoRules = TrucoRules(
            ruleset = TrucoRuleset.MINEIRO,
            scoreLadder = listOf(2, 4, 6, 10, 12),
            specialScore = 10,
            specialHandValue = 4,
            specialForfeitValue = 2
        )
    }
}

object TrucoCardSystem {
    val strengthOrder: List<CardRank> = CardRank.entries

    fun nextRankAfter(rank: CardRank): CardRank {
        val index = strengthOrder.indexOf(rank)
        return strengthOrder[(index + 1) % strengthOrder.size]
    }

    fun manilhaRank(vira: Card): CardRank = nextRankAfter(vira.rank)

    fun isManilha(card: Card, vira: Card): Boolean = card.rank == manilhaRank(vira)

    fun compare(first: Card, second: Card, vira: Card, rules: TrucoRules): Int {
        val firstIsManilha = isManilha(first, vira)
        val secondIsManilha = isManilha(second, vira)

        if (firstIsManilha && !secondIsManilha) return 1
        if (!firstIsManilha && secondIsManilha) return -1

        if (firstIsManilha) {
            return rules.manilhaSuitOrder.indexOf(first.suit)
                .compareTo(rules.manilhaSuitOrder.indexOf(second.suit))
        }

        return strengthOrder.indexOf(first.rank).compareTo(strengthOrder.indexOf(second.rank))
    }

    fun fullDeck(): List<Card> = CardSuit.entries.flatMap { suit ->
        CardRank.entries.map { rank -> Card(rank, suit) }
    }
}
