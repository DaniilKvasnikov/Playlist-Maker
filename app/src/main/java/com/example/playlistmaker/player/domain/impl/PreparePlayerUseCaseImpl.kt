package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.PreparePlayerUseCase

class PreparePlayerUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PreparePlayerUseCase {

    override fun invoke(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompletion)
    }
}
