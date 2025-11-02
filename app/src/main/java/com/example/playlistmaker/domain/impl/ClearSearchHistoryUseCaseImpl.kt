package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.api.SearchHistoryRepository

class ClearSearchHistoryUseCaseImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : ClearSearchHistoryUseCase {

    override fun invoke() {
        searchHistoryRepository.clearHistory()
    }
}
