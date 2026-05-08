package com.example.playlistmaker.playlist.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R

class MediaLibraryViewModel : ViewModel() {
    val tabTitleRes: List<Int> = listOf(R.string.featured_tracks, R.string.playlists)
}
