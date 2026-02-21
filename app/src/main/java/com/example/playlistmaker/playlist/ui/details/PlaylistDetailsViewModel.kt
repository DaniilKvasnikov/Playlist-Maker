package com.example.playlistmaker.playlist.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsState>()
    val state: LiveData<PlaylistDetailsState> = _state

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    private var currentPlaylistId: Int = -1

    fun loadPlaylist(playlistId: Int) {
        currentPlaylistId = playlistId
        _state.value = PlaylistDetailsState.Loading
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId) ?: return@launch
            val tracks = playlistInteractor.getTracksByIds(playlist.trackIds)
            val durationSum = tracks.sumOf { it.trackTimeMillis }
            val totalMinutes = SimpleDateFormat("mm", Locale.getDefault()).format(durationSum)
            _state.postValue(
                PlaylistDetailsState.Content(
                    playlist = playlist,
                    tracks = tracks,
                    totalDurationMinutes = totalMinutes
                )
            )
        }
    }

    fun removeTrack(trackId: Int) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(trackId, currentPlaylistId)
            loadPlaylist(currentPlaylistId)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(currentPlaylistId)
            _playlistDeleted.postValue(true)
        }
    }

    fun getShareText(): String? {
        val state = _state.value
        if (state !is PlaylistDetailsState.Content) return null
        if (state.tracks.isEmpty()) return null

        val playlist = state.playlist
        val tracks = state.tracks

        return buildString {
            appendLine(playlist.name)
            if (playlist.description.isNotEmpty()) {
                appendLine(playlist.description)
            }
            appendLine(getTrackCountText(tracks.size))
            tracks.forEachIndexed { index, track ->
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} (${track.getFormattedTime()})")
            }
        }.trimEnd()
    }

    fun hasTracksToShare(): Boolean {
        val state = _state.value
        return state is PlaylistDetailsState.Content && state.tracks.isNotEmpty()
    }

    fun getCurrentPlaylist(): Playlist? {
        val state = _state.value
        return if (state is PlaylistDetailsState.Content) state.playlist else null
    }

    private fun getTrackCountText(count: Int): String {
        val lastTwo = count % 100
        val lastOne = count % 10
        val suffix = when {
            lastTwo in 11..19 -> "треков"
            lastOne == 1 -> "трек"
            lastOne in 2..4 -> "трека"
            else -> "треков"
        }
        return "$count $suffix"
    }
}
