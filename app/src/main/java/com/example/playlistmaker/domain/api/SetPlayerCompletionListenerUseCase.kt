package com.example.playlistmaker.domain.api

interface SetPlayerCompletionListenerUseCase {
    operator fun invoke(callback: () -> Unit)
}
