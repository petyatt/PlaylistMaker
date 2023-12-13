package com.practicum.playlistmaker.search.domain.models

import com.practicum.playlistmaker.search.data.dto.TrackDto

interface TrackStorage {

    fun save(trackDto: TrackDto)

    fun get(): ArrayList<TrackDto>

    fun clearHistory()
}