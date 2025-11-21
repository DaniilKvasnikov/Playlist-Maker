package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.ApplyThemeUseCase
import com.example.playlistmaker.settings.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.models.ThemeSettings
import com.example.playlistmaker.settings.domain.api.ShareAppUseCase
import com.example.playlistmaker.settings.domain.api.OpenSupportUseCase
import com.example.playlistmaker.settings.domain.api.OpenTermsUseCase

class SettingsViewModel(
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val saveThemeSettingsUseCase: SaveThemeSettingsUseCase,
    private val applyThemeUseCase: ApplyThemeUseCase,
    private val shareAppUseCase: ShareAppUseCase,
    private val openSupportUseCase: OpenSupportUseCase,
    private val openTermsUseCase: OpenTermsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    init {
        loadSettings()
    }

    fun loadSettings() {
        val settings = getThemeSettingsUseCase()
        _state.value = SettingsState.Loaded(settings)
    }

    fun toggleTheme(isDark: Boolean) {
        val settings = ThemeSettings(isDarkTheme = isDark)
        saveThemeSettingsUseCase(settings)
        applyThemeUseCase(isDark)
        _state.value = SettingsState.Loaded(settings)
    }

    fun shareApp() {
        shareAppUseCase.execute()
    }

    fun openSupport() {
        openSupportUseCase.execute()
    }

    fun openTerms() {
        openTermsUseCase.execute()
    }
}
