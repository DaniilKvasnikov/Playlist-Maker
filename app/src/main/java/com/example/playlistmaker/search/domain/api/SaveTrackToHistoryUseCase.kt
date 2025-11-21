package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SaveTrackToHistoryUseCase {
    operator fun invoke(track: Track)
}
