package com.example.playlistmaker.search.data.dto

data class ITunesResponse(
    val resultCount: Int,
    val results: List<TrackDto>
)
