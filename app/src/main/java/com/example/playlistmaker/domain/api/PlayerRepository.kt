package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun preparePlayer(url: String)
    fun setOnCompletionListener(callback: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
}
