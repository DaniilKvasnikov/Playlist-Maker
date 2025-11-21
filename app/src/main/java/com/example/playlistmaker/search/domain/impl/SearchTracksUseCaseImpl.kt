package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchTracksUseCase
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchTracksUseCaseImpl(
    private val tracksRepository: TracksRepository
) : SearchTracksUseCase {

    override suspend fun invoke(query: String): Result<List<Track>> {
        if (query.isBlank()) {
            return Result.failure(IllegalArgumentException("Query cannot be blank"))
        }
        return tracksRepository.searchTracks(query)
    }
}
