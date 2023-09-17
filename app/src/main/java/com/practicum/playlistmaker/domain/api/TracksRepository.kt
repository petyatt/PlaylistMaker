package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {

    fun searchTracks(expression: String): List<Track>

    fun saveTrack(track: Track)

    fun getTracks(): ArrayList<Track>

    fun darkMode(darkTheme: Boolean)

    fun switchTheme(darkThemeEnabled: Boolean)

    fun clearHistory()

    fun previewUrl(): String

    fun imageUrl(): String

    fun collectionName(): String

    fun primaryGenreName(): String

    fun releaseDate(): String

    fun country(): String

    fun trackTimeMillis(): Long

    fun trackName(): String

    fun artistName(): String

}