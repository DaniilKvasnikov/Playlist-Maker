package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.data.network.ITunesApiService
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(
    private val apiService: ITunesApiService,
    private val mapper: TrackMapper,
    private val appDatabase: AppDatabase
) : TracksRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = apiService.getMusics(query)
            if (response.isSuccessful) {
                val body = response.body()
                val tracks = body?.results?.let {
                    mapper.mapDtoListToDomainList(it)
                } ?: emptyList()

                val favoriteIds = appDatabase.favoriteTrackDao().getTrackIds()
                tracks.forEach { track ->
                    track.isFavorite = favoriteIds.contains(track.trackId)
                }

                emit(Result.success(tracks))
            } else {
                emit(Result.failure(Exception("API Error: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
