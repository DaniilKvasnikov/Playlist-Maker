package com.example.playlistmaker.playlist.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

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
        setupTracksBottomSheet()
        setupMenuBottomSheet()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(playlistId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTracksBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        tracksBottomSheetBehavior.isHideable = false
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet)
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset
                binding.overlay.visibility = if (slideOffset > 0f) View.VISIBLE else View.GONE
            }
        })

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.menuEdit.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            navigateToEditPlaylist()
        }

        binding.menuDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
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

    private fun setupButtons() {
        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.menuButton.setOnClickListener {
            showMenu()
        }
    }

    private fun sharePlaylist() {
        if (!viewModel.hasTracksToShare()) {
            Toast.makeText(
                requireContext(),
                R.string.share_empty_playlist,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val shareText = viewModel.getShareText() ?: return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun showMenu() {
        val playlist = viewModel.getCurrentPlaylist() ?: return

        binding.menuPlaylistInfo.playlistName.text = playlist.name
        binding.menuPlaylistInfo.playlistTrackCount.text = resources.getQuantityString(
            R.plurals.track_count,
            playlist.trackCount,
            playlist.trackCount
        )

        if (playlist.imagePath != null && File(playlist.imagePath).exists()) {
            Glide.with(this)
                .load(File(playlist.imagePath))
                .transform(CenterCrop(), RoundedCorners(2))
                .into(binding.menuPlaylistInfo.playlistCover)
        } else {
            binding.menuPlaylistInfo.playlistCover.setImageResource(R.drawable.ic_placeholder_45)
        }

        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun navigateToEditPlaylist() {
        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        findNavController().navigate(
            R.id.action_playlistDetails_to_editPlaylist,
            bundleOf(ARG_PLAYLIST_ID to playlistId)
        )
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

    private fun showDeletePlaylistDialog() {
        val playlistName = viewModel.getCurrentPlaylist()?.name ?: return
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertTheme)
            .setMessage("Хотите удалить плейлист «$playlistName»?")
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.deletePlaylist()
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

        viewModel.playlistDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                findNavController().navigateUp()
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

    override fun onResume() {
        super.onResume()
        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
    }
}
