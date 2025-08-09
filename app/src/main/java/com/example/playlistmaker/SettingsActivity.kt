package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

