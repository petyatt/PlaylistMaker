package com.practicum.playlistmaker.search.ui.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchTrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchTrackViewModel>()

    private val trackListAdapter = TrackAdapter(object: TrackAdapter.OnClickListener {
        override fun onTrackClick(track: Track) {
            if (viewModel.clickDebounce()) {
                saveTrackAndStartFragment(track)
            }
        }

        override fun onTrackLongClick(track: Track) {}
    })

    private val trackListHistoryAdapter = TrackAdapter(object: TrackAdapter.OnClickListener {
        override fun onTrackClick(track: Track) {
            if (viewModel.clickDebounce()) {
                saveTrackAndStartFragment(track)
            }
        }

        override fun onTrackLongClick(track: Track) {}
    })

    private fun saveTrackAndStartFragment(track: Track) {
        viewModel.saveTrack(track)
        val args = Bundle()
        args.putSerializable("track", track)
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, args)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
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
            binding.tvHistoryView.isVisible = false
            binding.historyClearButton.isVisible = false
            binding.recyclerViewHistory.isVisible = false
            binding.recyclerViewTrack.isVisible = false
            binding.progressBar.isVisible = false
            binding.placeholderView.isVisible = false
            binding.placeholderText.isVisible = false
            binding.refrechButton.isVisible = false

            return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        binding.historyClearButton.setOnClickListener {
            viewModel.clearHistory()
            binding.tvHistoryView.isVisible = false
            binding.historyClearButton.isVisible = false
            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.notifyDataSetChanged()
        }

        binding.refrechButton.setOnClickListener {
            binding.placeholderView.isVisible = false
            binding.placeholderText.isVisible = false
            binding.refrechButton.isVisible = false

        }

        binding.inputEditText.setOnFocusChangeListener { _ , hasFocus ->
            binding.recyclerViewHistory.isVisible = !(hasFocus && binding.inputEditText.text.isEmpty())
            binding.historyClearButton.isVisible = !(hasFocus && binding.inputEditText.text.isEmpty())
            binding.tvHistoryView.isVisible = !(hasFocus && binding.inputEditText.text.isEmpty())
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

                binding.recyclerViewHistory.isVisible = !(binding.inputEditText.hasFocus() && s?.isEmpty() == true)
                binding.tvHistoryView.isVisible = !(binding.inputEditText.hasFocus() && s?.isEmpty() == true)
                binding.historyClearButton.isVisible = !(binding.inputEditText.hasFocus() && s?.isEmpty() == true)

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
        viewModel.loadSearchHistory()
    }

    private fun showPlaceholderView(placeholderImageRes: Int) {
        binding.placeholderView.setImageResource(placeholderImageRes)
        binding.placeholderView.isVisible = true
        binding.placeholderText.isVisible = true
    }

    private fun showError() {
        binding.placeholderText.setText(R.string.error_not_internet)
        showPlaceholderView(R.drawable.no_internet)
        binding.recyclerViewTrack.isVisible = false
        binding.recyclerViewHistory.isVisible = false
        binding.progressBar.isVisible = false
        binding.tvHistoryView.isVisible = false
        binding.historyClearButton.isVisible = false
        binding.refrechButton.isVisible = true
    }

    private fun showEmpty() {
        binding.placeholderText.setText(R.string.error_not_found)
        showPlaceholderView(R.drawable.no_search)
        binding.progressBar.isVisible = false
        binding.tvHistoryView.isVisible = false
        binding.historyClearButton.isVisible = false
    }

    private fun showContent(tracks: List<Track>) {
        binding.recyclerViewTrack.isVisible = true
        binding.recyclerViewHistory.isVisible = false
        binding.tvHistoryView.isVisible = false
        binding.historyClearButton.isVisible = false
        binding.progressBar.isVisible = false

        trackListAdapter.tracks.clear()
        trackListAdapter.tracks.addAll(tracks)
        trackListAdapter.notifyDataSetChanged()
    }

    private fun showHistoryTracks(historyTracks: List<Track>) {
        if (historyTracks.isEmpty()) {
            binding.recyclerViewHistory.isVisible = false
            binding.tvHistoryView.isVisible = false
            binding.historyClearButton.isVisible = false
        } else {
            binding.recyclerViewHistory.isVisible = true
            binding.tvHistoryView.isVisible = true
            binding.historyClearButton.isVisible = true

            trackListHistoryAdapter.tracks.clear()
            trackListHistoryAdapter.tracks.addAll(historyTracks)
            trackListHistoryAdapter.notifyDataSetChanged()
        }
        binding.progressBar.isVisible = false
        binding.recyclerViewTrack.isVisible = false
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.tvHistoryView.isVisible = false
        binding.historyClearButton.isVisible = false
        binding.recyclerViewHistory.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}