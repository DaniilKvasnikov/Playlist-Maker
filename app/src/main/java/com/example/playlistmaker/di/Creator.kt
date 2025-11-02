package com.example.playlistmaker.di

import android.app.Application
import com.example.playlistmaker.data.api.MediaPlayerFactory
import com.example.playlistmaker.data.factory.MediaPlayerFactoryImpl
import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.local.SettingsStorage
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.*
import com.example.playlistmaker.domain.impl.*

object Creator {

    private lateinit var application: Application

    fun init(app: Application) {
        application = app
    }

    // Network
    private fun getITunesApiService() = NetworkClient.getITunesApi()

    // Storages
    private val searchHistoryStorage: SearchHistoryStorage by lazy {
        SearchHistoryStorage(application)
    }

    private val settingsStorage: SettingsStorage by lazy {
        SettingsStorage(application)
    }

    // Mappers
    private fun getTrackMapper() = TrackMapper

    // Factories
    private val mediaPlayerFactory: MediaPlayerFactory by lazy {
        MediaPlayerFactoryImpl()
    }

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
            storage = settingsStorage
        )
    }

    private val playerRepository: PlayerRepository by lazy {
        PlayerRepositoryImpl(
            mediaPlayerFactory = mediaPlayerFactory
        )
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

    fun getPlayerPositionUseCase(): GetPlayerPositionUseCase {
        return GetPlayerPositionUseCaseImpl(playerRepository)
    }

    fun getIsPlayerPlayingUseCase(): IsPlayerPlayingUseCase {
        return IsPlayerPlayingUseCaseImpl(playerRepository)
    }

    fun getSetPlayerCompletionListenerUseCase(): SetPlayerCompletionListenerUseCase {
        return SetPlayerCompletionListenerUseCaseImpl(playerRepository)
    }

    // ViewModels
    fun provideSearchViewModel(): com.example.playlistmaker.presentation.ui.search.SearchViewModel {
        return com.example.playlistmaker.presentation.ui.search.SearchViewModel(
            searchTracksUseCase = getSearchTracksUseCase(),
            getSearchHistoryUseCase = getSearchHistoryUseCase(),
            saveTrackToHistoryUseCase = getSaveTrackToHistoryUseCase(),
            clearSearchHistoryUseCase = getClearSearchHistoryUseCase()
        )
    }

    fun provideAudioPlayerViewModel(): com.example.playlistmaker.presentation.ui.player.AudioPlayerViewModel {
        return com.example.playlistmaker.presentation.ui.player.AudioPlayerViewModel(
            preparePlayerUseCase = getPreparePlayerUseCase(),
            playUseCase = getPlayUseCase(),
            pauseUseCase = getPauseUseCase(),
            releasePlayerUseCase = getReleasePlayerUseCase(),
            getPlayerPositionUseCase = getPlayerPositionUseCase(),
            isPlayerPlayingUseCase = getIsPlayerPlayingUseCase(),
            setPlayerCompletionListenerUseCase = getSetPlayerCompletionListenerUseCase()
        )
    }

    fun provideSettingsViewModel(): com.example.playlistmaker.presentation.ui.settings.SettingsViewModel {
        return com.example.playlistmaker.presentation.ui.settings.SettingsViewModel(
            getThemeSettingsUseCase = getThemeSettingsUseCase(),
            saveThemeSettingsUseCase = getSaveThemeSettingsUseCase()
        )
    }
}
