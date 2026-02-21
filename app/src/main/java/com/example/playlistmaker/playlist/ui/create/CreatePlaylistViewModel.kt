package com.example.playlistmaker.playlist.ui.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

data class CreatePlaylistState(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null
)

open class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<Pair<String, Long>>()
    val playlistCreated: LiveData<Pair<String, Long>> = _playlistCreated

    private val _state = MutableLiveData(CreatePlaylistState())
    val state: LiveData<CreatePlaylistState> = _state

    private val currentState get() = _state.value ?: CreatePlaylistState()

    fun setName(name: String) {
        _state.value = currentState.copy(name = name)
    }

    fun setDescription(description: String) {
        _state.value = currentState.copy(description = description)
    }

    fun setCoverUri(uri: Uri) {
        _state.value = currentState.copy(coverUri = uri)
    }

    fun hasUnsavedData(): Boolean {
        return currentState.coverUri != null || currentState.name.isNotBlank() || currentState.description.isNotBlank()
    }

    fun createPlaylist() {
        val s = currentState
        viewModelScope.launch {
            val imagePath = s.coverUri?.let { playlistInteractor.saveImageToStorage(it) }
            val playlist = Playlist(
                name = s.name,
                description = s.description,
                imagePath = imagePath,
                trackIds = emptyList(),
                trackCount = 0
            )
            val playlistId = playlistInteractor.addPlaylist(playlist)
            _playlistCreated.postValue(Pair(s.name, playlistId))
        }
    }
}
