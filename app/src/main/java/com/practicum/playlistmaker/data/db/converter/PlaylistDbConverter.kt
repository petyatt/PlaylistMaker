package com.practicum.playlistmaker.data.db.converter

import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            title = playlistEntity.title,
            description = playlistEntity.description,
            imagePath = playlistEntity.imagePath,
            trackCount = playlistEntity.trackCount
        )
    }

    fun map(playlistEntity: List<PlaylistEntity>): List<Playlist> = playlistEntity.map { map(it) }
}