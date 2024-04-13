package com.practicum.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.data.db.entity.PlaylistEntity

@Database(version = 1, entities = [PlaylistEntity:: class])
abstract class PlaylistDatabase: RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
}