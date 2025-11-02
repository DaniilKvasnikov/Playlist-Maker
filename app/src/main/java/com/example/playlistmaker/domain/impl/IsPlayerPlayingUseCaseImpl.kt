package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.IsPlayerPlayingUseCase
import com.example.playlistmaker.domain.api.PlayerRepository

class IsPlayerPlayingUseCaseImpl(
    private val playerRepository: PlayerRepository
) : IsPlayerPlayingUseCase {

    override fun invoke(): Boolean {
        return playerRepository.isPlaying()
    }
}
