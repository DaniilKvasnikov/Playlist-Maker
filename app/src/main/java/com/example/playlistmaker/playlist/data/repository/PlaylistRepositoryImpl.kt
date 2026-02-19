package com.example.playlistmaker.playlist.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.playlist.data.db.PlaylistDao
import com.example.playlistmaker.playlist.data.db.PlaylistTrackDao
import com.example.playlistmaker.playlist.data.db.PlaylistTrackEntity
import com.example.playlistmaker.playlist.data.mapper.PlaylistDbConverter
import com.example.playlistmaker.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.playlist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val context: Context
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return playlistDao.getPlaylistById(id)?.let { playlistDbConverter.map(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists().map { playlistDbConverter.map(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val updatedTrackIds = playlist.trackIds + track.trackId
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            trackCount = updatedTrackIds.size
        )
        playlistDao.updatePlaylist(playlistDbConverter.map(updatedPlaylist))

        playlistTrackDao.insertTrack(
            PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                addedTimestamp = System.currentTimeMillis()
            )
        )
    }

    override fun saveImageToStorage(uri: Uri): String {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        inputStream?.close()
        outputStream.close()
        return file.absolutePath
    }
}
