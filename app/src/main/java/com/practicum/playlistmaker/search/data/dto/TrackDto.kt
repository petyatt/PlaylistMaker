package com.practicum.playlistmaker.search.data.dto

data class TrackDto (
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
    val isFavorite: Boolean,
    val addedAt: Long

) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}