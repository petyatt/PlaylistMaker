package com.practicum.playlistmaker.media.ui.view_model

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class EditNewPlaylistViewModel(
    appContext: Context,
    playlistInteractor: PlaylistInteractor
) : NewPlaylistViewModel(appContext, playlistInteractor) {

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }
}