package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.dao.PlaylistTrackCrossDao
import com.practicum.playlistmaker.data.db.dao.TracksPlaylistsDao
import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.data.db.entity.PlaylistTrackCrossEntity
import com.practicum.playlistmaker.data.db.entity.TracksPlaylistEntity

@Database(version = 1, entities = [
    FavoriteTrackEntity::class,
    PlaylistEntity::class,
    PlaylistTrackCrossEntity::class,
    TracksPlaylistEntity::class
])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackCrossDao(): PlaylistTrackCrossDao
    abstract fun tracksPlaylistsDao(): TracksPlaylistsDao
}