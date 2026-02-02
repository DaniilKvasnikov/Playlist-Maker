package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage,
    private val mapper: TrackMapper,
    private val appDatabase: AppDatabase
) : SearchHistoryRepository {

    override suspend fun getHistory(): List<Track> {
        val tracks = mapper.mapDtoListToDomainList(storage.getHistory())

        val favoriteIds = appDatabase.favoriteTrackDao().getTrackIds()
        tracks.forEach { track ->
            track.isFavorite = favoriteIds.contains(track.trackId)
        }

        return tracks
    }

    override fun addTrack(track: Track) {
        val trackDto = mapper.mapDomainToDto(track)
        storage.addTrack(trackDto)
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}
