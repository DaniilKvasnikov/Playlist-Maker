package com.example.playlistmaker.player.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.player.service.AudioPlayerController
import com.example.playlistmaker.player.service.AudioPlayerService
import com.example.playlistmaker.player.service.PlayerServiceState
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.ui.mappers.toDomain
import com.example.playlistmaker.search.ui.models.TrackUI
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _addToPlaylistResult = SingleLiveEvent<Pair<Boolean, String>>()
    val addToPlaylistResult: LiveData<Pair<Boolean, String>> = _addToPlaylistResult

    private var currentTrack: TrackUI? = null
    private var service: AudioPlayerController? = null
    private var stateCollectJob: Job? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val audioPlayerBinder = binder as? AudioPlayerService.AudioPlayerBinder ?: return
            onServiceBound(audioPlayerBinder.getService())
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            onServiceDisconnected()
        }
    }

    fun bindService(context: Context, intent: Intent) {
        if (!isServiceBound) {
            isServiceBound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        if (isServiceBound) {
            context.unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    fun setCurrentTrack(track: TrackUI) {
        currentTrack = track
        checkIsFavorite(track.trackId)
    }

    fun onServiceBound(service: AudioPlayerController) {
        this.service = service
        stateCollectJob?.cancel()
        stateCollectJob = viewModelScope.launch {
            service.playerState().collect { serviceState ->
                val track = currentTrack ?: return@collect
                when (serviceState) {
                    is PlayerServiceState.Idle -> Unit
                    is PlayerServiceState.Prepared -> _state.value = AudioPlayerState.Prepared(track)
                    is PlayerServiceState.Playing -> _state.value = AudioPlayerState.Playing(track, serviceState.position)
                    is PlayerServiceState.Paused -> _state.value = AudioPlayerState.Paused(track, serviceState.position)
                    is PlayerServiceState.Completed -> {
                        _state.value = AudioPlayerState.Completed(track)
                    }
                }
            }
        }
    }

    fun onUiVisible() {
        service?.hideNotification()
    }

    fun onUiHidden() {
        if (_state.value is AudioPlayerState.Playing) {
            service?.showNotification()
        }
    }

    fun playPause() {
        val svc = service ?: return
        when (_state.value) {
            is AudioPlayerState.Prepared,
            is AudioPlayerState.Paused,
            is AudioPlayerState.Completed -> svc.play()
            is AudioPlayerState.Playing -> svc.pause()
            else -> {}
        }
    }

    private fun checkIsFavorite(trackId: Int) {
        viewModelScope.launch {
            _isFavorite.postValue(favoritesInteractor.isTrackFavorite(trackId))
        }
    }

    fun onFavoriteClicked() {
        val track = currentTrack ?: return
        val currentFavorite = _isFavorite.value ?: false

        viewModelScope.launch {
            if (currentFavorite) {
                favoritesInteractor.removeFromFavorites(track.trackId)
            } else {
                favoritesInteractor.addToFavorites(track.toDomain())
            }
            _isFavorite.postValue(!currentFavorite)
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _playlists.postValue(playlistInteractor.getAllPlaylists())
        }
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        val track = currentTrack ?: return

        if (playlist.trackIds.contains(track.trackId)) {
            _addToPlaylistResult.value = Pair(false, playlist.name)
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(track.toDomain(), playlist)
                _addToPlaylistResult.postValue(Pair(true, playlist.name))
            }
        }
    }

    fun addTrackToPlaylistById(playlistId: Int) {
        val track = currentTrack ?: return
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId) ?: return@launch
            playlistInteractor.addTrackToPlaylist(track.toDomain(), playlist)
            _addToPlaylistResult.postValue(Pair(true, playlist.name))
        }
    }

    fun onServiceDisconnected() {
        stateCollectJob?.cancel()
        service = null
        val track = currentTrack ?: return
        _state.value = AudioPlayerState.Completed(track)
    }

    override fun onCleared() {
        super.onCleared()
        stateCollectJob?.cancel()
        service = null
    }
}
