package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.panelHeader.setOnClickListener {
            finish()
        }
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
        viewModel.state.observe(this) { state ->
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
}
