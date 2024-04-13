package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createNewPlaylist(playlist: Playlist)

    suspend fun addTrackInPlaylist(track: Track, playlist: Playlist)

    fun getAllPlaylist(): Flow<List<Playlist>>
}