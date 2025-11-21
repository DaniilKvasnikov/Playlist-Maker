package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SaveThemeSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : SaveThemeSettingsUseCase {

    override fun invoke(settings: ThemeSettings) {
        settingsRepository.saveThemeSettings(settings)
    }
}
