package com.example.playlistmaker.di

import com.example.playlistmaker.data.db.converters.TrackDbConverter
import com.example.playlistmaker.favorites.data.FavoritesRepositoryImpl
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.impl.NavigationRepositoryImpl
import com.example.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.NavigationRepository
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory { TrackDbConverter() }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    // Player repository
    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    // Settings repositories
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<NavigationRepository> {
        NavigationRepositoryImpl(androidContext())
    }
}
