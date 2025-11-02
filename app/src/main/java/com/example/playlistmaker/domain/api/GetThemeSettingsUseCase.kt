package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.ThemeSettings

interface GetThemeSettingsUseCase {
    operator fun invoke(): ThemeSettings
}
