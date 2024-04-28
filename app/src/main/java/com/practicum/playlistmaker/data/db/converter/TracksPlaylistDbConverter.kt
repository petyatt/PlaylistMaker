package com.practicum.playlistmaker.data.db.converter

import com.practicum.playlistmaker.data.db.entity.TracksPlaylistEntity
import com.practicum.playlistmaker.search.domain.models.Track

class TracksPlaylistDbConverter {

    fun map(track: Track, addedAt: Long): TracksPlaylistEntity {
        return TracksPlaylistEntity(
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

    fun map(tracksPlaylistEntity: TracksPlaylistEntity): Track {
        return Track(
            tracksPlaylistEntity.trackId,
            tracksPlaylistEntity.country,
            tracksPlaylistEntity.trackName,
            tracksPlaylistEntity.previewUrl,
            tracksPlaylistEntity.artistName,
            tracksPlaylistEntity.releaseDate,
            tracksPlaylistEntity.trackTimeMillis,
            tracksPlaylistEntity.artworkUrl100,
            tracksPlaylistEntity.collectionName,
            tracksPlaylistEntity.primaryGenreName,
            tracksPlaylistEntity.isFavorite,
            tracksPlaylistEntity.addedAt
        )
    }
}