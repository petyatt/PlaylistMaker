package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
): FavoriteTrackInteractor {
    override suspend fun insertFavoriteTrack(track: Track) {
        favoriteTrackRepository.insertFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoriteTrackRepository.deleteFavoriteTrack(track)
    }

    override fun getFavoriteTrackId(trackId: Long): Flow<Boolean> {
        return favoriteTrackRepository.getFavoriteTrackId(trackId)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getAllFavoriteTracks()
    }
}