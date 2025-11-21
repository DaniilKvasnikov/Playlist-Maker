package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.domain.api.ThemeDataSource
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(
    private val themeDataSource: ThemeDataSource
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(isDarkTheme = themeDataSource.getTheme())
    }

    override fun saveThemeSettings(settings: ThemeSettings) {
        themeDataSource.saveTheme(settings.isDarkTheme)
    }
}
