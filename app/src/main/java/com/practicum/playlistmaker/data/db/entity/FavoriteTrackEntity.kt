package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track_table")
data class FavoriteTrackEntity(
    @PrimaryKey
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
)