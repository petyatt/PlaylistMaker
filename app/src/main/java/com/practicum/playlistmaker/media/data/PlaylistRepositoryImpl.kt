package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.data.db.PlaylistDatabase
import com.practicum.playlistmaker.data.db.TracksPlaylistsDatabase
import com.practicum.playlistmaker.data.db.converter.PlaylistDbConverter
import com.practicum.playlistmaker.data.db.converter.TracksPlaylistDbConverter
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val tracksPlaylistsDatabase: TracksPlaylistsDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val tracksPlaylistDbConverter: TracksPlaylistDbConverter
): PlaylistRepository {
    override suspend fun createNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDatabase.playlistDao().createNewPlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val trackPlaylistEntity = tracksPlaylistDbConverter
            .map(track, System.currentTimeMillis())
        tracksPlaylistsDatabase.tracksPlaylistsDao().addTrackInPlaylist(trackPlaylistEntity)

        val updatedPlaylist = playlist.copy(trackCount = playlist.trackCount + 1)
        val playlistEntity = playlistDbConverter
            .map(updatedPlaylist)
        playlistDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistDatabase.playlistDao().getAllPlaylist()
            .map { playlistTrackEntity ->
                playlistDbConverter.map(playlistTrackEntity)
            }
    }
}