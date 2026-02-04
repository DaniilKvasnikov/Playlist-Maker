package com.example.playlistmaker.favorites.data

import com.example.playlistmaker.data.db.converters.TrackDbConverter
import com.example.playlistmaker.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao,
    private val trackDbConverter: TrackDbConverter
) : FavoritesRepository {

    override suspend fun addToFavorites(track: Track) {
        val entity = trackDbConverter.map(track)
        favoriteTrackDao.insertTrack(entity)
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        favoriteTrackDao.deleteTrack(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getAllTracks()
            .map { entities -> entities.map { entity -> trackDbConverter.map(entity) } }
            .distinctUntilChanged()
    }

    override suspend fun getFavoriteTrackIds(): List<Int> {
        return favoriteTrackDao.getTrackIds()
    }
}
