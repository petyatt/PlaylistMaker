package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.converter.FavoriteTrackDbConverter
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: FavoriteTrackDbConverter,
): FavoriteTrackRepository {
    override suspend fun insertFavoriteTrack(track: Track) {
        track.isFavorite = true
        appDatabase.favoriteTrackDao().insertFavoriteTrack(trackDbConverter.map(track, System.currentTimeMillis()))
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        track.isFavorite = false
        appDatabase.favoriteTrackDao().deleteFavoriteTrack(trackDbConverter.map(track, System.currentTimeMillis()))
    }

    override fun getFavoriteTrackId(trackId: Long): Flow<Boolean> = flow {
        emit(appDatabase.favoriteTrackDao().getFavoriteTrackById(trackId) != null)
    }

    override fun getAllFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTrackDao().getAllFavoriteTracks()
            .map { favoriteTrackEntityList ->
                trackDbConverter.map(favoriteTrackEntityList)
            }
    }
}