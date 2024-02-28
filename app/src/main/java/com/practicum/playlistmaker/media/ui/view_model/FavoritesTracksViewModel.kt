package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.model.MediaTrackState
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val searchInteractor: SearchInteractor
): ViewModel() {

    private val _stateLiveData = MutableLiveData<MediaTrackState>()
    fun observeState(): LiveData<MediaTrackState> = _stateLiveData

    private var isClickAllowed = true

    init {
        getFavoriteTrack()
    }

    fun getFavoriteTrack() {
        viewModelScope.launch {
            favoriteTrackInteractor.getAllFavoriteTracks().collect { tracks ->
                mediaTrackState(tracks)
            }
        }
    }

    fun saveTrack(track: Track) {
        searchInteractor.saveTrack(track)
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

    private fun mediaTrackState(tracks: List<Track>) {
        if (tracks.isNullOrEmpty()) {
            _stateLiveData.postValue(MediaTrackState.Empty)
        }
        else{
            _stateLiveData.postValue(MediaTrackState.Content(tracks))
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}