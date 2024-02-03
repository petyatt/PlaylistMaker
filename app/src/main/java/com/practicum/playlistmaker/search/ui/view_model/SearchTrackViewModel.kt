package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchTrackViewModel(private val searchInteractor: SearchInteractor): ViewModel() {

    private var latestSearchText: String? = null
    private var isClickAllowed = true
    private var searchJob: Job? = null

    private val _searchHistory = MutableLiveData(searchInteractor.getTracks())
    fun observerSearchHistory(): LiveData<ArrayList<Track>> = _searchHistory

    private val _stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _stateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

     private fun searchRequest(newSearchText: String) {
         if (newSearchText.isNotEmpty()) {

             render(SearchState.Loading)

             viewModelScope.launch {
                 searchInteractor
                     .searchTracks(newSearchText)
                     .collect {pair ->
                         processResult(pair.first, pair.second)
                     }
             }
         }
     }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
    }

    fun saveTrack(track: Track) {
        searchInteractor.saveTrack(track)

    }

    fun loadSearchHistory() {
        val updatedHistory = searchInteractor.getTracks()
        _searchHistory.postValue(updatedHistory)
    }

    fun refreshSearchResults() {
        latestSearchText?.let { searchText ->
            searchRequest(searchText)
        }
    }

    private fun processResult (foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.clear()
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                render(
                    SearchState.Error
                )
            }
            tracks.isEmpty() -> {
                render(
                    SearchState.Empty
                )
            }
            else -> {
                render(
                    SearchState.Content(tracks)
                )
            }
        }
    }

    private fun render(state: SearchState) {
        _stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}