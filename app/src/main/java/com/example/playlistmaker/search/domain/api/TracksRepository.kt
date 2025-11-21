package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}
