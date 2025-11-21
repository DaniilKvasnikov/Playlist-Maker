package com.example.playlistmaker.settings.domain.api

interface ApplyThemeUseCase {
    operator fun invoke(isDark: Boolean)
}
