package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Creator

class SearchTrackViewModel(application: Application): AndroidViewModel(application) {

    val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    var tracks = tracksInteractor.getTracks()
    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null
    private var isClickAllowed = true

    private val _searchHistory = MutableLiveData(tracks)
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

             tracksInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
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
        tracksInteractor.clearHistory()
    }

    fun saveTrack(track: Track) {
        tracksInteractor.saveTrack(track)
        loadSearchHistory()

    }

    fun loadSearchHistory() {
        val updatedHistory = tracksInteractor.getTracks()
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

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTrackViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}