package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SaveTrackToHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SaveTrackToHistoryUseCase {

    override fun invoke(track: Track) {
        searchHistoryRepository.addTrack(track)
    }
}
