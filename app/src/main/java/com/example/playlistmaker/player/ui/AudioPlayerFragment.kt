package com.example.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.service.AudioPlayerService
import com.example.playlistmaker.player.ui.playlist.BottomSheetPlaylistAdapter
import com.example.playlistmaker.search.ui.models.TrackUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel by viewModel<AudioPlayerViewModel>()

    private var _binding: ActivityAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: BottomSheetPlaylistAdapter

    private var isServiceBound = false

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {  }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerBinder
            viewModel.onServiceBound(binder.getService())
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.onServiceDisconnected()
        }
    }

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

        setupBottomSheet()
        setupPlaylistRecyclerView()

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.playButton.setOnClickListener {
            viewModel.playPause()
        }

        binding.addToFavoritesButton.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayer_to_createPlaylist)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.setPlaylists(playlists)
        }

        viewModel.addToPlaylistResult.observe(viewLifecycleOwner) { (success, playlistName) ->
            if (success) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    getString(R.string.added_to_playlist, playlistName),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_already_in_playlist, playlistName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val track = AudioPlayerFragmentArgs.fromBundle(requireArguments()).track
        displayTrackInfo(track)
        viewModel.setCurrentTrack(track)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val intent = Intent(requireContext(), AudioPlayerService::class.java).apply {
            putExtra(AudioPlayerService.EXTRA_PREVIEW_URL, track.previewUrl)
            putExtra(AudioPlayerService.EXTRA_ARTIST_NAME, track.artistName)
            putExtra(AudioPlayerService.EXTRA_TRACK_NAME, track.trackName)
        }
        isServiceBound = requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(com.example.playlistmaker.playlist.ui.create.CreatePlaylistFragment.CREATED_PLAYLIST_ID_KEY)
            ?.observe(viewLifecycleOwner) { playlistId ->
                viewModel.addTrackToPlaylistById(playlistId)
                findNavController().currentBackStackEntry?.savedStateHandle
                    ?.remove<Int>(com.example.playlistmaker.playlist.ui.create.CreatePlaylistFragment.CREATED_PLAYLIST_ID_KEY)
            }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onUiVisible()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onUiHidden()
    }

    override fun onDestroyView() {
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
        _binding = null
        super.onDestroyView()
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                }
            }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })
    }

    private fun setupPlaylistRecyclerView() {
        playlistAdapter = BottomSheetPlaylistAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = playlistAdapter
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            R.drawable.ic_like_button_filled_25
        } else {
            R.drawable.ic_like_button_25
        }
        binding.addToFavoritesButton.setImageResource(iconRes)
    }
    private fun render(state: AudioPlayerState) {
        when (state) {
            is AudioPlayerState.Prepared -> {
                binding.playButton.setIsPlaying(false)
                binding.playTime.text = getString(R.string.default_playTime)
            }
            is AudioPlayerState.Playing -> {
                binding.playButton.setIsPlaying(true)
                binding.playTime.text = timeFormat.format(state.position.toLong())
            }
            is AudioPlayerState.Paused -> {
                binding.playButton.setIsPlaying(false)
                binding.playTime.text = timeFormat.format(state.position.toLong())
            }
            is AudioPlayerState.Completed -> {
                binding.playButton.setIsPlaying(false)
                binding.playTime.text = getString(R.string.default_playTime)
            }
        }
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

    companion object {
        private const val ARTWORK_CORNER_RADIUS_DP = 8
    }
}
