package com.practicum.playlistmaker

data class Track(
    val id: Long,
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)