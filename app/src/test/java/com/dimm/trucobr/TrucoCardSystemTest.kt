package com.dimm.trucobr

import com.dimm.trucobr.game.Card
import com.dimm.trucobr.game.CardRank
import com.dimm.trucobr.game.CardSuit
import com.dimm.trucobr.game.TrucoCardSystem
import com.dimm.trucobr.game.TrucoRules
import com.dimm.trucobr.game.TrucoRuleset
import com.dimm.trucobr.game.TrucoDealer
import com.dimm.trucobr.game.TrucoRoundResolver
import com.dimm.trucobr.game.PlayedCard
import com.dimm.trucobr.game.RoundResult
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrucoCardSystemTest {
    private val paulista = TrucoRules.paulista()
    private val mineiro = TrucoRules.mineiro()

    @Test
    fun viraDefinesNextRankAndWrapsAfterThree() {
        assertEquals(CardRank.FIVE, TrucoCardSystem.manilhaRank(Card(CardRank.FOUR, CardSuit.OUROS)))
        assertEquals(CardRank.FOUR, TrucoCardSystem.manilhaRank(Card(CardRank.THREE, CardSuit.OUROS)))
    }

    @Test
    fun manilhaBeatsHighestOrdinaryCard() {
        val vira = Card(CardRank.SIX, CardSuit.OUROS)
        val manilha = Card(CardRank.SEVEN, CardSuit.OUROS)
        val highestOrdinary = Card(CardRank.THREE, CardSuit.PAUS)

        assertTrue(TrucoCardSystem.compare(manilha, highestOrdinary, vira, paulista) > 0)
    }

    @Test
    fun manilhaSuitOrderIsAppliedOnlyBetweenManilhas() {
        val vira = Card(CardRank.SIX, CardSuit.OUROS)
        val zap = Card(CardRank.SEVEN, CardSuit.PAUS)
        val copas = Card(CardRank.SEVEN, CardSuit.COPAS)

        assertTrue(TrucoCardSystem.compare(zap, copas, vira, paulista) > 0)
        assertTrue(TrucoCardSystem.compare(zap, copas, vira, mineiro) > 0)
    }

    @Test
    fun ordinaryCardsOfSameRankTieRegardlessOfSuit() {
        val vira = Card(CardRank.SIX, CardSuit.OUROS)
        val first = Card(CardRank.THREE, CardSuit.PAUS)
        val second = Card(CardRank.THREE, CardSuit.OUROS)

        assertEquals(0, TrucoCardSystem.compare(first, second, vira, paulista))
    }

    @Test
    fun paulistaAndMineiroAreDistinctProfilesWithViraManilhas() {
        assertEquals(TrucoRuleset.PAULISTA, paulista.ruleset)
        assertEquals(TrucoRuleset.MINEIRO, mineiro.ruleset)
        val vira = Card(CardRank.JACK, CardSuit.OUROS)
        assertEquals(CardRank.KING, TrucoCardSystem.manilhaRank(vira))
    }

    @Test
    fun deckHasFortyUniqueCards() {
        val deck = TrucoCardSystem.fullDeck()
        assertEquals(40, deck.size)
        assertEquals(40, deck.toSet().size)
        assertFalse(deck.any { it.rank in setOf(CardRank.EIGHT, CardRank.NINE, CardRank.TEN) })
    }

    @Test
    fun dealGivesThreeCardsAndOneViraForTwoOrFourPlayers() {
        listOf(listOf("A", "B"), listOf("A", "B", "C", "D")).forEach { players ->
            val deal = TrucoDealer.deal(players, Random(1234))
            assertEquals(players.toSet(), deal.hands.keys)
            assertTrue(deal.hands.values.all { it.size == 3 })
            assertEquals(1, deal.vira.let { 1 })
            assertEquals(40, deal.hands.values.sumOf { it.size } + 1 + deal.remainingCards.size)
        }
    }

    @Test
    fun roundResolverReturnsWinnerAndDetectsTie() {
        val vira = Card(CardRank.FOUR, CardSuit.OUROS)
        val winner = TrucoRoundResolver.resolve(
            listOf(
                PlayedCard("A", Card(CardRank.FIVE, CardSuit.OUROS)),
                PlayedCard("B", Card(CardRank.THREE, CardSuit.PAUS))
            ),
            vira,
            paulista
        )
        assertTrue(winner is RoundResult.Winner)
        assertEquals("B", (winner as RoundResult.Winner).playerId)

        val tie = TrucoRoundResolver.resolve(
            listOf(
                PlayedCard("A", Card(CardRank.THREE, CardSuit.PAUS)),
                PlayedCard("B", Card(CardRank.THREE, CardSuit.OUROS))
            ),
            vira,
            paulista
        )
        assertTrue(tie is RoundResult.Tie)
    }
}
