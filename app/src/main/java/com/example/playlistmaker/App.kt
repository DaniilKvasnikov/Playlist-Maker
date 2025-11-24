package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.settings.data.applier.ThemeApplier
import com.example.playlistmaker.settings.domain.api.ThemeDataSource
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
       super.onCreate()
       startKoin {
           androidLogger()
           androidContext(this@App)
           modules(repositoryModule)
       }
        val themeApplier: ThemeApplier by inject()
        val themeDataSource: ThemeDataSource by inject()
        themeApplier.applyTheme(themeDataSource.getTheme())
   }
}