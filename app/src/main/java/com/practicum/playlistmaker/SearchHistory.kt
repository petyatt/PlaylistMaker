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
        val editor: SharedPreferences.Editor= sharedPreferences.edit()

        editor.putLong(ID, track.id)
        editor.putLong(TRACK_ID, track.trackId)
        editor.putString(COUNTRY, track.country)
        editor.putString(TRACK_NAME, track.trackName)
        editor.putString(RELEASE_DATE, track.releaseDate)
        editor.putString(ARTIST_NAME, track.artistName)
        editor.putLong(TRACK_TIME_MILLIS, track.trackTimeMillis)
        editor.putString(ARTWORK_URL_100, track.getCoverArtwork())
        editor.putString(COLLECTION_NAME, track.collectionName)
        editor.putString(PRIMARY_GENRE_NAME, track.primaryGenreName)
        editor.apply()

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
        const val ID = "id"
        const val TRACK_ID = "trackId"
        const val COUNTRY = "country"
        const val TRACK_NAME = "track_Name"
        const val RELEASE_DATE = "release_Date"
        const val ARTIST_NAME = "artist_Name"
        const val TRACK_TIME_MILLIS = " track_Time_Millis"
        const val ARTWORK_URL_100 = "artwork_Url_100"
        const val COLLECTION_NAME = "collection_Name"
        const val PRIMARY_GENRE_NAME = "primary_Genre_Name"
    }
}