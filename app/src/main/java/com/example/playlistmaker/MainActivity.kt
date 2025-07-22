package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button_search)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            Toast.makeText(this@MainActivity, "Начинаем поиск...", Toast.LENGTH_SHORT).show()
        } }
        button.setOnClickListener(buttonClickListener)
        val library = findViewById<Button>(R.id.button_library)
        library.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываю медиатеку...", Toast.LENGTH_SHORT).show()
        }
        val settings = findViewById<Button>(R.id.button_settings)
        settings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Открываю настройки...", Toast.LENGTH_SHORT).show()
        }
    }
}