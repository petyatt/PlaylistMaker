package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun saveTrack(track: Track)

    fun getTracks(): ArrayList<Track>

    fun clearHistory()
}