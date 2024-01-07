package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.AudioPlayerActivity
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchTrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModel<SearchTrackViewModel>()

    private val trackListAdapter = TrackAdapter { track ->
        if (viewModel.clickDebounce()) {
            saveTrackAndStartActivity(track)
        }
    }

    private val trackListHistoryAdapter = TrackAdapter { track ->
        if (viewModel.clickDebounce()) {
            saveTrackAndStartActivity(track)
        }
    }

    private fun saveTrackAndStartActivity(track: Track) {
        viewModel.saveTrack(track)
        val intent = Intent(requireContext(), AudioPlayerActivity::class.java)
        intent.putExtra("track", track)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.observeState().observe(viewLifecycleOwner) {state ->
            when (state) {
                is SearchState.Loading -> showLoading()
                is SearchState.Content -> showContent(state.tracks)
                is SearchState.Error -> showError()
                is SearchState.Empty -> showEmpty()
            }
        }

        viewModel.observerSearchHistory().observe(viewLifecycleOwner) {
            showHistoryTracks(it)
        }

        binding.recyclerViewTrack.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTrack.adapter = trackListAdapter

        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = trackListHistoryAdapter

        fun clearButtonVisibility(s: CharSequence?): Int {
            viewModel.loadSearchHistory()
            binding.tvHistoryView.visibility = View.GONE
            binding.historyClearButton.visibility = View.GONE
            binding.recyclerViewHistory.visibility = View.GONE
            binding.recyclerViewTrack.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.placeholderView.visibility = View.GONE
            binding.placeholderText.visibility = View.GONE
            binding.refrechButton.visibility = View.GONE

            return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        binding.historyClearButton.setOnClickListener {
            viewModel.clearHistory()
            binding.tvHistoryView.visibility = View.GONE
            binding.historyClearButton.visibility = View.GONE
            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.notifyDataSetChanged()
        }

        binding.refrechButton.setOnClickListener {
            binding.placeholderView.visibility = View.GONE
            binding.placeholderText.visibility = View.GONE
            binding.refrechButton.visibility = View.GONE

        }

        binding.inputEditText.setOnFocusChangeListener { _ , hasFocus ->
            binding.recyclerViewHistory.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE
            binding.historyClearButton.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE
            binding.tvHistoryView.visibility = if (hasFocus && binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.inputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(binding.inputEditText.text.toString())
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                viewModel.searchDebounce(
                    changedText = s?.toString() ?:""
                )

                binding.recyclerViewHistory.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE
                binding.tvHistoryView.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE
                binding.historyClearButton.visibility = if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) View.GONE else View.VISIBLE

                trackListHistoryAdapter.notifyDataSetChanged()

                binding.clearIcon.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher.let { binding.inputEditText.addTextChangedListener(it) }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            val hideKeyboard = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            hideKeyboard.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshSearchResults()
    }

    private fun showPlaceholderView(placeholderImageRes: Int) {
        binding.placeholderView.setImageResource(placeholderImageRes)
        binding.placeholderView.visibility = View.VISIBLE
        binding.placeholderText.visibility = View.VISIBLE
    }

    private fun showError() {
        binding.placeholderText.setText(R.string.error_not_internet)
        showPlaceholderView(R.drawable.no_internet)
        binding.progressBar.visibility = View.GONE
        binding.tvHistoryView.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.refrechButton.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        binding.placeholderText.setText(R.string.error_not_found)
        showPlaceholderView(R.drawable.no_search)
        binding.progressBar.visibility = View.GONE
        binding.tvHistoryView.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerViewTrack.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
        binding.tvHistoryView.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        trackListAdapter.tracks.clear()
        trackListAdapter.tracks.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showHistoryTracks(historyTracks: List<Track>) {
        if (historyTracks.isEmpty()) {
            binding.recyclerViewHistory.visibility = View.GONE
            binding.tvHistoryView.visibility = View.GONE
            binding.historyClearButton.visibility = View.GONE
        } else {
            binding.recyclerViewHistory.visibility = View.VISIBLE
            binding.tvHistoryView.visibility = View.VISIBLE
            binding.historyClearButton.visibility = View.VISIBLE

            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.tracks.addAll(historyTracks)
            trackListHistoryAdapter.notifyDataSetChanged()
        }
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewTrack.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvHistoryView.visibility = View.GONE
        binding.historyClearButton.visibility = View.GONE
        binding.recyclerViewHistory.visibility = View.GONE
    }
}