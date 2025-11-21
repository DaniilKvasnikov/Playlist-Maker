package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.settings.domain.models.ThemeSettings

sealed class SettingsState {
    data class Loaded(val settings: ThemeSettings) : SettingsState()
}
