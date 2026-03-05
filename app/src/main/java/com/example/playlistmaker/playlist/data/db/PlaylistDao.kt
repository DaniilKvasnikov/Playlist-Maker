package com.example.playlistmaker.playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY id DESC")
    suspend fun getPlaylistsWithTracks(): List<PlaylistWithTracks>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistWithTracksById(id: Int): PlaylistWithTracks?

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: Int)
}
