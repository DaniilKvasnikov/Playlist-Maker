package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(
    private val apiService: ITunesApiService,
    private val mapper: TrackMapper
) : TracksRepository {

    override suspend fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = apiService.getMusics(query)
            if (response.isSuccessful) {
                val body = response.body()
                val tracks = body?.results?.let {
                    mapper.mapDtoListToDomainList(it)
                } ?: emptyList()
                Result.success(tracks)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
