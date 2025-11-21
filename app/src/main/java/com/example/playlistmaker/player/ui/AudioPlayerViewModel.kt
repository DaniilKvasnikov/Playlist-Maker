package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.GetCurrentPositionUseCase
import com.example.playlistmaker.player.domain.api.IsPlayingUseCase
import com.example.playlistmaker.player.domain.api.PauseUseCase
import com.example.playlistmaker.player.domain.api.PlayUseCase
import com.example.playlistmaker.player.domain.api.PreparePlayerUseCase
import com.example.playlistmaker.player.domain.api.ReleasePlayerUseCase
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val preparePlayerUseCase: PreparePlayerUseCase,
    private val playUseCase: PlayUseCase,
    private val pauseUseCase: PauseUseCase,
    private val releasePlayerUseCase: ReleasePlayerUseCase,
    private val getCurrentPositionUseCase: GetCurrentPositionUseCase,
    private val isPlayingUseCase: IsPlayingUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private var currentTrack: Track? = null
    private var updateJob: Job? = null

    fun preparePlayer(track: Track) {
        currentTrack = track
        preparePlayerUseCase(
            url = track.previewUrl,
            onPrepared = {
                _state.value = AudioPlayerState.Prepared(track)
            },
            onCompletion = {
                onCompletion()
            }
        )
    }

    fun playPause() {
        val track = currentTrack ?: return
        when (val currentState = _state.value) {
            is AudioPlayerState.Prepared, is AudioPlayerState.Paused, is AudioPlayerState.Completed -> {
                playUseCase()
                _state.value = AudioPlayerState.Playing(track, 0)
                startUpdatingPosition()
            }
            is AudioPlayerState.Playing -> {
                pauseUseCase()
                stopUpdatingPosition()
                _state.value = AudioPlayerState.Paused(track, currentState.position)
            }
            else -> {}
        }
    }

    fun pause() {
        val track = currentTrack ?: return
        pauseUseCase()
        stopUpdatingPosition()
        _state.value = AudioPlayerState.Paused(track, 0)
    }

    fun onCompletion() {
        val track = currentTrack ?: return
        stopUpdatingPosition()
        _state.value = AudioPlayerState.Completed(track)
    }

    private fun startUpdatingPosition() {
        updateJob = viewModelScope.launch {
            val track = currentTrack ?: return@launch
            while (isActive) {
                delay(UPDATE_INTERVAL)
                if (isPlayingUseCase.execute()) {
                    val position = getCurrentPositionUseCase.execute()
                    _state.value = AudioPlayerState.Playing(track, position)
                } else {
                    break
                }
            }
        }
    }

    private fun stopUpdatingPosition() {
        updateJob?.cancel()
        updateJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUpdatingPosition()
        releasePlayerUseCase()
    }

    companion object {
        private const val UPDATE_INTERVAL = 500L
    }
}
