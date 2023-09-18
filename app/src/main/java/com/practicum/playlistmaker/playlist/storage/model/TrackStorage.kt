package com.practicum.playlistmaker.playlist.storage.model

import com.practicum.playlistmaker.playlist.search.data.dto.TrackDto

interface TrackStorage {

    fun save(trackDto: TrackDto)

    fun get(): ArrayList<TrackDto>

    fun clearHistory()
}