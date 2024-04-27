package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun createNewPlaylist(playlist: Playlist) {
        playlistRepository.createNewPlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylist()
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return playlistRepository.getAllTracks()
    }

    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> {
        return playlistRepository.getTracksForPlaylist(playlistId)
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> {
        return playlistRepository.getTracksByIds(trackIds)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistRepository.deletePlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        playlistRepository.deleteTrackFromPlaylist(trackId, playlistId)
    }

    override suspend fun deleteTrack(trackId: Long) {
        playlistRepository.deleteTrack(trackId)
    }

    override suspend fun isTrackOrphan(trackId: Long): Boolean {
        return playlistRepository.isTrackOrphan(trackId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
}