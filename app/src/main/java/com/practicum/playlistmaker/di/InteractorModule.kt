package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.domain.db.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.impl.FavoriteTrackInteractorImpl
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }
}