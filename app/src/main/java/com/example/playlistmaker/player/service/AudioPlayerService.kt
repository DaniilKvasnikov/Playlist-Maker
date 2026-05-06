// app/src/main/java/com/example/playlistmaker/player/service/AudioPlayerService.kt
package com.example.playlistmaker.player.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.ui.models.TrackUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.ext.android.inject

class AudioPlayerService : Service(), AudioPlayerController {

    private val playerRepository: PlayerRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playerState = MutableStateFlow<PlayerServiceState>(PlayerServiceState.Idle)
    private var positionJob: Job? = null

    private var track: TrackUI? = null

    private val binder = AudioPlayerBinder()

    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        _playerState.value = PlayerServiceState.Idle
        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_TRACK, TrackUI::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_TRACK)
        }
        playerRepository.preparePlayer(
            url = track?.previewUrl ?: "",
            onPrepared = { _playerState.value = PlayerServiceState.Prepared },
            onCompletion = {
                serviceScope.launch {
                    stopPositionUpdates()
                    hideNotification()
                    _playerState.value = PlayerServiceState.Completed
                }
            }
        )
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopPositionUpdates()
        hideNotification()
        playerRepository.release()
        _playerState.value = PlayerServiceState.Idle
        return super.onUnbind(intent)
    }

    override fun play() {
        playerRepository.play()
        _playerState.value = PlayerServiceState.Playing(playerRepository.getCurrentPosition())
        startPositionUpdates()
    }

    override fun pause() {
        playerRepository.pause()
        stopPositionUpdates()
        _playerState.value = PlayerServiceState.Paused(playerRepository.getCurrentPosition())
    }

    override fun playerState(): StateFlow<PlayerServiceState> = _playerState

    override fun showNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("${track?.artistName} - ${track?.trackName}")
            .setSmallIcon(R.drawable.ic_placeholder_45)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this, NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, 0)
        }
    }

    override fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionJob = serviceScope.launch {
            while (isActive) {
                delay(POSITION_UPDATE_INTERVAL)
                if (playerRepository.isPlaying()) {
                    _playerState.value = PlayerServiceState.Playing(playerRepository.getCurrentPosition())
                } else {
                    break
                }
            }
        }
    }

    private fun stopPositionUpdates() {
        positionJob?.cancel()
        positionJob = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Player",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"
        private const val CHANNEL_ID = "audio_player_channel"
        private const val NOTIFICATION_ID = 1
        private const val POSITION_UPDATE_INTERVAL = 300L
    }
}
