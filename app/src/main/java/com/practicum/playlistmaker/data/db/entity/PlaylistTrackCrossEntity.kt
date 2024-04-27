package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity

@Entity(tableName = "playlist_track_cross_ref",
    primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossEntity(
    val playlistId: Long,
    val trackId: Long
)