package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.local.SearchHistoryStorage
import com.example.playlistmaker.search.data.mapper.TrackMapper
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage,
    private val mapper: TrackMapper
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        return mapper.mapDtoListToDomainList(storage.getHistory())
    }

    override fun addTrack(track: Track) {
        val trackDto = mapper.mapDomainToDto(track)
        storage.addTrack(trackDto)
    }

    override fun clearHistory() {
        storage.clearHistory()
    }
}
