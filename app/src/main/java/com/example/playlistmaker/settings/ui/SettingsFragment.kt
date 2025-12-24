package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private var _binding: ActivitySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.buttonEmail.setOnClickListener {
            viewModel.openSupport()
        }

        binding.buttonPracticumOffer.setOnClickListener {
            viewModel.openTerms()
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: SettingsState) {
        when (state) {
            is SettingsState.Loaded -> {
                binding.switchTheme.isChecked = state.settings.isDarkTheme
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
