package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ThemeStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        THEME_PREFERENCES, Context.MODE_PRIVATE
    )

    fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit {
            putBoolean(DARK_THEME_KEY, isDark)
        }
    }

    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    companion object {
        private const val THEME_PREFERENCES = "app_settings"
        private const val DARK_THEME_KEY = "dark_theme"
    }
}
