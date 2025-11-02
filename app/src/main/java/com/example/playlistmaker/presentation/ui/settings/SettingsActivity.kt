package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.app.App
import com.example.playlistmaker.di.Creator
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        // Получаем ViewModel из Creator
        viewModel = Creator.provideSettingsViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()

        // Загружаем настройки
        viewModel.loadSettings()
    }

    private fun setupUI() {
        val share = findViewById<Button>(R.id.button_share)
        share.setOnClickListener {
            shareApp()
        }

        val email = findViewById<Button>(R.id.button_email)
        email.setOnClickListener {
            sendEmail()
        }

        val docs = findViewById<Button>(R.id.button_practicum_offer)
        docs.setOnClickListener {
            practicumOffer()
        }

        val close = findViewById<MaterialToolbar>(R.id.panel_header)
        close.setOnClickListener {
            finish()
        }

        themeSwitcher = findViewById(R.id.switch_theme)
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
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
                themeSwitcher.isChecked = state.settings.isDarkTheme
            }
            is SettingsState.ThemeChanged -> {
                // Применяем тему через App
                (applicationContext as App).switchTheme(state.isDark)
            }
        }
    }

    private fun shareApp() {
        val text = getString(R.string.message_share_text)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun sendEmail() {
        val email = getString(R.string.email_address)
        val subject = getString(R.string.email_theme)
        val body = getString(R.string.email_text)
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(intent)
    }

    private fun practicumOffer() {
        val url = getString(R.string.practicum_offer)
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }
}
