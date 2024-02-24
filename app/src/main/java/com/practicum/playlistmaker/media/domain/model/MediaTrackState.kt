package com.practicum.playlistmaker.media.domain.model

import com.practicum.playlistmaker.search.domain.models.Track

interface MediaTrackState {

    object Empty : MediaTrackState

    data class Content(val data: List<Track>) : MediaTrackState

}