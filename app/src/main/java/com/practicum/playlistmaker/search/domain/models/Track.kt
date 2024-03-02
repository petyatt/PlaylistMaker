package com.practicum.playlistmaker.search.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,
    val country: String,
    val trackName: String,
    val previewUrl: String,
    val artistName: String,
    val releaseDate: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String,
    var isFavorite: Boolean,
    val addedAt: Long
): Serializable