package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.ClearSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.GetSearchHistoryUseCase
import com.example.playlistmaker.search.domain.api.SaveTrackToHistoryUseCase
import com.example.playlistmaker.search.domain.api.SearchTracksUseCase
import com.example.playlistmaker.search.ui.mappers.toDomain
import com.example.playlistmaker.search.ui.mappers.toUI
import com.example.playlistmaker.search.ui.models.TrackUI
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksUseCase: SearchTracksUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveTrackToHistoryUseCase: SaveTrackToHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>(SearchState.None)
    val state: LiveData<SearchState> = _state

    fun searchTracks(query: String) {
        _state.value = SearchState.Loading

        viewModelScope.launch {
            searchTracksUseCase(query)
                .onSuccess { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.Content(tracks.toUI())
                    }
                }
                .onFailure {
                    _state.value = SearchState.Error
                }
        }
    }

    fun loadHistory() {
        val history = getSearchHistoryUseCase()
        if (history.isNotEmpty()) {
            _state.value = SearchState.History(history.toUI())
        } else {
            _state.value = SearchState.None
        }
    }

    fun saveToHistory(track: TrackUI) {
        saveTrackToHistoryUseCase(track.toDomain())
    }

    fun clearHistory() {
        clearSearchHistoryUseCase()
        _state.value = SearchState.None
    }
}
