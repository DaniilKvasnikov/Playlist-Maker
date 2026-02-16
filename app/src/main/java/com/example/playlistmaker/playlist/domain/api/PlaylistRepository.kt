package com.example.playlistmaker.playlist.domain.api

import android.net.Uri
import com.example.playlistmaker.playlist.domain.models.Playlist

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun saveImageToStorage(uri: Uri): String
}
