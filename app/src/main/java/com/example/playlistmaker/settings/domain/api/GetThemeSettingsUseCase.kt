package com.example.playlistmaker.settings.domain.api

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface GetThemeSettingsUseCase {
    operator fun invoke(): ThemeSettings
}
