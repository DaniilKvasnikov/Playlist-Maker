package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils.hideKeyboard

class SearchActivity : AppCompatActivity() {
    private lateinit var search: EditText
    private var stringInput : String = ""
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnBack = findViewById<MaterialToolbar>(R.id.panel_header)
        btnBack.setOnClickListener {
            finish()
        }
        val btnClear = findViewById<ImageButton>(R.id.button_clear)
        search = findViewById<EditText>(R.id.edittext_serach)
        btnClear.setOnClickListener {
            search.setText("")
            hideKeyboard(search)
        }
        btnClear.visibility = View.GONE
        search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                stringInput = s.toString()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

        }
        )
    }

    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_INPUT, stringInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringInput = savedInstanceState.getString(STRING_INPUT, "")
        search.setText(stringInput)
    }

    companion object {
        private const val STRING_INPUT = "STRING_INPUT"
    }
}