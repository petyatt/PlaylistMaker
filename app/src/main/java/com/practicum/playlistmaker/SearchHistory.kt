package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun clearSearchHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY)
            .apply()
    }

    fun readSearchHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        val jsonG = GsonBuilder().create()
        return jsonG.fromJson(json, object: TypeToken<MutableList<Track>>() {} .type) ?: mutableListOf()
    }

    fun writeSearchHistory(track: Track) {
        val editor = sharedPreferences.edit()

        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        val gson = Gson()
        val currentHistory: MutableList<Track> =
            gson.fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf()

        currentHistory.removeAll { it.trackId == track.trackId }

        currentHistory.add(0, track)

        while (currentHistory.size > LIMITATIONS_HISTORY_TRACKS) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        val jsonHistory = gson.toJson(currentHistory)
        editor.putString(KEY_SEARCH_HISTORY, jsonHistory)
        editor.apply()
    }

    companion object {
        const val KEY_SEARCH_HISTORY = "key_search_history"
        const val LIMITATIONS_HISTORY_TRACKS = 10
    }
}