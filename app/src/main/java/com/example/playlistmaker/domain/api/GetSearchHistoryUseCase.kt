package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface GetSearchHistoryUseCase {
    operator fun invoke(): List<Track>
}
