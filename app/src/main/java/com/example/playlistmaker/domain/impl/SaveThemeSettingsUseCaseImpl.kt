package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings

class SaveThemeSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : SaveThemeSettingsUseCase {

    override fun invoke(settings: ThemeSettings) {
        settingsRepository.saveThemeSettings(settings)
    }
}
