package com.example.playlistmaker.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.playlist.data.db.PlaylistDao
import com.example.playlistmaker.playlist.data.db.PlaylistTrackDao
import com.example.playlistmaker.playlist.data.mapper.PlaylistDbConverter
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

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS playlists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                imagePath TEXT,
                trackIds TEXT NOT NULL,
                trackCount INTEGER NOT NULL
            )"""
        )
    }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS playlist_tracks (
                trackId INTEGER PRIMARY KEY NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTimeMillis INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL,
                addedTimestamp INTEGER NOT NULL
            )"""
        )
    }
}

val dataModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "playlist_maker.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    single<FavoriteTrackDao> {
        get<AppDatabase>().favoriteTrackDao()
    }

    single<PlaylistDao> {
        get<AppDatabase>().playlistDao()
    }

    single<PlaylistTrackDao> {
        get<AppDatabase>().playlistTrackDao()
    }

    single { PlaylistDbConverter(get()) }
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
