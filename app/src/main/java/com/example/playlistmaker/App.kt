package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
class App : Application() {
  
   var darkTheme = false
   private val sharedPreferences by lazy { getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE) }
  
   override fun onCreate() {
       super.onCreate()
       
       darkTheme = sharedPreferences.getBoolean("dark_theme", false)
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
       sharedPreferences.edit { putBoolean("dark_theme", darkThemeEnabled) }
   }
}