package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.storage.ThemeStorage
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(
    private val themeStorage: ThemeStorage
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(isDarkTheme = themeStorage.getTheme())
    }

    override fun saveThemeSettings(settings: ThemeSettings) {
        themeStorage.saveTheme(settings.isDarkTheme)
    }
}
