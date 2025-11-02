package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchTracksUseCase {
    suspend operator fun invoke(query: String): Result<List<Track>>
}
