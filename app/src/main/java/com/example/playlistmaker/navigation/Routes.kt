package com.example.playlistmaker.navigation

import kotlinx.serialization.Serializable

@Serializable
object SearchRoute

@Serializable
object MediaLibraryRoute

@Serializable
object SettingsRoute

@Serializable
data class AudioPlayerRoute(val trackJson: String)

@Serializable
object CreatePlaylistRoute

@Serializable
data class PlaylistDetailsRoute(val playlistId: Int)

@Serializable
data class EditPlaylistRoute(val playlistId: Int)
