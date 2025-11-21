package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.IsPlayingUseCase
import com.example.playlistmaker.player.domain.api.PlayerRepository

class IsPlayingUseCaseImpl(
    private val repository: PlayerRepository
) : IsPlayingUseCase {
    override fun execute(): Boolean {
        return repository.isPlaying()
    }
}
