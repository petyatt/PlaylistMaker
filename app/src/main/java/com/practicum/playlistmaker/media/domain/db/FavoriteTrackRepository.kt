package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    suspend fun insertFavoriteTrack(track: Track)

    suspend fun deleteFavoriteTrack(track: Track)

    fun getFavoriteTrackId(trackId: Long): Flow<Boolean>

    fun getAllFavoriteTracks(): Flow<List<Track>>
}