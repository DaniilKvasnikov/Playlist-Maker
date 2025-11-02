package com.example.playlistmaker.domain.api

interface PreparePlayerUseCase {
    operator fun invoke(url: String)
}
