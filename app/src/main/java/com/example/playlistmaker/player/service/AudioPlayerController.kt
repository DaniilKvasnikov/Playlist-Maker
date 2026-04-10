package com.example.playlistmaker.player.service

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerController {
    fun play()
    fun pause()
    fun playerState(): StateFlow<PlayerServiceState>
    fun showNotification()
    fun hideNotification()
}
