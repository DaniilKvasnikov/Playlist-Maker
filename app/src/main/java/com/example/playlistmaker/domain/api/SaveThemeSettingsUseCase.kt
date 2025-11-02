package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ThemeSettings

interface SaveThemeSettingsUseCase {
    operator fun invoke(settings: ThemeSettings)
}
