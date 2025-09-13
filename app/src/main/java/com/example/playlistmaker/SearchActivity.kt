package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.core.view.marginTop
import androidx.core.view.marginBottom
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils.hideKeyboard
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api by lazy { retrofit.create(ITunesApiService::class.java) }
    private lateinit var search: EditText
    private var stringInput : String = ""
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private val historyData = mutableListOf<Track>()

    private lateinit var emptyView: View
    private lateinit var recycleView: RecyclerView
    private lateinit var errorView: View
    private lateinit var historyContainer: View
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyTitle: View
    private lateinit var clearHistoryButton: Button
    private lateinit var clearHistoryInlineButton: Button
    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        
        searchHistory = SearchHistory(applicationContext)
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
        search = findViewById(R.id.edittext_search)
        btnClear.visibility = View.GONE
        search.doOnTextChanged {s, _, _, _ ->
            btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            stringInput = s.toString()
            
            if (s.isNullOrEmpty()) {
                showHistoryIfNeeded()
            } else {
                showState(State.NONE)
            }
        }

        emptyView = findViewById(R.id.empty_placeholder)
        recycleView = findViewById(R.id.recycler)
        errorView = findViewById(R.id.error_placeholder)
        historyContainer = findViewById(R.id.history_container)
        historyRecycler = findViewById(R.id.history_recycler)
        historyTitle = findViewById(R.id.history_title)
        clearHistoryButton = findViewById(R.id.button_clear_history)
        clearHistoryInlineButton = findViewById(R.id.button_clear_history_inline)
        val retryButton = findViewById<Button>(R.id.button_retry)
        retryButton.setOnClickListener {
            search.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        val data = mutableListOf<Track>()

        recycleView.layoutManager = LinearLayoutManager(this)
        val adapter = TrackAdapter(data) { track ->
            searchHistory.addTrack(track)
            openAudioPlayer(track)
        }
        recycleView.adapter = adapter

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(historyData) { track ->
            searchHistory.addTrack(track)
            openAudioPlayer(track)
        }
        historyRecycler.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistory()
            if (search.text.isEmpty() && search.hasFocus()) {
                showState(State.NONE)
            }
        }

        clearHistoryInlineButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistory()
            if (search.text.isEmpty() && search.hasFocus()) {
                showState(State.NONE)
            }
        }

        search.setOnFocusChangeListener { _, hasFocus ->
            showHistoryIfNeeded()
        }
        search.setOnEditorActionListener { s, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = s.text.toString()

                api.getMusics(query).enqueue(object : Callback<ITunesResponse> {
                    override fun onResponse(call: Call<ITunesResponse>, response: Response<ITunesResponse>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            data.clear()
                            body?.results?.let { data.addAll(it) }
                            adapter.notifyDataSetChanged()
                            showState(if (data.isEmpty()) State.EMPTY else State.CONTENT)
                        } else {
                            showState(State.ERROR)
                        }
                    }

                    override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
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
            showState(State.NONE)
            btnClear.visibility = View.GONE
            showHistoryIfNeeded()
        }
    }
    
    private fun updateHistory() {
        val history = searchHistory.getHistory()
        historyData.clear()
        historyData.addAll(history)
        historyAdapter.notifyDataSetChanged()
        updateButtonVisibility()
    }
    
    private fun updateButtonVisibility() {
        historyContainer.post {
            val containerHeight = historyContainer.height
            val titleHeight = historyTitle.height
            val buttonHeight = clearHistoryInlineButton.height + 
                clearHistoryInlineButton.marginTop + clearHistoryInlineButton.marginBottom
            val recyclerHeight = historyRecycler.height

            val totalContentHeight = titleHeight + recyclerHeight + buttonHeight
            
            if (totalContentHeight <= containerHeight) {
                clearHistoryInlineButton.isVisible = true
                clearHistoryButton.isVisible = false
            } else {
                clearHistoryInlineButton.isVisible = false
                clearHistoryButton.isVisible = true
            }
        }
    }
    
    private fun showHistoryIfNeeded() {
        if (search.text.isEmpty() && search.hasFocus()) {
            updateHistory()
            if (historyData.isNotEmpty()) {
                showState(State.HISTORY)
            }
        }
    }
    
    private fun showState(state: State) {
        recycleView.isVisible = (state == State.CONTENT)
        emptyView.isVisible = (state == State.EMPTY)
        errorView.isVisible = (state == State.ERROR)
        historyContainer.isVisible = (state == State.HISTORY)
    }

    enum class State { CONTENT, EMPTY, ERROR, HISTORY, NONE }
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

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra("TRACK", track)
        startActivity(intent)
    }

    companion object {
        private const val STRING_INPUT = "STRING_INPUT"
    }
}