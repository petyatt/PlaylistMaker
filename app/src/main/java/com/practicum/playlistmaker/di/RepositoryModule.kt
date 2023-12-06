package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.localstorage.LocalTrackStorage
import com.practicum.playlistmaker.search.data.localstorage.LocalTrackStorage.Companion.TRACK_SHARED_PREFERENCES
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.TrackStorage
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.data.localstorage.LocalThemeStorage
import com.practicum.playlistmaker.settings.data.localstorage.LocalThemeStorage.Companion.THEME_SHARED_PREFERENCES
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<TrackStorage> {
        LocalTrackStorage(get(named(TRACK_SHARED_PREFERENCES)), get())
    }

    single<ThemeStorage> {
        LocalThemeStorage(get(named(THEME_SHARED_PREFERENCES)))
    }
}