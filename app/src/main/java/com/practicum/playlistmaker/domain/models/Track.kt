package com.practicum.playlistmaker.domain.models

data class Track(
    val id: Long,
    val trackId: Long,
    val country: String,
    val trackName: String,
    val previewUrl: String,
    val artistName: String,
    val releaseDate: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String
)