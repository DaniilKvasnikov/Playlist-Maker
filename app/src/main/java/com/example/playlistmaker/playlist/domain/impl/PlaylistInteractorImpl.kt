package com.example.playlistmaker.playlist.domain.impl

import android.net.Uri
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override fun saveImageToStorage(uri: Uri): String {
        return playlistRepository.saveImageToStorage(uri)
    }
}
