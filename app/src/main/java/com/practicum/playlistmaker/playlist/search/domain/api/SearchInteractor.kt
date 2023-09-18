package com.practicum.playlistmaker.playlist.search.domain.api

import Track

interface SearchInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getTracks(): ArrayList<Track>

    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }


}