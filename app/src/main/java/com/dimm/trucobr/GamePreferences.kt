package com.dimm.trucobr

import android.content.Context

data class GamePreferences(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val botDifficulty: String = "Normal"
)

object PreferencesStore {
    private const val FILE = "truco_br_preferences"

    fun load(context: Context): GamePreferences {
        val values = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return GamePreferences(
            soundEnabled = values.getBoolean("sound", true),
            vibrationEnabled = values.getBoolean("vibration", true),
            animationsEnabled = values.getBoolean("animations", true),
            botDifficulty = values.getString("difficulty", "Normal") ?: "Normal"
        )
    }

    fun save(context: Context, preferences: GamePreferences) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putBoolean("sound", preferences.soundEnabled)
            .putBoolean("vibration", preferences.vibrationEnabled)
            .putBoolean("animations", preferences.animationsEnabled)
            .putString("difficulty", preferences.botDifficulty)
            .apply()
    }
}
