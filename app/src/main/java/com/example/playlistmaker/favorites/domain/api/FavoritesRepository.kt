package com.example.playlistmaker.favorites.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    suspend fun addToFavorites(track: Track)

    suspend fun removeFromFavorites(trackId: Int)

    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun getFavoriteTrackIds(): List<Int>
}
