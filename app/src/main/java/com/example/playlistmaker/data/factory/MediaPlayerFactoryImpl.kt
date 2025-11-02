package com.example.playlistmaker.data.factory

import android.media.MediaPlayer
import com.example.playlistmaker.data.api.MediaPlayerFactory

/**
 * Реализация фабрики для создания экземпляров MediaPlayer.
 */
class MediaPlayerFactoryImpl : MediaPlayerFactory {
    override fun create(): MediaPlayer {
        return MediaPlayer()
    }
}
