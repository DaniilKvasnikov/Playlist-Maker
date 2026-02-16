package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.PlaylistDao
import com.example.playlistmaker.playlist.data.db.PlaylistEntity

@Database(version = 2, entities = [TrackEntity::class, PlaylistEntity::class], exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistDao(): PlaylistDao
}
