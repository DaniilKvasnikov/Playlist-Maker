package com.example.playlistmaker.settings.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.api.ThemeDataSource

class SharedPreferencesThemeStorage(context: Context) : ThemeDataSource {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        THEME_PREFERENCES, Context.MODE_PRIVATE
    )

    override fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit {
            putBoolean(DARK_THEME_KEY, isDark)
        }
    }

    override fun getTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    companion object {
        private const val THEME_PREFERENCES = "app_settings"
        private const val DARK_THEME_KEY = "dark_theme"
    }
}
