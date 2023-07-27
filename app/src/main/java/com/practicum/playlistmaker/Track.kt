package com.practicum.playlistmaker

data class Track(
    val id: Long,
    val trackId: Long,
    val country: String,
    val trackName: String,
    val releaseDate: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val primaryGenreName: String

) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}