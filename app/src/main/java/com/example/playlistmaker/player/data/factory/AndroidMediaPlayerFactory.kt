package com.example.playlistmaker.player.data.factory

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerFactory

class AndroidMediaPlayerFactory : MediaPlayerFactory {
    override fun create(): MediaPlayer = MediaPlayer()

    override fun release(player: MediaPlayer) {
        player.release()
    }
}