package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface GetSearchHistoryUseCase {
    operator fun invoke(): List<Track>
}
