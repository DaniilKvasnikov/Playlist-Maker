package com.example.playlistmaker.playlist.data.mapper

import com.example.playlistmaker.playlist.data.db.PlaylistEntity
import com.example.playlistmaker.playlist.data.db.PlaylistWithTracks
import com.example.playlistmaker.playlist.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistWithTracks: PlaylistWithTracks): Playlist {
        val entity = playlistWithTracks.playlist
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imagePath = entity.imagePath,
            trackIds = playlistWithTracks.tracks.sortedByDescending { it.addedTimestamp }.map { it.trackId },
            trackCount = entity.trackCount
        )
    }
}
