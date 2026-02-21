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
        return playlistDao.getPlaylistWithTracksById(id)?.let { playlistDbConverter.map(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getPlaylistsWithTracks().map { playlistDbConverter.map(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistTrackDao.insertTrack(
            PlaylistTrackEntity(
                playlistId = playlist.id,
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

        val updatedPlaylist = playlist.copy(
            trackCount = playlist.trackCount + 1
        )
        playlistDao.updatePlaylist(playlistDbConverter.map(updatedPlaylist))
    }

    override suspend fun getTracksByIds(trackIds: List<Int>): List<Track> {
        val allTracks = playlistTrackDao.getAllTracks()
        return allTracks
            .filter { it.trackId in trackIds }
            .map { entity ->
                Track(
                    trackId = entity.trackId,
                    trackName = entity.trackName,
                    artistName = entity.artistName,
                    trackTimeMillis = entity.trackTimeMillis,
                    artworkUrl100 = entity.artworkUrl100,
                    collectionName = entity.collectionName,
                    releaseDate = entity.releaseDate,
                    primaryGenreName = entity.primaryGenreName,
                    country = entity.country,
                    previewUrl = entity.previewUrl
                )
            }
            .distinctBy { it.trackId }
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int) {
        playlistTrackDao.deleteTrack(playlistId, trackId)

        val playlist = getPlaylistById(playlistId) ?: return
        val updatedPlaylist = playlist.copy(
            trackCount = maxOf(playlist.trackCount - 1, 0)
        )
        playlistDao.updatePlaylist(playlistDbConverter.map(updatedPlaylist))

        cleanupOrphanedTrack(trackId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlist = getPlaylistById(playlistId) ?: return
        val trackIds = playlist.trackIds

        playlistTrackDao.deleteTracksByPlaylistId(playlistId)
        playlistDao.deletePlaylistById(playlistId)

        for (trackId in trackIds) {
            cleanupOrphanedTrack(trackId)
        }
    }

    private suspend fun cleanupOrphanedTrack(trackId: Int) {
        val allPlaylists = getAllPlaylists()
        val trackInAnyPlaylist = allPlaylists.any { trackId in it.trackIds }
        if (!trackInAnyPlaylist) {
            playlistTrackDao.deleteTrackById(trackId)
        }
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
