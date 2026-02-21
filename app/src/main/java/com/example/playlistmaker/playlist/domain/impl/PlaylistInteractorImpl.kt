package com.example.playlistmaker.playlist.domain.impl

import android.net.Uri
import com.example.playlistmaker.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return playlistRepository.getPlaylistById(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistRepository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        return playlistRepository.getTracksByIds(trackIds)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int) {
        playlistRepository.removeTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override fun saveImageToStorage(uri: Uri): String {
        return playlistRepository.saveImageToStorage(uri)
    }
}
