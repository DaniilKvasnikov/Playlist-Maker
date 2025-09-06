package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() {
  
   var darkTheme = false
   private val sharedPreferences by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE) }
  
   override fun onCreate() {
       super.onCreate()
       
       darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
       switchTheme(darkTheme)
   }
   
   fun switchTheme(darkThemeEnabled: Boolean) {
       darkTheme = darkThemeEnabled
       AppCompatDelegate.setDefaultNightMode(
           if (darkTheme) {
               AppCompatDelegate.MODE_NIGHT_YES
           } else {
               AppCompatDelegate.MODE_NIGHT_NO
           }
       )
       sharedPreferences.edit { putBoolean(DARK_THEME_KEY, darkThemeEnabled) }
   }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME_KEY = "dark_theme"
    }
}