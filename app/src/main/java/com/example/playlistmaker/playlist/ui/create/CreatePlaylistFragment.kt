package com.example.playlistmaker.playlist.ui.create

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.coverImage.setImageURI(uri)
            binding.coverImage.clipToOutline = true
            binding.coverImage2.visibility = View.GONE
            viewModel.setCoverUri(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCoverPicker()
        setupNameWatcher()
        setupCreateButton()
        setupBackNavigation()
        observeViewModel()
    }

    private fun setupCoverPicker() {
        binding.coverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupNameWatcher() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty = !s.isNullOrBlank()
                binding.createButton.isEnabled = isNotEmpty
                if (isNotEmpty) {
                    binding.createButton.setBackgroundColor(requireContext().getColor(R.color.icon_color_search))
                } else {
                    binding.createButton.setBackgroundColor(requireContext().getColor(R.color.item_track_info_play))
                }
                viewModel.name = s?.toString() ?: ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.description = s?.toString() ?: ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCreateButton() {
        binding.createButton.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun setupBackNavigation() {
        binding.toolbar.setNavigationOnClickListener {
            handleBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBack()
                }
            }
        )
    }

    private fun handleBack() {
        if (viewModel.hasUnsavedData()) {
            showConfirmDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext(),R.style.AlertTheme)
            .setTitle(R.string.finish_creating_playlist_title)
            .setMessage(R.string.finish_creating_playlist_message)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finish) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) { name ->
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, name),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
