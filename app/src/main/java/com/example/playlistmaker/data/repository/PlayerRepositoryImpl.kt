package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.data.api.MediaPlayerFactory
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayerFactory: MediaPlayerFactory
) : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(url: String) {
        if (mediaPlayer == null) {
            mediaPlayer = mediaPlayerFactory.create()
        }
        mediaPlayer?.apply {
            reset()
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun setOnCompletionListener(callback: () -> Unit) {
        mediaPlayer?.setOnCompletionListener {
            callback()
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
