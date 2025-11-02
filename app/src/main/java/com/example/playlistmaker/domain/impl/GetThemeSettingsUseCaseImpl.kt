package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeSettings

class GetThemeSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository
) : GetThemeSettingsUseCase {

    override fun invoke(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }
}
