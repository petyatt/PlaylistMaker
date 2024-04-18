package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.Playlist
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
    private val playlistInteractor: PlaylistInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
): ViewModel() {

    private var timerJob: Job? = null
    private var favouriteTrackJob: Job? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val _bottomSheetState = MutableLiveData<Int>()
    val bottomSheetState: LiveData<Int> get() = _bottomSheetState

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>> get() = _playlistsLiveData

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
            val isCurrentlyFavorite = track.isFavorite
            if (isCurrentlyFavorite) {
                favoriteTrackInteractor.deleteFavoriteTrack(track)
                track.isFavorite = false
            } else {
                favoriteTrackInteractor.insertFavoriteTrack(track)
                track.isFavorite = true
            }
            _isFavorite.postValue(track.isFavorite)
        }
    }

    fun observeFavourite(track: Track) {
        favouriteTrackJob = viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTrackId(track.trackId).collect { isFavorite ->
                _isFavorite.postValue(isFavorite)
            }
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(DELAY_MILLIS)
                _playerState.value?.let { currentState ->
                    val currentPosition = getCurrentPlayerPosition()
                    if (currentState.progress != currentPosition) {
                        _playerState.postValue(PlayerState.Playing(currentPosition))
                    }
                }
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return dateFormat.format(playerInteractor.getCurrentPosition())
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylist().collect { playlistEntities ->
                val playlists = playlistEntities.map { playlistEntity ->
                    Playlist(
                        playlistEntity.playlistId,
                        playlistEntity.title,
                        playlistEntity.description,
                        playlistEntity.imagePath,
                        playlistEntity.trackList,
                        playlistEntity.trackCount
                    )
                }
                _playlistsLiveData.value = playlists
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            if (playlist.trackList.contains(track.trackId)) {
                _message.postValue("Трек уже добавлен в плейлист ${playlist.title}")
            } else {
                val newTrackList = playlist.trackList + track.trackId
                playlist.trackList = newTrackList
                playlistInteractor.addTrackInPlaylist(track, playlist)
                _message.postValue("Добавлено в плейлист ${playlist.title}")
                _bottomSheetState.postValue(BottomSheetBehavior.STATE_HIDDEN)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        }

    companion object {
        const val DELAY_MILLIS = 300L
    }
}