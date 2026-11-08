package com.dimm.trucobr

import com.dimm.trucobr.game.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TrucoMatchEngineTest {
    private val rules = TrucoRules.paulista()
    private val vira = Card(CardRank.FOUR, CardSuit.OUROS)

    @Test fun resolvesTwoPlayerTrick() {
        val result = TrucoMatchEngine.resolveTrick(
            listOf(
                PlayedCard("p1", Card(CardRank.THREE, CardSuit.OUROS)),
                PlayedCard("p2", Card(CardRank.TWO, CardSuit.OUROS))
            ),
            mapOf("p1" to "A", "p2" to "B"), vira, rules
        )
        assertEquals("A", result.winningTeamId)
    }

    @Test fun resolvesTwoAgainstTwoByBestCardOfEachTeam() {
        val result = TrucoMatchEngine.resolveTrick(
            listOf(
                PlayedCard("a1", Card(CardRank.TWO, CardSuit.OUROS)),
                PlayedCard("b1", Card(CardRank.THREE, CardSuit.OUROS)),
                PlayedCard("a2", Card(CardRank.ACE, CardSuit.OUROS)),
                PlayedCard("b2", Card(CardRank.FOUR, CardSuit.OUROS))
            ),
            mapOf("a1" to "A", "a2" to "A", "b1" to "B", "b2" to "B"),
            vira, rules
        )
        assertEquals("B", result.winningTeamId)
    }

    @Test fun sameTeamTopCardsDoNotCreateFalseTie() {
        val result = TrucoMatchEngine.resolveTrick(
            listOf(
                PlayedCard("a1", Card(CardRank.THREE, CardSuit.OUROS)),
                PlayedCard("b1", Card(CardRank.TWO, CardSuit.OUROS)),
                PlayedCard("a2", Card(CardRank.ACE, CardSuit.OUROS)),
                PlayedCard("b2", Card(CardRank.FOUR, CardSuit.OUROS))
            ),
            mapOf("a1" to "A", "a2" to "A", "b1" to "B", "b2" to "B"),
            vira, rules
        )
        assertEquals("A", result.winningTeamId)
    }

    @Test fun equalBestCardsFromOpposingTeamsTie() {
        val result = TrucoMatchEngine.resolveTrick(
            listOf(
                PlayedCard("a", Card(CardRank.THREE, CardSuit.OUROS)),
                PlayedCard("b", Card(CardRank.THREE, CardSuit.PAUS))
            ),
            mapOf("a" to "A", "b" to "B"), vira, rules
        )
        assertNull(result.winningTeamId)
    }

    @Test fun handResolutionHandlesStandardTiePatterns() {
        assertEquals(HandResult.Winner("A", 2), TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome("A"), TrickOutcome("A"))
        ))
        assertEquals(HandResult.Winner("B", 1), TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome(null), TrickOutcome("B"))
        ))
        assertEquals(HandResult.Winner("A", 1), TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome("A"), TrickOutcome(null))
        ))
        assertEquals(HandResult.Winner("B", 2), TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome("A"), TrickOutcome("B"), TrickOutcome("B"))
        ))
        assertEquals(HandResult.Winner("A", 1), TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome("A"), TrickOutcome("B"), TrickOutcome(null))
        ))
        assertEquals(HandResult.Tie, TrucoMatchEngine.resolveHand(
            listOf(TrickOutcome(null), TrickOutcome(null), TrickOutcome(null))
        ))
    }

    @Test fun profilesExposeDifferentScoreBaselinesButBothUseVira() {
        val paulista = TrucoRules.paulista()
        val mineiro = TrucoRules.mineiro()
        assertEquals(listOf(1, 3, 6, 9, 12), paulista.scoreLadder)
        assertEquals(listOf(2, 4, 6, 10, 12), mineiro.scoreLadder)
        assertEquals(11, paulista.specialScore)
        assertEquals(10, mineiro.specialScore)
        assertEquals(CardRank.FIVE, TrucoCardSystem.manilhaRank(vira))
    }

    @Test fun specialHandFoldAwardsConfiguredForfeitValue() {
        val score = MatchScore(teamA = 11, teamB = 7)
        val updated = TrucoScoring.applySpecialDecision(
            score, "A", SpecialHandDecision.FOLD, rules
        )
        assertEquals(MatchScore(11, 8), updated)
    }

    @Test fun ironHandWinnerClosesMatch() {
        val score = MatchScore(11, 11)
        assertTrue(TrucoScoring.isIronHand(score, rules))
        val updated = TrucoScoring.awardHand(
            score, HandResult.Winner("B", 2), 3, rules
        )
        assertEquals(12, updated.teamB)
    }

    @Test fun bettingRequiresOpposingTeamToRaiseAndFoldAwardsPreviousValue() {
        val first = TrucoBetState.initial(rules).raise("A", rules)
        assertEquals(3, first.pendingValue)
        assertEquals(1, first.fold())
        val accepted = first.accept()
        assertEquals(3, accepted.acceptedValue)
        assertFalse(accepted.pendingValue != null)
        val next = accepted.raise("B", rules)
        assertEquals(6, next.pendingValue)

        val directCounter = first.raise("B", rules)
        assertEquals(6, directCounter.pendingValue)
        assertEquals(1, directCounter.fold())
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotResolveTrickWithoutAllPlayers() {
        TrucoMatchEngine.resolveTrick(
            listOf(PlayedCard("a", Card(CardRank.THREE, CardSuit.PAUS))),
            mapOf("a" to "A", "b" to "B"), vira, rules
        )
    }
}
