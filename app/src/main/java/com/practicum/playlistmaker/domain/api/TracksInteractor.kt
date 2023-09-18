package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getTracks(): ArrayList<Track>

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

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }


}