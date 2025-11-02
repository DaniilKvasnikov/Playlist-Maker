package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.ReleasePlayerUseCase

class ReleasePlayerUseCaseImpl(
    private val playerRepository: PlayerRepository
) : ReleasePlayerUseCase {

    override fun invoke() {
        playerRepository.release()
    }
}
