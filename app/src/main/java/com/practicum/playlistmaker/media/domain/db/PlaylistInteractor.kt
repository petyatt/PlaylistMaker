package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun getPlaylistById(playlistId: Long): Playlist?

    suspend fun createNewPlaylist(playlist: Playlist)

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>

    fun getAllTracks(): Flow<List<Track>>

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>>

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long)

    suspend fun deleteTrack(trackId: Long)

    suspend fun isTrackOrphan(trackId: Long): Boolean

    suspend fun updatePlaylist(playlist: Playlist)
}