package com.example.playlistmaker.playlist.data.mapper

import com.example.playlistmaker.playlist.data.db.PlaylistEntity
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        val trackIds: List<Int> = gson.fromJson(entity.trackIds, type) ?: emptyList()
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            imagePath = entity.imagePath,
            trackIds = trackIds,
            trackCount = entity.trackCount
        )
    }
}
