package com.example.playlistmaker.presentation.ui.player

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: AudioPlayerViewModel
    private lateinit var toolbar: MaterialToolbar
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playButton: ImageButton
    private lateinit var addToPlaylistButton: ImageButton
    private lateinit var addToFavoritesButton: ImageButton
    private lateinit var playTime: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var updateTimeRunnable: Runnable? = null
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)

        // Получаем ViewModel из Creator
        viewModel = Creator.provideAudioPlayerViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()

        // Получаем TrackDto из Intent и конвертируем в domain Track
        val trackDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, TrackDto::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        }

        trackDto?.let {
            val track = TrackMapper.mapDtoToDomain(it)
            viewModel.preparePlayer(track)
        }
    }

    private fun setupUI() {
        toolbar = findViewById(R.id.toolbar)
        trackCover = findViewById(R.id.track_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        playButton = findViewById(R.id.play_button)
        addToPlaylistButton = findViewById(R.id.add_to_playlist_button)
        addToFavoritesButton = findViewById(R.id.add_to_favorites_button)
        playTime = findViewById(R.id.play_time)
        durationValue = findViewById(R.id.duration_value)
        albumValue = findViewById(R.id.album_value)
        yearValue = findViewById(R.id.year_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.playPause()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            render(state)
        }

        viewModel.currentPosition.observe(this) { position ->
            playTime.text = timeFormat.format(position.toLong())
        }
    }

    private fun render(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Prepared -> {
                displayTrackInfo(state.track)
                updatePlayButton(false)
                stopUpdatingTime()
                playTime.text = getString(R.string.default_playTime)
            }
            is AudioPlayerState.Playing -> {
                updatePlayButton(true)
                startUpdatingTime()
            }
            is AudioPlayerState.Paused -> {
                updatePlayButton(false)
                stopUpdatingTime()
            }
            is AudioPlayerState.Completed -> {
                updatePlayButton(false)
                stopUpdatingTime()
                playTime.text = getString(R.string.default_playTime)
            }
        }
    }

    private fun displayTrackInfo(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.getFormattedTime()
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        if (!track.collectionName.isNullOrEmpty()) {
            albumValue.text = track.collectionName
        } else {
            albumValue.visibility = TextView.GONE
            findViewById<TextView>(R.id.album_label).visibility = TextView.GONE
        }

        if (!track.releaseDate.isNullOrEmpty()) {
            yearValue.text = formatYear(track.releaseDate)
        } else {
            yearValue.visibility = TextView.GONE
            findViewById<TextView>(R.id.year_label).visibility = TextView.GONE
        }

        val artworkUrl512 = track.getArtworkUrl512()
        Glide.with(this)
            .load(artworkUrl512)
            .apply(RequestOptions().transform(RoundedCorners(8.toPx())))
            .placeholder(R.drawable.ic_placeholder_45)
            .error(R.drawable.ic_placeholder_45)
            .into(trackCover)
    }

    private fun formatYear(releaseDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (_: Exception) {
            releaseDate.substring(0, 4)
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        val iconRes = if (isPlaying) {
            R.drawable.ic_pause_button_84
        } else {
            R.drawable.ic_play_button_100
        }
        playButton.setImageResource(iconRes)
    }

    private fun startUpdatingTime() {
        updateTimeRunnable = object : Runnable {
            override fun run() {
                // Обновляем время через ViewModel
                viewModel.updatePosition()

                // Проверяем, играет ли еще трек
                if (viewModel.isPlaying()) {
                    handler.postDelayed(this, UPDATE_TIME_DELAY_MS)
                }
            }
        }
        handler.post(updateTimeRunnable!!)
    }

    private fun stopUpdatingTime() {
        updateTimeRunnable?.let { handler.removeCallbacks(it) }
        updateTimeRunnable = null
    }

    private fun Int.toPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopUpdatingTime()
    }

    companion object {
        const val TRACK_KEY = "TRACK"
        private const val UPDATE_TIME_DELAY_MS = 500L
    }
}
