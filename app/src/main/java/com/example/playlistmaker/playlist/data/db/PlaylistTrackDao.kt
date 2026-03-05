package com.example.playlistmaker.playlist.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks")
    suspend fun getAllTracks(): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrack(playlistId: Int, trackId: Int)

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun deleteTracksByPlaylistId(playlistId: Int)
}
