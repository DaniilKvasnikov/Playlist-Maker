package com.example.playlistmaker.playlist.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.playlist.ui.create.CreatePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    private val _playlistLoaded = MutableLiveData<Playlist>()
    val playlistLoaded: LiveData<Playlist> = _playlistLoaded

    private val _playlistUpdated = MutableLiveData<Boolean>()
    val playlistUpdated: LiveData<Boolean> = _playlistUpdated

    private var editingPlaylist: Playlist? = null

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId) ?: return@launch
            editingPlaylist = playlist
            setName(playlist.name)
            setDescription(playlist.description)
            _playlistLoaded.postValue(playlist)
        }
    }

    fun savePlaylist() {
        val playlist = editingPlaylist ?: return
        val s = state.value ?: return
        viewModelScope.launch {
            val newImagePath = if (s.coverUri != null) {
                playlistInteractor.saveImageToStorage(s.coverUri)
            } else {
                playlist.imagePath
            }
            val updated = playlist.copy(
                name = s.name,
                description = s.description,
                imagePath = newImagePath
            )
            playlistInteractor.updatePlaylist(updated)
            _playlistUpdated.postValue(true)
        }
    }
}
