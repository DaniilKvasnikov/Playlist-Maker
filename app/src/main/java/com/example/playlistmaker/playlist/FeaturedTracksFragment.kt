package com.example.playlistmaker.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFeaturedTracksBinding
import com.example.playlistmaker.playlist.ui.fragments.FeaturedTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeaturedTracksFragment : Fragment() {

    companion object {
        fun newInstance() = FeaturedTracksFragment()
    }
    private lateinit var binding: FragmentFeaturedTracksBinding
    private val viewModel by viewModel<FeaturedTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeaturedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}