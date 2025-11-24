package com.example.playlistmaker.search.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorage(context: Context,
                           private val gson: Gson
) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        HISTORY_PREFERENCES, Context.MODE_PRIVATE
    )

    fun addTrack(track: TrackDto) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }
        saveHistory(history)
    }

    fun getHistory(): List<TrackDto> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<TrackDto>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun clearHistory() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }

    private fun saveHistory(history: List<TrackDto>) {
        val json = gson.toJson(history)
        sharedPreferences.edit { putString(HISTORY_KEY, json) }
    }

    companion object {
        private const val HISTORY_PREFERENCES = "track_history"
        private const val HISTORY_KEY = "history_tracks"
        private const val MAX_HISTORY_SIZE = 10
    }
}
