package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val _totalMinutesLiveData = MutableLiveData<Int>()
    val totalMinutesLiveData: LiveData<Int> get() = _totalMinutesLiveData

    private val _tracksLiveData = MutableLiveData<List<Track>>()
    val tracksLiveData: LiveData<List<Track>> get() = _tracksLiveData

    private val _playlistLiveData = MutableLiveData<Playlist?>()
    val playlistLiveData: LiveData<Playlist?> get() = _playlistLiveData

    fun getPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)?.let { playlist ->
                _playlistLiveData.postValue(playlist)
                playlistInteractor.getTracksForPlaylist(playlistId).collect { tracks ->
                    _tracksLiveData.postValue(tracks)
                    _totalMinutesLiveData.postValue(calculateTotalMinutes(tracks))
                }
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            refreshPlaylistAndTracks(playlistId)
        }
    }

    private fun calculateTotalMinutes(tracks: List<Track>): Int {
        val totalMillis = tracks.sumOf { it.trackTimeMillis }
        return (totalMillis / 60000).toInt()
    }

    fun getTracksWord(count: Int, unitType: String): String {
        val word = when (unitType) {
            "tracks" -> when {
                count % 10 == 1 && count % 100 != 11 -> "трек"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "трека"
                else -> "треков"
            }

            "minutes" -> when {
                count % 10 == 1 && count % 100 != 11 -> "минута"
                count % 10 in 2..4 && count % 100 !in 12..14 -> "минуты"
                else -> "минут"
            }

            else -> ""
        }
        return "$count $word"
    }

    fun deleteTrackFromPlaylist(trackId: Long, playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            refreshPlaylistAndTracks(playlistId)
        }
    }

    private fun refreshPlaylistAndTracks(playlistId: Long) {
        viewModelScope.launch {
            val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId)
            _playlistLiveData.postValue(updatedPlaylist)

            updatedPlaylist?.let {
                val updatedTracks = playlistInteractor.getTracksForPlaylist(playlistId).first()
                _tracksLiveData.postValue(updatedTracks)
            }
        }
    }
}