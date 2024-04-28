package com.practicum.playlistmaker.media.data

import androidx.room.Transaction
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.converter.PlaylistDbConverter
import com.practicum.playlistmaker.data.db.converter.TracksPlaylistDbConverter
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackCrossEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val tracksPlaylistDbConverter: TracksPlaylistDbConverter,
): PlaylistRepository {
    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId)
        return playlistEntity?.let { playlistDbConverter.map(it) }
    }

    override suspend fun createNewPlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistEntity?.let { appDatabase.playlistDao().createNewPlaylist(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val trackPlaylistEntity = tracksPlaylistDbConverter.map(track, System.currentTimeMillis())
        appDatabase.tracksPlaylistsDao().addTrackInPlaylist(trackPlaylistEntity)
        appDatabase.playlistTrackCrossDao().insert(PlaylistTrackCrossEntity(playlist.playlistId, track.trackId, System.currentTimeMillis()))

        val updatedPlaylist = playlist.copy(trackCount = playlist.trackCount + 1)
        val playlistEntity = playlistDbConverter.map(updatedPlaylist)
        playlistEntity.let {
            appDatabase.playlistDao().updatePlaylist(it)
        }
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAllPlaylist()
            .map { list -> list.map { playlistDbConverter.map(it) } }
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return appDatabase.tracksPlaylistsDao().getAllTracks()
            .map { list -> list.map { tracksPlaylistDbConverter.map(it) } }
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return appDatabase.playlistTrackCrossDao().getTracksForPlaylist(playlistId)
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> {
        return appDatabase.tracksPlaylistsDao().loadAllTracksByIds(trackIds)
    }

    @Transaction
    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        appDatabase.playlistTrackCrossDao().deleteTrackFromPlaylist(trackId, playlistId)
        if (isTrackOrphan(trackId)) {
            appDatabase.tracksPlaylistsDao().deleteTrackById(trackId)
        }

        val currentPlaylist = appDatabase.playlistDao().getPlaylistById(playlistId)
        if (currentPlaylist != null) {
            val updatedTrackCount = currentPlaylist.trackCount - 1
            val updatedPlaylist = currentPlaylist.copy(trackCount = updatedTrackCount)
            appDatabase.playlistDao().updatePlaylist(updatedPlaylist)
        }
    }

    @Transaction
    override suspend fun deletePlaylist(playlistId: Long) {
        appDatabase.playlistTrackCrossDao().deletePlaylistById(playlistId)
        appDatabase.playlistDao().deletePlaylistById(playlistId)
        deleteOrphanTracksAfterPlaylistDeletion()
    }

    override suspend fun deleteTrack(trackId: Long) {
        if (isTrackOrphan(trackId)) {
            appDatabase.tracksPlaylistsDao().deleteTrackById(trackId)
        }
    }

    override suspend fun isTrackOrphan(trackId: Long): Boolean {
        val playlistsContainingTrack = appDatabase
            .playlistTrackCrossDao()
            .getCountOfPlaylistsContainingTrack(trackId)
            .first()
        return playlistsContainingTrack == 0
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    private suspend fun deleteOrphanTracksAfterPlaylistDeletion() {
        val tracks = appDatabase.tracksPlaylistsDao().getAllTracks().firstOrNull()
        tracks?.forEach { track ->
            val trackId = track.trackId
            if (isTrackOrphan(trackId)) {
                deleteTrack(trackId)
            }
        }
    }
}