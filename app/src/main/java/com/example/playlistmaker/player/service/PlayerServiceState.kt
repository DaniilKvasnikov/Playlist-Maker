package com.example.playlistmaker.player.service

sealed interface PlayerServiceState {
    object Idle : PlayerServiceState
    object Prepared : PlayerServiceState
    data class Playing(val position: Int) : PlayerServiceState
    data class Paused(val position: Int) : PlayerServiceState
    object Completed : PlayerServiceState
}
