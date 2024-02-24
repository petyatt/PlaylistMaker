package com.practicum.playlistmaker.search.data.localstorage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.TrackStorage

class LocalTrackStorage(
    private val sharedPreferences: SharedPreferences,
    private var gson: Gson
): TrackStorage {


    companion object {
        const val TRACK_SHARED_PREFERENCES = "trackSharedPreferences"
        const val SAVE_HISTORY = "save_history"
        const val KEY_SEARCH_HISTORY = "key_search_history"
        const val LIMITATIONS_HISTORY_TRACKS = 10
        const val TRACK_ID = "trackId"
        const val COUNTRY = "country"
        const val TRACK_NAME = "track_Name"
        const val RELEASE_DATE = "release_Date"
        const val ARTIST_NAME = "artist_Name"
        const val TRACK_TIME_MILLIS = " track_Time_Millis"
        const val ARTWORK_URL_100 = "artwork_Url_100"
        const val COLLECTION_NAME = "collection_Name"
        const val PRIMARY_GENRE_NAME = "primary_Genre_Name"
        const val PREVIEW_URL = "previewUrl"
    }

    override fun save(trackDto: TrackDto) {
        sharedPreferences.edit().apply {
            putString(PREVIEW_URL, trackDto.previewUrl)
            putLong(TRACK_ID, trackDto.trackId)
            putString(COUNTRY, trackDto.country)
            putString(TRACK_NAME, trackDto.trackName)
            putString(RELEASE_DATE, trackDto.releaseDate)
            putString(ARTIST_NAME, trackDto.artistName)
            putLong(TRACK_TIME_MILLIS, trackDto.trackTimeMillis)
            putString(ARTWORK_URL_100, trackDto.getCoverArtwork())
            putString(COLLECTION_NAME, trackDto.collectionName)
            putString(PRIMARY_GENRE_NAME, trackDto.primaryGenreName)
            apply()
        }

        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        val currentHistory: MutableList<TrackDto> =
            gson.fromJson(json, object : TypeToken<MutableList<TrackDto>>() {}.type) ?: mutableListOf()

        currentHistory.removeAll { it.trackId == trackDto.trackId }

        currentHistory.add(0, trackDto)

        while (currentHistory.size > LIMITATIONS_HISTORY_TRACKS) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        val jsonHistory = gson.toJson(currentHistory)
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, jsonHistory).apply()
    }

    override fun get(): ArrayList<TrackDto> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY, null)
        return gson.fromJson(json, object : TypeToken<ArrayList<TrackDto>>() {}.type) ?: arrayListOf()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY)
            .apply()
    }


}