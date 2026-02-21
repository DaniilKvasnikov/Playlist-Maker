package com.example.playlistmaker.playlist.ui.edit

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.playlist.ui.create.CreatePlaylistFragment
import com.example.playlistmaker.playlist.ui.details.PlaylistDetailsFragment.Companion.ARG_PLAYLIST_ID
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = getString(R.string.edit_playlist)
        binding.createButton.text = getString(R.string.save)

        val playlistId = arguments?.getInt(ARG_PLAYLIST_ID) ?: return
        viewModel.loadPlaylist(playlistId)

        viewModel.playlistLoaded.observe(viewLifecycleOwner) { playlist ->
            binding.nameEditText.setText(playlist.name)
            binding.descriptionEditText.setText(playlist.description)
            if (playlist.imagePath != null && File(playlist.imagePath).exists()) {
                Glide.with(this)
                    .load(File(playlist.imagePath))
                    .centerCrop()
                    .into(binding.coverImage)
                binding.coverImage.clipToOutline = true
                binding.coverImage2.visibility = View.GONE
            }
        }

        binding.createButton.setOnClickListener {
            viewModel.savePlaylist()
        }

        viewModel.playlistUpdated.observe(viewLifecycleOwner) { updated ->
            if (updated) {
                findNavController().navigateUp()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }
}
