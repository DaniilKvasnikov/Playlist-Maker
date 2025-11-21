package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.PlayUseCase

class PlayUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PlayUseCase {

    override fun invoke() {
        playerRepository.play()
    }
}
