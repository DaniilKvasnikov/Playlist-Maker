package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistory(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        HISTORY_PREFERENCES, Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > MAX_HISTORY_SIZE) {
            history.subList(MAX_HISTORY_SIZE, history.size).clear()
        }
        saveHistory(history)
    }
    
    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun clearHistory() {
        sharedPreferences.edit { remove(HISTORY_KEY) }
    }
    
    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit { putString(HISTORY_KEY, json) }
    }
    
    companion object {
        private const val HISTORY_PREFERENCES = "track_history"
        private const val HISTORY_KEY = "history_tracks"
        private const val MAX_HISTORY_SIZE = 10
    }
}