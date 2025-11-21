package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class ClearSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : ClearSearchHistoryUseCase {

    override fun invoke() {
        searchHistoryRepository.clearHistory()
    }
}
