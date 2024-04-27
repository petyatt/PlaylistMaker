package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>>
        get() = _playlistsLiveData

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylist().collect { playlistEntities ->
                val playlists = playlistEntities.map { playlistEntity ->
                    Playlist(
                        playlistEntity.playlistId,
                        playlistEntity.title,
                        playlistEntity.description,
                        playlistEntity.imagePath,
                        playlistEntity.trackCount
                    )
                }
                _playlistsLiveData.value = playlists
            }
        }
    }
}