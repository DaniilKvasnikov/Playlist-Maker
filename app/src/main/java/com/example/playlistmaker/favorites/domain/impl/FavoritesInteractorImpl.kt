package com.example.playlistmaker.favorites.domain.impl

import com.example.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.favorites.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(trackId: Int) {
        favoritesRepository.removeFromFavorites(trackId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return favoritesRepository.getFavoriteTrackIds().contains(trackId)
    }
}
