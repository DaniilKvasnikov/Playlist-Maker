package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.search.ui.models.TrackUI
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private var _binding: ActivityAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        val track = AudioPlayerFragmentArgs.fromBundle(requireArguments()).track
        displayTrackInfo(track)
        viewModel.preparePlayer(track)
    }
    private fun render(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Prepared -> {
                updatePlayButton(isPlaying = false)
                binding.playTime.text = getString(R.string.default_playTime)
            }
            is AudioPlayerState.Playing -> {
                updatePlayButton(isPlaying = true)
                binding.playTime.text = timeFormat.format(state.position.toLong())
            }
            is AudioPlayerState.Paused -> {
                updatePlayButton(isPlaying = false)
                binding.playTime.text = timeFormat.format(state.position.toLong())
            }
            is AudioPlayerState.Completed -> {
                updatePlayButton(isPlaying = false)
                binding.playTime.text = getString(R.string.default_playTime)
            }
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        val iconRes = if (isPlaying) {
            R.drawable.ic_pause_button_84
        } else {
            R.drawable.ic_play_button_100
        }
        binding.playButton.setImageResource(iconRes)
    }

    private fun displayTrackInfo(track: TrackUI) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.getFormattedTime()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        binding.playTime.text = getString(R.string.default_playTime)

        if (!track.collectionName.isNullOrEmpty()) {
            binding.albumValue.text = track.collectionName
        } else {
            binding.albumValue.visibility = TextView.GONE
            binding.albumLabel.visibility = TextView.GONE
        }

        if (!track.releaseDate.isNullOrEmpty()) {
            binding.yearValue.text = formatYear(track.releaseDate)
        } else {
            binding.yearValue.visibility = TextView.GONE
            binding.yearLabel.visibility = TextView.GONE
        }

        val artworkUrl512 = track.getArtworkUrl512()
        Glide.with(requireContext())
            .load(artworkUrl512)
            .apply(RequestOptions().transform(RoundedCorners(ARTWORK_CORNER_RADIUS_DP.toPx())))
            .placeholder(R.drawable.ic_placeholder_45)
            .error(R.drawable.ic_placeholder_45)
            .into(binding.trackCover)
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

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARTWORK_CORNER_RADIUS_DP = 8
    }
}
