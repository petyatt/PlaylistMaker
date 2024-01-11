package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchTrackViewModel(private val searchInteractor: SearchInteractor): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private var isClickAllowed = true

    private val _searchHistory = MutableLiveData(searchInteractor.getTracks())
    fun observerSearchHistory(): LiveData<ArrayList<Track>> = _searchHistory

    private val _stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = _stateLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

     private fun searchRequest(newSearchText: String) {
         if (newSearchText.isNotEmpty()) {
             render(SearchState.Loading)

             searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                 override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
             })
         }
     }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun render(state: SearchState) {
        _stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}