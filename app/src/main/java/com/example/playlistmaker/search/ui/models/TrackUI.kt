package com.example.playlistmaker.search.ui.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class TrackUI(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val isFavorite: Boolean = false
) : Parcelable {

    fun getFormattedTime(): String {
        if (trackTimeMillis <= 0L) return ""
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.Companion.format(Locale.US, "%d:%02d", minutes, seconds)
    }

    fun getArtworkUrl512(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}