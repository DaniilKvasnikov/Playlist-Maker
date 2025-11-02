package com.example.playlistmaker.data.api

import android.media.MediaPlayer

/**
 * Фабрика для создания экземпляров MediaPlayer.
 * Используется для инъекции зависимости в PlayerRepository.
 */
interface MediaPlayerFactory {
    fun create(): MediaPlayer
}
