package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class SearchState {
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    object None : SearchState()
}
