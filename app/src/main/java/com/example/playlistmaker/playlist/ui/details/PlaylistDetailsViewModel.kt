package com.example.playlistmaker.playlist.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsState>()
    val state: LiveData<PlaylistDetailsState> = _state

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
}
