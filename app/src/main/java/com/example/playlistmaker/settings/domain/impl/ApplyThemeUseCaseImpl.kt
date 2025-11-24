package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.applier.ThemeApplier
import com.example.playlistmaker.settings.domain.api.ApplyThemeUseCase
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class ApplyThemeUseCaseImpl(
    private val themeApplier: ThemeApplier,
    private val settingsRepository: SettingsRepository
) : ApplyThemeUseCase {

    override fun invoke(isDark: Boolean) {
        settingsRepository.saveThemeSettings(ThemeSettings(isDark))
        themeApplier.applyTheme()
    }
}
