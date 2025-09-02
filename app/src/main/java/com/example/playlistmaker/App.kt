package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
class App : Application() {
  
   var darkTheme = false
  
   override fun onCreate() {
       super.onCreate()
       
       val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
       darkTheme = sharedPrefs.getBoolean("dark_theme", false)
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
   }
} 