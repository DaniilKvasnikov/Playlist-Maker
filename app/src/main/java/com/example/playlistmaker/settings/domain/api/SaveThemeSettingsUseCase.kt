package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface SaveThemeSettingsUseCase {
    operator fun invoke(settings: ThemeSettings)
}
