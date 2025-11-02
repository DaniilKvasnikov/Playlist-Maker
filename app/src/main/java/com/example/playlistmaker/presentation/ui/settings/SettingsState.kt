package com.example.playlistmaker.presentation.ui.settings

import com.example.playlistmaker.domain.models.ThemeSettings

sealed class SettingsState {
    data class Loaded(val settings: ThemeSettings) : SettingsState()
    data class ThemeChanged(val isDark: Boolean) : SettingsState()
}
