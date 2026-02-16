package com.example.playlistmaker.playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}
