package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.GetCurrentPositionUseCase
import com.example.playlistmaker.player.domain.api.PlayerRepository

class GetCurrentPositionUseCaseImpl(
    private val repository: PlayerRepository
) : GetCurrentPositionUseCase {
    override fun execute(): Int {
        return repository.getCurrentPosition()
    }
}
