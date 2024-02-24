package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private var timerJob: Job? = null
    private var favouriteTrackJob: Job? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> get() = _playerState

    fun preparePlayer(trackUrl: String) {
        playerInteractor.setDataSource(trackUrl)
        playerInteractor.prepareAsync()
        playerInteractor.setOnPreparedListener {
            _playerState.postValue(PlayerState.Prepared())
        }
        playerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            _playerState.postValue(PlayerState.Prepared())
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    fun playbackControl() {
        when(playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        _playerState.value = PlayerState.Default()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if(track.isFavorite) {
                track.trackId.let { favoriteTrackInteractor.deleteFavoriteTrack(track) }
            }
            else {
                track.trackId.let { favoriteTrackInteractor.insertFavoriteTrack(track) }
            }
        }
    }

    fun observeFavourite(track: Track): LiveData<Boolean> {
        favouriteTrackJob = viewModelScope.launch {
            while (true) {
                track.trackId.let { trackId ->
                    favoriteTrackInteractor.getFavoriteTrackId(trackId).collect { item ->
                        _isFavorite.postValue(item)
                    }
                }
            }
        }
        return _isFavorite
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY_MILLIS)
                _playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(playerInteractor.getCurrentPosition())
    }

    companion object {
        const val DELAY_MILLIS = 300L
    }
}