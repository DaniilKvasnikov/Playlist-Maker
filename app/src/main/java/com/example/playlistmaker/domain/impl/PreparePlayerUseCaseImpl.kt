package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.PreparePlayerUseCase

class PreparePlayerUseCaseImpl(
    private val playerRepository: PlayerRepository
) : PreparePlayerUseCase {

    override fun invoke(url: String) {
        playerRepository.preparePlayer(url)
    }
}
