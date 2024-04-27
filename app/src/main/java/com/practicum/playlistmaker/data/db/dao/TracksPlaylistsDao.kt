package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.TracksPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackInPlaylist(tracksPlaylistEntity: TracksPlaylistEntity)

    @Query("SELECT * FROM tracks_playlist_table ORDER BY addedAt DESC")
    fun getAllTracks(): Flow<List<TracksPlaylistEntity>>

    @Query("SELECT * FROM tracks_playlist_table WHERE trackId IN (:trackIds)")
    fun loadAllTracksByIds(trackIds: List<Long>): Flow<List<Track>>

    @Query("DELETE FROM tracks_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}