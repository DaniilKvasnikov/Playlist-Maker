package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    suspend fun searchTracks(query: String): Result<List<Track>>
}
