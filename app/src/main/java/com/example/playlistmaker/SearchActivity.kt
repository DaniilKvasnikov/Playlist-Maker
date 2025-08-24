package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils.hideKeyboard
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .build()
    private val api by lazy { retrofit.create(ITunesApiService::class.java) }
    private lateinit var search: EditText
    private var stringInput : String = ""
    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
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
        search = findViewById(R.id.edittext_serach)
        btnClear.visibility = View.GONE
        search.doOnTextChanged {s, _, _, _ ->
            btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            stringInput = s.toString()
        }
        val emptyView = findViewById<View>(R.id.empty_placeholder)
        val recycleView = findViewById<RecyclerView>(R.id.recycler)
        val errorView = findViewById<View>(R.id.error_placeholder)
        val retryButton = findViewById<Button>(R.id.button_retry)
        retryButton.setOnClickListener {
            search.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
        fun showState(state: State) {
            when (state) {
                State.CONTENT -> {
                    recycleView.isVisible = true
                    emptyView.isVisible = false
                    errorView.isVisible = false
                }
                State.EMPTY -> {
                    recycleView.isVisible = false
                    emptyView.isVisible = true
                    errorView.isVisible = false
                }
                State.ERROR -> {
                    recycleView.isVisible = false
                    emptyView.isVisible = false
                    errorView.isVisible = true
                }
            }
        }
        val data = mutableListOf<Track>()

        recycleView.layoutManager = LinearLayoutManager(this)
        val adapter = TrackAdapter(data)
        recycleView.adapter = adapter
        search.setOnEditorActionListener { s, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = s.text.toString()

                api.getMusics(query).enqueue(object : retrofit2.Callback<ResponseBody> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        if (response.isSuccessful && response.body() != null) {
                            val json = org.json.JSONObject(response.body()!!.string())
                            val results = json.getJSONArray("results")
                            data.clear()
                            for (i in 0 until results.length()) {
                                val item = results.getJSONObject(i)
                                val trackName = item.optString("trackName")
                                val artistName = item.optString("artistName")
                                val duration = formatMillis(item.optLong("trackTimeMillis"))
                                val artwork = item.optString("artworkUrl100")
                                data.add(Track(trackName, artistName, duration, artwork))
                            }
                            adapter.notifyDataSetChanged()
                            showState(if (data.isEmpty()) State.EMPTY else State.CONTENT)
                        } else {
                            showState(State.ERROR)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        showState(State.ERROR)
                    }
                })
                true
            }
            false
        }
        btnClear.setOnClickListener {
            search.setText("")
            hideKeyboard(search)
            data.clear()
            adapter.notifyDataSetChanged()
            recycleView.isVisible = false
            emptyView.isVisible = false
            errorView.isVisible = false
            btnClear.visibility = View.GONE
        }
    }

    enum class State { CONTENT, EMPTY, ERROR }
    private fun formatMillis(ms: Long): String {
        if (ms <= 0L) return ""
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
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