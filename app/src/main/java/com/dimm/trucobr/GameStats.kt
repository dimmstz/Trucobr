package com.dimm.trucobr

import android.content.Context

data class GameStats(val played: Int = 0, val wins: Int = 0, val losses: Int = 0) {
    val winRate: Int get() = if (played == 0) 0 else (wins * 100 / played)
}

object StatsStore {
    private const val FILE = "truco_br_stats"

    fun load(context: Context): GameStats {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return GameStats(
            played = prefs.getInt("played", 0),
            wins = prefs.getInt("wins", 0),
            losses = prefs.getInt("losses", 0)
        )
    }

    fun record(context: Context, won: Boolean) {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("played", prefs.getInt("played", 0) + 1)
            .putInt("wins", prefs.getInt("wins", 0) + if (won) 1 else 0)
            .putInt("losses", prefs.getInt("losses", 0) + if (won) 0 else 1)
            .apply()
    }

    fun reset(context: Context) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
