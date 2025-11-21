package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.ui.models.TrackUI

sealed class SearchState {
    data class Content(val tracks: List<TrackUI>) : SearchState()
    data class History(val tracks: List<TrackUI>) : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    object None : SearchState()
}
