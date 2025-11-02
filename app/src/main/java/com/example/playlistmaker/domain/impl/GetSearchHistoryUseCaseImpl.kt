package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class GetSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : GetSearchHistoryUseCase {

    override fun invoke(): List<Track> {
        return searchHistoryRepository.getHistory()
    }
}
