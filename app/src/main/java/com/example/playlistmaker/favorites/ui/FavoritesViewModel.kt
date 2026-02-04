package com.example.playlistmaker.favorites.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.favorites.domain.api.FavoritesInteractor
import com.example.playlistmaker.search.ui.mappers.toUI
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    fun loadFavorites() {
        _state.value = FavoritesState.Loading
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        _state.postValue(FavoritesState.Empty)
                    } else {
                        _state.postValue(FavoritesState.Content(tracks.toUI()))
                    }
                }
        }
    }
}
