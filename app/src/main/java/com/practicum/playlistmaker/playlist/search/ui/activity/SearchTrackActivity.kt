package com.practicum.playlistmaker.playlist.search.ui.activity

import Track
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.playlist.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.playlist.search.domain.models.SearchState
import com.practicum.playlistmaker.playlist.search.ui.view_model.SearchTrackViewModel

class SearchTrackActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchTrackViewModel
    private lateinit var binding: ActivitySearchBinding

    private val trackListAdapter = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (viewModel.clickDebounce()) {
                    saveTrackAndStartActivity(track)
                }
            }
        }
    )

    private val trackListHistoryAdapter = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (viewModel.clickDebounce()) {
                    saveTrackAndStartActivity(track)
                }
            }
        }
    )

    private fun saveTrackAndStartActivity(track: Track) {
        viewModel.saveTrack(track)
        val intent = Intent(this@SearchTrackActivity, AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SearchTrackViewModel.getViewModelFactory())[SearchTrackViewModel::class.java]

        viewModel.observeState().observe(this) {state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmpty()
            }
        }

        viewModel.observerSearchHistory().observe(this) {
            showHistoryTracks(it)
        }

        binding.recyclerViewTrack.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTrack.adapter = trackListAdapter

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = trackListHistoryAdapter

        fun clearButtonVisibility(s: CharSequence?): Int {
            binding.tvHistoryView.visibility = GONE
            binding.historyClearButton.visibility = GONE
            binding.recyclerViewHistory.visibility = GONE
            binding.recyclerViewTrack.visibility = GONE
            binding.progressBar.visibility = GONE
            binding.placeholderView.visibility = GONE
            binding.placeholderText.visibility = GONE
            binding.refrechButton.visibility = GONE

            return if (s.isNullOrEmpty()) GONE else VISIBLE
        }

        binding.historyClearButton.setOnClickListener {
            viewModel.clearHistory()
            binding.tvHistoryView.visibility = GONE
            binding.historyClearButton.visibility = GONE
            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.notifyDataSetChanged()
        }

        binding.refrechButton.setOnClickListener {
            binding.placeholderView.visibility = GONE
            binding.placeholderText.visibility = GONE
            binding.refrechButton.visibility = GONE
            viewModel.searchDebounce(binding.inputEditText.text.toString())
        }

        binding.inputEditText.setOnFocusChangeListener { _ , hasFocus ->
            binding.recyclerViewHistory.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) GONE else VISIBLE
            binding.historyClearButton.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) GONE else VISIBLE
            binding.tvHistoryView.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) GONE else VISIBLE
        }

        binding.inputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(binding.inputEditText.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                viewModel.searchDebounce(
                    changedText = s?.toString() ?:""
                )

                binding.recyclerViewHistory.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) GONE else VISIBLE
                binding.tvHistoryView.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) GONE else VISIBLE
                binding.historyClearButton.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) GONE else VISIBLE

                trackListHistoryAdapter.notifyDataSetChanged()

                binding.clearIcon.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val hideKeyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }
    }

    private fun showPlaceholderView(placeholderImageRes: Int) {
        binding.placeholderView.setImageResource(placeholderImageRes)
        binding.placeholderView.visibility = VISIBLE
        binding.placeholderText.visibility = VISIBLE
    }

    private fun showError() {
        binding.placeholderText.setText(R.string.error_not_internet)
        showPlaceholderView(R.drawable.no_internet)
        binding.progressBar.visibility = GONE
        binding.tvHistoryView.visibility = GONE
        binding.historyClearButton.visibility = GONE
        binding.refrechButton.visibility = VISIBLE
    }

    private fun showEmpty() {
        binding.placeholderText.setText(R.string.error_not_found)
        showPlaceholderView(R.drawable.no_search)
        binding.progressBar.visibility = GONE
        binding.tvHistoryView.visibility = GONE
        binding.historyClearButton.visibility = GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerViewTrack.visibility = VISIBLE
        binding.recyclerViewHistory.visibility = GONE
        binding.tvHistoryView.visibility = GONE
        binding.historyClearButton.visibility = GONE
        binding.progressBar.visibility = GONE

        trackListAdapter.tracks.clear()
        trackListAdapter.tracks.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showHistoryTracks(historyTracks: List<Track>) {
        if (historyTracks.isEmpty()) {
            binding.recyclerViewHistory.visibility = GONE
            binding.tvHistoryView.visibility = GONE
            binding.historyClearButton.visibility = GONE
        } else {
            binding.recyclerViewHistory.visibility = VISIBLE
            binding.tvHistoryView.visibility = VISIBLE
            binding.historyClearButton.visibility = VISIBLE

            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.tracks.addAll(historyTracks)
            trackListHistoryAdapter.notifyDataSetChanged()
        }
        binding.progressBar.visibility = GONE
        binding.recyclerViewTrack.visibility = GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = VISIBLE
        binding.tvHistoryView.visibility = GONE
        binding.historyClearButton.visibility = GONE
    }
}