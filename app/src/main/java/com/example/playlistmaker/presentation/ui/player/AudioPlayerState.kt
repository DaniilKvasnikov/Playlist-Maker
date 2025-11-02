package com.example.playlistmaker.presentation.ui.player

import com.example.playlistmaker.domain.models.Track

sealed class AudioPlayerState {
    data class Prepared(val track: Track) : AudioPlayerState()
    data class Playing(val track: Track, val position: Int = 0) : AudioPlayerState()
    data class Paused(val track: Track, val position: Int) : AudioPlayerState()
    data class Completed(val track: Track) : AudioPlayerState()
}
