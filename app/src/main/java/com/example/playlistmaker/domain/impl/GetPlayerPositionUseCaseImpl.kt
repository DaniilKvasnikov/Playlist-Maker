package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.GetPlayerPositionUseCase
import com.example.playlistmaker.domain.api.PlayerRepository

class GetPlayerPositionUseCaseImpl(
    private val playerRepository: PlayerRepository
) : GetPlayerPositionUseCase {

    override fun invoke(): Int {
        return playerRepository.getCurrentPosition()
    }
}
