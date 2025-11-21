package com.example.playlistmaker.settings.domain.api

interface ThemeDataSource {
    fun saveTheme(isDark: Boolean)
    fun getTheme(): Boolean
}