package com.example.playlistmaker.settings.domain.applier

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.ThemeDataSource

class ThemeApplier(
    private val themeDataSource: ThemeDataSource
) {
    fun applyTheme() {
        val isDark: Boolean = themeDataSource.getTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
