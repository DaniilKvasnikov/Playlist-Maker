package com.example.playlistmaker.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
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
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.player.AudioPlayerActivity
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var search: EditText
    private var stringInput: String = ""
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val searchData = mutableListOf<Track>()
    private val historyData = mutableListOf<Track>()

    private lateinit var emptyView: View
    private lateinit var recycleView: RecyclerView
    private lateinit var errorView: View
    private lateinit var historyContainer: View
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyTitle: View
    private lateinit var clearHistoryButton: Button
    private lateinit var clearHistoryInlineButton: Button
    private lateinit var progressBar: ProgressBar

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastClickTime = 0L

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Получаем ViewModel из Creator
        viewModel = Creator.provideSearchViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val btnBack = findViewById<MaterialToolbar>(R.id.panel_header)
        btnBack.setOnClickListener {
            finish()
        }

        val btnClear = findViewById<ImageButton>(R.id.button_clear)
        search = findViewById(R.id.edittext_search)
        btnClear.visibility = View.GONE

        search.doOnTextChanged { s, _, _, _ ->
            btnClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            stringInput = s.toString()

            searchRunnable?.let { searchHandler.removeCallbacks(it) }

            if (s.isNullOrEmpty()) {
                showHistoryIfNeeded()
            } else {
                searchRunnable = Runnable {
                    viewModel.searchTracks(s.toString())
                }
                searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
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
        progressBar = findViewById(R.id.progress_bar)

        val retryButton = findViewById<Button>(R.id.button_retry)
        retryButton.setOnClickListener {
            search.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }

        recycleView.layoutManager = LinearLayoutManager(this)
        adapter = TrackAdapter(searchData) { track ->
            if (isClickAllowed()) {
                viewModel.saveToHistory(track)
                openAudioPlayer(track)
            }
        }
        recycleView.adapter = adapter

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyAdapter = TrackAdapter(historyData) { track ->
            if (isClickAllowed()) {
                viewModel.saveToHistory(track)
                openAudioPlayer(track)
            }
        }
        historyRecycler.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        clearHistoryInlineButton.setOnClickListener {
            viewModel.clearHistory()
        }

        search.setOnFocusChangeListener { _, _ ->
            showHistoryIfNeeded()
        }

        search.setOnEditorActionListener { s, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                viewModel.searchTracks(s.text.toString())
                true
            }
            false
        }

        btnClear.setOnClickListener {
            searchRunnable?.let { searchHandler.removeCallbacks(it) }
            search.setText("")
            hideKeyboard(search)
            searchData.clear()
            adapter.notifyDataSetChanged()
            btnClear.visibility = View.GONE
            showHistoryIfNeeded()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> {
                searchData.clear()
                searchData.addAll(state.tracks)
                adapter.notifyDataSetChanged()
                showState(State.CONTENT)
            }
            is SearchState.Loading -> showState(State.LOADING)
            is SearchState.Empty -> showState(State.EMPTY)
            is SearchState.Error -> showState(State.ERROR)
            is SearchState.History -> {
                historyData.clear()
                historyData.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
                updateButtonVisibility()
                showState(State.HISTORY)
            }
            SearchState.None -> showState(State.NONE)
        }
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
            viewModel.loadHistory()
        }
    }

    private fun showState(state: State) {
        recycleView.isVisible = (state == State.CONTENT)
        emptyView.isVisible = (state == State.EMPTY)
        errorView.isVisible = (state == State.ERROR)
        historyContainer.isVisible = (state == State.HISTORY)
        progressBar.isVisible = (state == State.LOADING)
    }

    enum class State { CONTENT, EMPTY, ERROR, HISTORY, NONE, LOADING }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_INPUT, stringInput)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringInput = savedInstanceState.getString(STRING_INPUT, "")
        search.setText(stringInput)
    }

    private fun isClickAllowed(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
            return false
        }
        lastClickTime = currentTime
        return true
    }

    private fun openAudioPlayer(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java)
        // Конвертируем domain Track в TrackDto и сериализуем в JSON
        val trackDto = TrackMapper.mapDomainToDto(track)
        val trackJson = Gson().toJson(trackDto)
        intent.putExtra(AudioPlayerActivity.TRACK_KEY, trackJson)
        startActivity(intent)
    }

    override fun onDestroy() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    companion object {
        private const val STRING_INPUT = "STRING_INPUT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 500L
    }
}
