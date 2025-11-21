package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchTracksUseCase {
    suspend operator fun invoke(query: String): Result<List<Track>>
}
