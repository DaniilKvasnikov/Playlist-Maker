package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.local.SettingsStorage
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings

class SettingsRepositoryImpl(
    private val storage: SettingsStorage
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(isDarkTheme = storage.isDarkTheme())
    }

    override fun saveThemeSettings(settings: ThemeSettings) {
        storage.saveDarkTheme(settings.isDarkTheme)
    }
}
