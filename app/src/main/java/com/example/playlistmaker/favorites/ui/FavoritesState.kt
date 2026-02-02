package com.example.playlistmaker.favorites.ui

import com.example.playlistmaker.search.ui.models.TrackUI

sealed interface FavoritesState {

    object Loading : FavoritesState

    data class Content(val tracks: List<TrackUI>) : FavoritesState

    object Empty : FavoritesState
}
