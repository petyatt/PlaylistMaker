package com.practicum.playlistmaker.data.db.converter

import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

class FavoriteTrackDbConverter {

    fun map(track: Track, addedAt: Long): FavoriteTrackEntity {
        return FavoriteTrackEntity(
            track.trackId,
            track.country,
            track.trackName,
            track.previewUrl,
            track.artistName,
            track.releaseDate,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.primaryGenreName,
            track.isFavorite,
            addedAt
        )
    }

    fun map(trackEntity: FavoriteTrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.country,
            trackEntity.trackName,
            trackEntity.previewUrl,
            trackEntity.artistName,
            trackEntity.releaseDate,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.primaryGenreName,
            trackEntity.isFavorite,
            trackEntity.addedAt
        )
    }

    fun map(trackEntity: List<FavoriteTrackEntity>): List<Track> = trackEntity.map { map(it) }
}