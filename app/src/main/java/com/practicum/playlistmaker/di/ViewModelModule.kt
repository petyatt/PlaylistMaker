package com.practicum.playlistmaker.di

import android.app.Application
import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.ui.view_model.EditNewPlaylistViewModel
import com.practicum.playlistmaker.media.ui.view_model.FavoritesTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.NewPlaylistViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistInfoViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.ui.view_model.SearchTrackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        AudioPlayerViewModel(get<PlayerInteractor>(), get<PlaylistInteractor>(), get<FavoriteTrackInteractor>())
    }

    viewModel {
        SearchTrackViewModel(get<SearchInteractor>())
    }

    viewModel {
        FavoritesTracksViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get<Application>().applicationContext, get())
    }

    viewModel {
        PlaylistInfoViewModel(get())
    }

    viewModel {
        EditNewPlaylistViewModel(get<Application>().applicationContext, get())
    }
}