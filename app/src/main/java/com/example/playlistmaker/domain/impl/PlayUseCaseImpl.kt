package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.PlayUseCase

class PlayUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PlayUseCase {

    override fun invoke() {
        playerRepository.play()
    }
}
