package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.MediaPlayerFactory
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayerFactory: MediaPlayerFactory
) : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        if (mediaPlayer == null) {
            mediaPlayer = mediaPlayerFactory.create()
        }
        mediaPlayer?.apply {
            reset()
            setDataSource(url)
            setOnPreparedListener {
                onPrepared()
            }
            setOnCompletionListener {
                onCompletion()
            }
            prepareAsync()
        }
    }

    override fun play() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.let { mediaPlayerFactory.release(it) }
        mediaPlayer = null
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}
