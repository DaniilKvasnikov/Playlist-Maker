package com.example.playlistmaker.favorites.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.converters.TrackDbConverter
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        val entity = trackDbConverter.map(track)
        appDatabase.favoriteTrackDao().insertTrack(entity)
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        appDatabase.favoriteTrackDao().deleteTrack(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getAllTracks().map { entities ->
            entities.map { entity -> trackDbConverter.map(entity) }
        }
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return appDatabase.favoriteTrackDao().getTrackIds()
    }
}
