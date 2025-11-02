package com.example.playlistmaker.presentation.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.domain.models.ThemeSettings

class SettingsViewModel(
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val saveThemeSettingsUseCase: SaveThemeSettingsUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> = _state

    fun loadSettings() {
        val settings = getThemeSettingsUseCase()
        _state.value = SettingsState.Loaded(settings)
    }

    fun toggleTheme(isDark: Boolean) {
        val settings = ThemeSettings(isDarkTheme = isDark)
        saveThemeSettingsUseCase(settings)
        _state.value = SettingsState.ThemeChanged(isDark)
    }
}
