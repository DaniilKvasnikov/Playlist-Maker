package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.util.Locale

data class Track(
    @SerializedName("trackId")
    val trackId: Int,
    @SerializedName("trackName")
    val trackName: String,
    @SerializedName("artistName")
    val artistName: String,
    @SerializedName("trackTimeMillis")
    private val trackTimeMillis: Long,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String,
    @SerializedName("collectionName")
    val collectionName: String?,
    @SerializedName("releaseDate")
    val releaseDate: String?,
    @SerializedName("primaryGenreName")
    val primaryGenreName: String,
    @SerializedName("country")
    val country: String
) {
    val trackTime: String
        get() = formatMillis(trackTimeMillis)

    private fun formatMillis(ms: Long): String {
        if (ms <= 0L) return ""
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}