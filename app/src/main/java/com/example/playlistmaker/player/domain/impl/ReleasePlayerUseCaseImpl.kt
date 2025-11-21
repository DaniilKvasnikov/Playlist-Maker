package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.api.ReleasePlayerUseCase

class ReleasePlayerUseCaseImpl(
    private val playerRepository: PlayerRepository
) : ReleasePlayerUseCase {

    override fun invoke() {
        playerRepository.release()
    }
}
