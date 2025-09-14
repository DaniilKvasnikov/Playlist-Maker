package com.example.playlistmaker

import android.os.Bundle
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
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        val track = intent.getParcelableExtra<Track>(TRACK_KEY)
        track?.let { displayTrackInfo(it) }
    }

    private fun displayTrackInfo(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.trackTime
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country
        playTime.text = getString(R.string.default_playTime)

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

        val artworkUrl512 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
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

    private fun Int.toPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    companion object {
        const val TRACK_KEY = "TRACK"
    }
}