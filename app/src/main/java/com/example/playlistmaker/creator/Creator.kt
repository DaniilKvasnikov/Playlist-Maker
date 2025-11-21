package com.example.playlistmaker.creator

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.settings.data.storage.ThemeStorage
import com.example.playlistmaker.settings.data.applier.ThemeApplier
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.*
import com.example.playlistmaker.settings.domain.api.*
import com.example.playlistmaker.player.domain.api.*
import com.example.playlistmaker.search.domain.impl.*
import com.example.playlistmaker.settings.domain.impl.*
import com.example.playlistmaker.player.domain.impl.*
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.settings.domain.impl.OpenSupportUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.OpenTermsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.ShareAppUseCaseImpl
import com.example.playlistmaker.settings.data.impl.NavigationRepositoryImpl

object Creator {

    private lateinit var application: Application

    fun init(app: Application) {
        application = app
        themeApplier.applyTheme(themeStorage.getTheme())
    }

    // Network
    private fun getITunesApiService() = NetworkClient.getITunesApi()

    // Storages
    private val searchHistoryStorage: SearchHistoryStorage by lazy {
        SearchHistoryStorage(application)
    }

    private val themeStorage: ThemeStorage by lazy {
        ThemeStorage(application)
    }

    private val themeApplier: ThemeApplier by lazy {
        ThemeApplier()
    }

    // Mappers
    private fun getTrackMapper() = TrackMapper

    // Repositories
    private val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(
            apiService = getITunesApiService(),
            mapper = getTrackMapper()
        )
    }

    private val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(
            storage = searchHistoryStorage,
            mapper = getTrackMapper()
        )
    }

    private val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(
            themeStorage = themeStorage
        )
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl()
    }

    private val navigationRepository: NavigationRepository by lazy {
        NavigationRepositoryImpl(application.applicationContext)
    }

    // Use Cases
    fun getSearchTracksUseCase(): SearchTracksUseCase {
        return SearchTracksUseCaseImpl(tracksRepository)
    }

    fun getSearchHistoryUseCase(): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCaseImpl(searchHistoryRepository)
    }

    fun getSaveTrackToHistoryUseCase(): SaveTrackToHistoryUseCase {
        return SaveTrackToHistoryUseCaseImpl(searchHistoryRepository)
    }

    fun getClearSearchHistoryUseCase(): ClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCaseImpl(searchHistoryRepository)
    }

    fun getThemeSettingsUseCase(): GetThemeSettingsUseCase {
        return GetThemeSettingsUseCaseImpl(settingsRepository)
    }

    fun getSaveThemeSettingsUseCase(): SaveThemeSettingsUseCase {
        return SaveThemeSettingsUseCaseImpl(settingsRepository)
    }

    fun getApplyThemeUseCase(): ApplyThemeUseCase {
        return ApplyThemeUseCaseImpl(
            themeApplier = themeApplier,
            themeStorage = themeStorage
        )
    }

    fun getPreparePlayerUseCase(): PreparePlayerUseCase {
        return PreparePlayerUseCaseImpl(playerRepository)
    }

    fun getPlayUseCase(): PlayUseCase {
        return PlayUseCaseImpl(playerRepository)
    }

    fun getPauseUseCase(): PauseUseCase {
        return PauseUseCaseImpl(playerRepository)
    }

    fun getReleasePlayerUseCase(): ReleasePlayerUseCase {
        return ReleasePlayerUseCaseImpl(playerRepository)
    }

    fun getGetCurrentPositionUseCase(): GetCurrentPositionUseCase {
        return GetCurrentPositionUseCaseImpl(playerRepository)
    }

    fun getIsPlayingUseCase(): IsPlayingUseCase {
        return IsPlayingUseCaseImpl(playerRepository)
    }

    fun getShareAppUseCase(): ShareAppUseCase {
        return ShareAppUseCaseImpl(navigationRepository)
    }

    fun getOpenSupportUseCase(): OpenSupportUseCase {
        return OpenSupportUseCaseImpl(navigationRepository)
    }

    fun getOpenTermsUseCase(): OpenTermsUseCase {
        return OpenTermsUseCaseImpl(navigationRepository)
    }

    // ViewModelFactories
    fun provideSearchViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                    return SearchViewModel(
                        searchTracksUseCase = getSearchTracksUseCase(),
                        getSearchHistoryUseCase = getSearchHistoryUseCase(),
                        saveTrackToHistoryUseCase = getSaveTrackToHistoryUseCase(),
                        clearSearchHistoryUseCase = getClearSearchHistoryUseCase()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    fun provideAudioPlayerViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AudioPlayerViewModel::class.java)) {
                    return AudioPlayerViewModel(
                        preparePlayerUseCase = getPreparePlayerUseCase(),
                        playUseCase = getPlayUseCase(),
                        pauseUseCase = getPauseUseCase(),
                        releasePlayerUseCase = getReleasePlayerUseCase(),
                        getCurrentPositionUseCase = getGetCurrentPositionUseCase(),
                        isPlayingUseCase = getIsPlayingUseCase()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    fun provideSettingsViewModelFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    return SettingsViewModel(
                        getThemeSettingsUseCase = getThemeSettingsUseCase(),
                        saveThemeSettingsUseCase = getSaveThemeSettingsUseCase(),
                        applyThemeUseCase = getApplyThemeUseCase(),
                        shareAppUseCase = getShareAppUseCase(),
                        openSupportUseCase = getOpenSupportUseCase(),
                        openTermsUseCase = getOpenTermsUseCase()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
