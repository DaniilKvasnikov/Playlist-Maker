package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PauseUseCase
import com.example.playlistmaker.player.domain.api.PlayerRepository

class PauseUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PauseUseCase {

    override fun invoke() {
        playerRepository.pause()
    }
}
