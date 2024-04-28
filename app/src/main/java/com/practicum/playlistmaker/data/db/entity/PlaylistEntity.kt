package com.practicum.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,
    val title: String,
    val description: String,
    val imagePath: String?,
    val trackCount: Int
)