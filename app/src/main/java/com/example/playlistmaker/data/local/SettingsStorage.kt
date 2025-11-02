package com.example.playlistmaker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SettingsStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SETTINGS_PREFERENCES, Context.MODE_PRIVATE
    )

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    fun saveDarkTheme(isDark: Boolean) {
        sharedPreferences.edit { putBoolean(THEME_KEY, isDark) }
    }

    companion object {
        private const val SETTINGS_PREFERENCES = "app_settings"
        private const val THEME_KEY = "dark_theme"
    }
}
