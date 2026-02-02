package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface GetSearchHistoryUseCase {
    suspend operator fun invoke(): List<Track>
}
