package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.ui.models.TrackUI
import com.google.android.material.internal.ViewUtils.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()


    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!
    private var stringInput: String = ""
    private lateinit var historyAdapter: TrackAdapter
    private val historyData = mutableListOf<TrackUI>()
    private val data = mutableListOf<TrackUI>()

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastClickTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            stringInput = it.getString(STRING_INPUT, "")
            binding.edittextSearch.setText(stringInput)
        }

        initViews()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun initViews() {

        binding.buttonClear.visibility = View.GONE

        binding.buttonRetry.setOnClickListener {
            binding.edittextSearch.onEditorAction(EditorInfo.IME_ACTION_DONE)
        }
    }

    private fun setupRecyclerViews() {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TrackAdapter(data) { track ->
            if (isClickAllowed()) {
                viewModel.saveToHistory(track)
                openAudioPlayer(track)
            }
        }
        binding.recycler.adapter = adapter

        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TrackAdapter(historyData) { track ->
            if (isClickAllowed()) {
                viewModel.saveToHistory(track)
                openAudioPlayer(track)
            }
        }
        binding.historyRecycler.adapter = historyAdapter
    }

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    private fun setupListeners() {
        binding.edittextSearch.doOnTextChanged { s, _, _, _ ->
            binding.buttonClear.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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

        binding.edittextSearch.setOnFocusChangeListener { _, _ ->
            showHistoryIfNeeded()
        }

        binding.edittextSearch.setOnEditorActionListener { s, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                viewModel.searchTracks(s.text.toString())
                true
            }
            false
        }

        binding.buttonClear.setOnClickListener {
            searchRunnable?.let { searchHandler.removeCallbacks(it) }
            binding.edittextSearch.setText("")
            hideKeyboard(binding.edittextSearch)
            data.clear()
            (binding.recycler.adapter as TrackAdapter).notifyDataSetChanged()
            binding.buttonClear.visibility = View.GONE
            showHistoryIfNeeded()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.buttonClearHistoryInline.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                showState(State.LOADING)
            }
            is SearchState.Content -> {
                data.clear()
                data.addAll(state.tracks)
                (binding.recycler.adapter as TrackAdapter).notifyDataSetChanged()
                showState(State.CONTENT)
            }
            is SearchState.Empty -> {
                data.clear()
                (binding.recycler.adapter as TrackAdapter).notifyDataSetChanged()
                showState(State.EMPTY)
            }
            is SearchState.Error -> {
                data.clear()
                (binding.recycler.adapter as TrackAdapter).notifyDataSetChanged()
                showState(State.ERROR)
            }
            is SearchState.History -> {
                historyData.clear()
                historyData.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
                updateButtonVisibility()
                showState(State.HISTORY)
            }
            is SearchState.None -> {
                showState(State.NONE)
            }
        }
    }

    private fun showState(state: State) {
        binding.recycler.isVisible = (state == State.CONTENT)
        binding.emptyPlaceholder.isVisible = (state == State.EMPTY)
        binding.errorPlaceholder.isVisible = (state == State.ERROR)
        binding.historyContainer.isVisible = (state == State.HISTORY)
        binding.progressBar.isVisible = (state == State.LOADING)
    }

    private fun updateButtonVisibility() {
        binding.historyContainer.post {
            val containerHeight = binding.historyContainer.height
            val titleHeight = binding.historyTitle.height
            val buttonHeight = binding.buttonClearHistoryInline.height +
                binding.buttonClearHistoryInline.marginTop + binding.buttonClearHistoryInline.marginBottom
            val recyclerHeight = binding.historyRecycler.height

            val totalContentHeight = titleHeight + recyclerHeight + buttonHeight

            if (totalContentHeight <= containerHeight) {
                binding.buttonClearHistoryInline.isVisible = true
                binding.buttonClearHistory.isVisible = false
            } else {
                binding.buttonClearHistoryInline.isVisible = false
                binding.buttonClearHistory.isVisible = true
            }
        }
    }

    private fun showHistoryIfNeeded() {
        if (binding.edittextSearch.text.isEmpty() && binding.edittextSearch.hasFocus()) {
            viewModel.loadHistory()
        }
    }

    private fun isClickAllowed(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < CLICK_DEBOUNCE_DELAY) {
            return false
        }
        lastClickTime = currentTime
        return true
    }

    private fun openAudioPlayer(track: TrackUI) {
        hideKeyboard(binding.edittextSearch)
        val action = SearchActivityDirections.actionSearchToPlayer(track)
        findNavController().navigate(action)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STRING_INPUT, stringInput)
    }

    override fun onDestroyView() {
        _binding = null
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        super.onDestroyView()
    }

    enum class State { CONTENT, EMPTY, ERROR, HISTORY, NONE, LOADING }

    companion object {
        private const val STRING_INPUT = "STRING_INPUT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 500L
        fun newInstance() = SearchActivity()
    }
}
