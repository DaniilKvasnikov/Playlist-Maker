package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.ui.models.TrackUI

sealed interface AudioPlayerState {
    data class Prepared(val track: TrackUI) : AudioPlayerState
    data class Playing(val track: TrackUI, val position: Int = 0) : AudioPlayerState
    data class Paused(val track: TrackUI, val position: Int) : AudioPlayerState
    data class Completed(val track: TrackUI) : AudioPlayerState
}
