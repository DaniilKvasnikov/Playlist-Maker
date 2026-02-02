package com.example.playlistmaker.playlist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFeaturedTracksBinding
import com.example.playlistmaker.favorites.ui.FavoritesState
import com.example.playlistmaker.favorites.ui.FavoritesViewModel
import com.example.playlistmaker.playlist.ui.MediaLibraryFragmentDirections
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.models.TrackUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeaturedTracksFragment : Fragment() {

    private var _binding: FragmentFeaturedTracksBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()
    private var adapter: TrackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeaturedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter { track ->
            navigateToPlayer(track)
        }
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Loading -> showLoading()
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvFavorites.visibility = View.GONE
        binding.placeholderContainer.visibility = View.GONE
    }

    private fun showContent(tracks: List<TrackUI>) {
        binding.progressBar.visibility = View.GONE
        binding.rvFavorites.visibility = View.VISIBLE
        binding.placeholderContainer.visibility = View.GONE
        adapter?.updateItems(tracks)
    }

    private fun showEmpty() {
        binding.progressBar.visibility = View.GONE
        binding.rvFavorites.visibility = View.GONE
        binding.placeholderContainer.visibility = View.VISIBLE
    }

    private fun navigateToPlayer(track: TrackUI) {
        val action = MediaLibraryFragmentDirections.actionMediaLibraryToPlayer(track)
        requireParentFragment().findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFavorites.adapter = null
        adapter = null
        _binding = null
    }

    companion object {
        fun newInstance() = FeaturedTracksFragment()
    }
}