package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.data.applier.ThemeApplier
import com.example.playlistmaker.settings.data.storage.ThemeStorage
import com.example.playlistmaker.settings.domain.api.ApplyThemeUseCase

class ApplyThemeUseCaseImpl(
    private val themeApplier: ThemeApplier,
    private val themeStorage: ThemeStorage
) : ApplyThemeUseCase {

    override fun invoke(isDark: Boolean) {
        themeStorage.saveTheme(isDark)
        themeApplier.applyTheme(isDark)
    }
}
