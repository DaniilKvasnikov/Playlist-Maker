package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SetPlayerCompletionListenerUseCase

class SetPlayerCompletionListenerUseCaseImpl(
    private val playerRepository: PlayerRepository
) : SetPlayerCompletionListenerUseCase {

    override fun invoke(callback: () -> Unit) {
        playerRepository.setOnCompletionListener(callback)
    }
}
