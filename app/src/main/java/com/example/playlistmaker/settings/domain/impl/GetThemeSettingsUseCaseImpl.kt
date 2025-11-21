package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class GetThemeSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : GetThemeSettingsUseCase {

    override fun invoke(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }
}
