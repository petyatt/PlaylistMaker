package com.practicum.playlistmaker.data.db.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.model.Playlist

class PlaylistDbConverter(
    private val gson: Gson
) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackList = gson.toJson(playlist.trackList),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            title = playlistEntity.title,
            description = playlistEntity.description,
            imagePath = playlistEntity.imagePath,
            trackList = gson.fromJson(playlistEntity.trackList, object : TypeToken<List<Long>>() {}.type),
            trackCount = playlistEntity.trackCount
        )
    }

    fun map(playlistEntity: List<PlaylistEntity>): List<Playlist> = playlistEntity.map { map(it) }
}