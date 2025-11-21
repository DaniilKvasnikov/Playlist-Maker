package com.example.playlistmaker.settings.data.applier

import androidx.appcompat.app.AppCompatDelegate

class ThemeApplier {

    fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
