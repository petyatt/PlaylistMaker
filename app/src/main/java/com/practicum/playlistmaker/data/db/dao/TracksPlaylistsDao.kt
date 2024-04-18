package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TracksPlaylistEntity

@Dao
interface TracksPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackInPlaylist(tracksPlaylistEntity: TracksPlaylistEntity)
}