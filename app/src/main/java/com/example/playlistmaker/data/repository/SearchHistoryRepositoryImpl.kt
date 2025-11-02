package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.local.SearchHistoryStorage
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

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
