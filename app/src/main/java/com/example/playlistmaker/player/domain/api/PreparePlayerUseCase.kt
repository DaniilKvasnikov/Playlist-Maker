package com.example.playlistmaker.player.domain.api

interface PreparePlayerUseCase {
    operator fun invoke(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
}
