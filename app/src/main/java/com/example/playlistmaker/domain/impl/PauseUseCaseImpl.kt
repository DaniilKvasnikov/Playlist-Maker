package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PauseUseCase
import com.example.playlistmaker.domain.api.PlayerRepository

class PauseUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PauseUseCase {

    override fun invoke() {
        playerRepository.pause()
    }
}
