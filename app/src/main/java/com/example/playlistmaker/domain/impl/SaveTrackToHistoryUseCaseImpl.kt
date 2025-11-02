package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SaveTrackToHistoryUseCase
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SaveTrackToHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SaveTrackToHistoryUseCase {

    override fun invoke(track: Track) {
        searchHistoryRepository.addTrack(track)
    }
}
