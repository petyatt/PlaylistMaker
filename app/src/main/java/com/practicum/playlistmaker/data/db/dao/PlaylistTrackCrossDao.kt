package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackCrossEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackCrossDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlistTrackCrossRef: PlaylistTrackCrossEntity)

    @Query("SELECT t.* FROM tracks_playlist_table AS t\n" +
            "INNER JOIN playlist_track_cross_ref AS ptc ON t.trackId = ptc.trackId\n" +
            "WHERE ptc.playlistId = :playlistId")
    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    @Query("DELETE FROM playlist_track_cross_ref WHERE trackId = :trackId AND playlistId = :playlistId")
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)

    @Query("DELETE FROM playlist_track_cross_ref WHERE playlistId = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE trackId = :trackId")
    fun getCountOfPlaylistsContainingTrack(trackId: Long): Flow<Int>
}