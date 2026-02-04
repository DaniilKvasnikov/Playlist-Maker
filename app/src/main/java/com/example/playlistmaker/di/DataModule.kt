package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.player.domain.api.MediaPlayerFactory
import com.example.playlistmaker.player.data.factory.AndroidMediaPlayerFactory
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.settings.domain.applier.ThemeApplier
import com.example.playlistmaker.settings.data.storage.SharedPreferencesThemeStorage
import com.example.playlistmaker.settings.domain.api.ThemeDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_maker.db"
        ).build()
    }

    single<FavoriteTrackDao> {
        get<AppDatabase>().favoriteTrackDao()
    }
    // JSON serialization
    single<Gson> {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create()
    }

    // Network layer
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single<ITunesApiService> {
        get<Retrofit>().create(ITunesApiService::class.java)
    }

    // Local storage
    single<SearchHistoryStorage> {
        SearchHistoryStorage(androidContext(), get())
    }

    single<ThemeDataSource> {
        SharedPreferencesThemeStorage(androidContext())
    }

    // Factories
    single<MediaPlayerFactory> {
        AndroidMediaPlayerFactory()
    }

    // Mappers
    single<TrackMapper> {
        TrackMapper
    }

    // Utilities
    single {
        ThemeApplier(get())
    }
}
