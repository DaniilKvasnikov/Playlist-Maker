package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.factory.AndroidMediaPlayerFactory
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.GetCurrentPositionUseCase
import com.example.playlistmaker.player.domain.api.IsPlayingUseCase
import com.example.playlistmaker.player.domain.api.MediaPlayerFactory
import com.example.playlistmaker.player.domain.api.PauseUseCase
import com.example.playlistmaker.player.domain.api.PlayUseCase
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.PreparePlayerUseCase
import com.example.playlistmaker.player.domain.api.ReleasePlayerUseCase
import com.example.playlistmaker.player.domain.impl.GetCurrentPositionUseCaseImpl
import com.example.playlistmaker.player.domain.impl.IsPlayingUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PauseUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PlayUseCaseImpl
import com.example.playlistmaker.player.domain.impl.PreparePlayerUseCaseImpl
import com.example.playlistmaker.player.domain.impl.ReleasePlayerUseCaseImpl
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.SearchTracksUseCase
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.ClearSearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.GetSearchHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.SaveTrackToHistoryUseCaseImpl
import com.example.playlistmaker.search.domain.impl.SearchTracksUseCaseImpl
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.data.applier.ThemeApplier
import com.example.playlistmaker.settings.data.impl.NavigationRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.storage.SharedPreferencesThemeStorage
import com.example.playlistmaker.settings.domain.api.ApplyThemeUseCase
import com.example.playlistmaker.settings.domain.api.GetThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.NavigationRepository
import com.example.playlistmaker.settings.domain.api.OpenSupportUseCase
import com.example.playlistmaker.settings.domain.api.OpenTermsUseCase
import com.example.playlistmaker.settings.domain.api.SaveThemeSettingsUseCase
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.settings.domain.api.ShareAppUseCase
import com.example.playlistmaker.settings.domain.api.ThemeDataSource
import com.example.playlistmaker.settings.domain.impl.ApplyThemeUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.GetThemeSettingsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.OpenSupportUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.OpenTermsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.SaveThemeSettingsUseCaseImpl
import com.example.playlistmaker.settings.domain.impl.ShareAppUseCaseImpl
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {
    single { ThemeApplier() }
    single<NavigationRepository> { NavigationRepositoryImpl(androidContext()) }
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    single<MediaPlayerFactory> { AndroidMediaPlayerFactory() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<ThemeDataSource> { SharedPreferencesThemeStorage(androidContext()) }
    single<SearchHistoryStorage> { SearchHistoryStorage(androidApplication(), get()) }
    single<TracksRepository> { TracksRepositoryImpl(get(), get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    single<SearchTracksUseCase> { SearchTracksUseCaseImpl(get()) }
    single<GetSearchHistoryUseCase> { GetSearchHistoryUseCaseImpl(get()) }
    single<SaveTrackToHistoryUseCase> { SaveTrackToHistoryUseCaseImpl(get()) }
    single<ClearSearchHistoryUseCase> { ClearSearchHistoryUseCaseImpl(get()) }
    single<GetThemeSettingsUseCase> { GetThemeSettingsUseCaseImpl(get()) }
    single<SaveThemeSettingsUseCase> { SaveThemeSettingsUseCaseImpl(get()) }
    single<ApplyThemeUseCase> { ApplyThemeUseCaseImpl(get(), get()) }
    single<PreparePlayerUseCase> { PreparePlayerUseCaseImpl(get()) }
    single<PlayUseCase> { PlayUseCaseImpl(get()) }
    single<PauseUseCase> { PauseUseCaseImpl(get()) }
    single<ReleasePlayerUseCase> { ReleasePlayerUseCaseImpl(get()) }
    single<GetCurrentPositionUseCase> { GetCurrentPositionUseCaseImpl(get()) }
    single<IsPlayingUseCase> { IsPlayingUseCaseImpl(get()) }
    single<ShareAppUseCase> { ShareAppUseCaseImpl(get()) }
    single<OpenSupportUseCase> { OpenSupportUseCaseImpl(get()) }
    single<OpenTermsUseCase> { OpenTermsUseCaseImpl(get()) }
    single<TrackMapper> { TrackMapper }
    single<Gson> {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()
    }
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<ITunesApiService> { get<Retrofit>().create(ITunesApiService::class.java) }

    viewModel { SearchViewModel(get(), get(), get(), get()) }
    viewModel { AudioPlayerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get()) }
}
