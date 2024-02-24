package com.practicum.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.data.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(tracksEntity: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_track_table ORDER BY addedAt DESC")
    fun getAllFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT * FROM favorite_track_table WHERE trackId=:trackId")
    suspend fun getFavoriteTrackById(trackId: Long): FavoriteTrackEntity?

    @Delete
    suspend fun deleteFavoriteTrack(trackEntity: FavoriteTrackEntity)
}