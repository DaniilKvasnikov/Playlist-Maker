package com.example.playlistmaker.playlist.domain.api

import android.net.Uri
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist): Long
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getAllPlaylists(): List<Playlist>
    suspend fun getPlaylistById(id: Int): Playlist?
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun getTracksByIds(trackIds: List<Int>): List<Track>
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int)
    suspend fun deletePlaylist(playlistId: Int)
    fun saveImageToStorage(uri: Uri): String
}
