package com.example.playlistmaker.playlist.domain.models

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imagePath: String?,
    val trackIds: List<Int> = emptyList(),
    val trackCount: Int = 0
)
