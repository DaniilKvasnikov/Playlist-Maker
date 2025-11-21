package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class GetSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : GetSearchHistoryUseCase {

    override fun invoke(): List<Track> {
        return searchHistoryRepository.getHistory()
    }
}
