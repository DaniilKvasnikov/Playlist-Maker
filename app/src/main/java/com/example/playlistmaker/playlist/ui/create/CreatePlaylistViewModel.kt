package com.example.playlistmaker.playlist.ui.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.models.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<String>()
    val playlistCreated: LiveData<String> = _playlistCreated

    private var coverUri: Uri? = null
    var name: String = ""
    var description: String = ""

    fun setCoverUri(uri: Uri) {
        coverUri = uri
    }

    fun hasUnsavedData(): Boolean {
        return coverUri != null || name.isNotBlank() || description.isNotBlank()
    }

    fun createPlaylist() {
        viewModelScope.launch {
            val imagePath = coverUri?.let { playlistInteractor.saveImageToStorage(it) }
            val playlist = Playlist(
                name = name,
                description = description,
                imagePath = imagePath,
                trackIds = emptyList(),
                trackCount = 0
            )
            playlistInteractor.addPlaylist(playlist)
            _playlistCreated.postValue(name)
        }
    }
}
