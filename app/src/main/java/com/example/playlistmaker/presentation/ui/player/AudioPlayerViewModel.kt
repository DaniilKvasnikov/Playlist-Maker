package com.example.playlistmaker.presentation.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.*
import com.example.playlistmaker.domain.models.Track

class AudioPlayerViewModel(
    private val preparePlayerUseCase: PreparePlayerUseCase,
    private val playUseCase: PlayUseCase,
    private val pauseUseCase: PauseUseCase,
    private val releasePlayerUseCase: ReleasePlayerUseCase,
    private val getPlayerPositionUseCase: GetPlayerPositionUseCase,
    private val isPlayerPlayingUseCase: IsPlayerPlayingUseCase,
    private val setPlayerCompletionListenerUseCase: SetPlayerCompletionListenerUseCase
) : ViewModel() {

    private val _state = MutableLiveData<AudioPlayerState>()
    val state: LiveData<AudioPlayerState> = _state

    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition

    private var currentTrack: Track? = null

    fun preparePlayer(track: Track) {
        currentTrack = track
        preparePlayerUseCase(track.previewUrl)
        _state.value = AudioPlayerState.Prepared(track)
    }

    fun playPause() {
        val track = currentTrack ?: return
        when (val currentState = _state.value) {
            is AudioPlayerState.Prepared, is AudioPlayerState.Paused, is AudioPlayerState.Completed -> {
                playUseCase()
                _state.value = AudioPlayerState.Playing(track)
                setPlayerCompletionListenerUseCase {
                    _state.value = AudioPlayerState.Completed(track)
                }
            }
            is AudioPlayerState.Playing -> {
                pauseUseCase()
                val position = getPlayerPositionUseCase()
                _state.value = AudioPlayerState.Paused(track, position)
            }
            else -> {}
        }
    }

    fun pause() {
        val track = currentTrack ?: return
        if (isPlayerPlayingUseCase()) {
            pauseUseCase()
            val position = getPlayerPositionUseCase()
            _state.value = AudioPlayerState.Paused(track, position)
        }
    }

    fun updatePosition() {
        _currentPosition.value = getPlayerPositionUseCase()
    }

    fun isPlaying(): Boolean {
        return isPlayerPlayingUseCase()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayerUseCase()
    }
}
