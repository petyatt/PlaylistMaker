package com.practicum.playlistmaker.data.dto

data class TrackDto (
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

) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}