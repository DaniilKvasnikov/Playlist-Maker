package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchTracksUseCase
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class SearchTracksUseCaseImpl(
    private val tracksRepository: TracksRepository
) : SearchTracksUseCase {

    override fun invoke(query: String): Flow<Result<List<Track>>> = flow {
        if (query.isBlank()) {
            emit(Result.failure(IllegalArgumentException("Query cannot be blank")))
        } else {
            emitAll(tracksRepository.searchTracks(query))
        }
    }
}
