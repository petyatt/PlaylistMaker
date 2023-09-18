package com.practicum.playlistmaker.data.storage.model

import com.practicum.playlistmaker.data.dto.TrackDto

interface TrackStorage {

    fun save(trackDto: TrackDto)

    fun get(): ArrayList<TrackDto>

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