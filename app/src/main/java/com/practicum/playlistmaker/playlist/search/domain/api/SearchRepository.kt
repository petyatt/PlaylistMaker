package com.practicum.playlistmaker.playlist.search.domain.api

import Track
import com.practicum.playlistmaker.playlist.util.Resource

interface SearchRepository {

    fun searchTracks(expression: String): Resource<List<Track>>

    fun saveTrack(track: Track)

    fun getTracks(): ArrayList<Track>

    fun clearHistory()
}