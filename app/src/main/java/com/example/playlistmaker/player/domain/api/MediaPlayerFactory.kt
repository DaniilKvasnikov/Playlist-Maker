package com.example.playlistmaker.player.domain.api

import android.media.MediaPlayer

interface MediaPlayerFactory {
    fun create(): MediaPlayer
    fun release(player: MediaPlayer)
}