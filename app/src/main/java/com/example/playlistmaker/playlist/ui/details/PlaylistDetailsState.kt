package com.example.playlistmaker.playlist.ui.details

import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed class PlaylistDetailsState {
    data object Loading : PlaylistDetailsState()
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDurationMinutes: String
    ) : PlaylistDetailsState()
}
