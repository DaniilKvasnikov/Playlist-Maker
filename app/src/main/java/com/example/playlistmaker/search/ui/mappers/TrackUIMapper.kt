package com.example.playlistmaker.search.ui.mappers

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TrackUI

fun Track.toUI(): TrackUI {
    return TrackUI(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl
    )
}

fun TrackUI.toDomain(): Track {
    return Track(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl
    )
}

fun List<Track>.toUI(): List<TrackUI> {
    return this.map { it.toUI() }
}

fun List<TrackUI>.toDomain(): List<Track> {
    return this.map { it.toDomain() }
}
