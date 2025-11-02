package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SaveTrackToHistoryUseCase {
    operator fun invoke(track: Track)
}
