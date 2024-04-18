package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.TracksPlaylistsDao
import com.practicum.playlistmaker.data.db.entity.TracksPlaylistEntity

@Database(version = 1, entities = [TracksPlaylistEntity::class])
abstract class TracksPlaylistsDatabase : RoomDatabase() {

    abstract fun tracksPlaylistsDao(): TracksPlaylistsDao
}