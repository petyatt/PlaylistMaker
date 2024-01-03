package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.view_model.FavoritesTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.ui.view_model.AudioPlayerViewModel
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.ui.view_model.SearchTrackViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        AudioPlayerViewModel(get<PlayerInteractor>())
    }

    viewModel {
        SearchTrackViewModel(get<SearchInteractor>())
    }

    viewModel {
        FavoritesTracksViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}