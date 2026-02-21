package com.example.playlistmaker.playlist.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.mappers.toUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistDetailsViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBottomSheet()
        setupRecyclerView()
        observeViewModel()

        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(playlistId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        bottomSheetBehavior.isHideable = false
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(
            onTrackClick = { trackUI ->
                findNavController().navigate(
                    R.id.action_playlistDetails_to_player,
                    bundleOf("track" to trackUI)
                )
            },
            onTrackLongClick = { trackUI ->
                showDeleteTrackDialog(trackUI.trackId)
            }
        )
        binding.tracksRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trackAdapter
        }
    }

    private fun showDeleteTrackDialog(trackId: Int) {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertTheme)
            .setMessage(R.string.delete_track_dialog_message)
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrack(trackId)
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailsState.Loading -> Unit
                is PlaylistDetailsState.Content -> showContent(state)
            }
        }
    }

    private fun showContent(state: PlaylistDetailsState.Content) {
        val playlist = state.playlist

        binding.playlistName.text = playlist.name

        if (playlist.description.isNotEmpty()) {
            binding.playlistDescription.text = playlist.description
            binding.playlistDescription.isVisible = true
        } else {
            binding.playlistDescription.isVisible = false
        }

        binding.totalDuration.text = resources.getQuantityString(
            R.plurals.minutes_count,
            state.totalDurationMinutes.toIntOrNull() ?: 0,
            state.totalDurationMinutes.toIntOrNull() ?: 0
        )
        binding.trackCount.text = resources.getQuantityString(
            R.plurals.track_count,
            playlist.trackCount,
            playlist.trackCount
        )

        if (playlist.imagePath != null && File(playlist.imagePath).exists()) {
            Glide.with(this)
                .load(File(playlist.imagePath))
                .transform(CenterCrop())
                .into(binding.cover)
        } else {
            binding.cover.setImageResource(R.drawable.ic_placeholder_45)
        }

        if (state.tracks.isEmpty()) {
            binding.emptyTracksMessage.isVisible = true
            binding.tracksRecyclerView.isVisible = false
        } else {
            binding.emptyTracksMessage.isVisible = false
            binding.tracksRecyclerView.isVisible = true
            trackAdapter.updateItems(state.tracks.map { it.toUI() })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}
