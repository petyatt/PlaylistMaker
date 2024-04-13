package com.practicum.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistDatabase
import com.practicum.playlistmaker.data.db.TracksPlaylistsDatabase
import com.practicum.playlistmaker.player.data.dto.PlayerData
import com.practicum.playlistmaker.player.data.dto.PlayerDataSource
import com.practicum.playlistmaker.search.data.localstorage.LocalTrackStorage
import com.practicum.playlistmaker.search.data.localstorage.LocalTrackStorage.Companion.TRACK_SHARED_PREFERENCES
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.localstorage.LocalThemeStorage
import com.practicum.playlistmaker.settings.data.localstorage.LocalThemeStorage.Companion.THEME_SHARED_PREFERENCES
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "playlist.db")
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), TracksPlaylistsDatabase::class.java, "tracksPlaylists.db")
            .build()
    }

    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single(named(TRACK_SHARED_PREFERENCES)) {
        androidContext()
            .getSharedPreferences(LocalTrackStorage.SAVE_HISTORY, MODE_PRIVATE)
    }

    single(named(THEME_SHARED_PREFERENCES)) {
        androidContext()
            .getSharedPreferences(LocalThemeStorage.DARK_THEME, MODE_PRIVATE)
    }

    single<LocalTrackStorage> {
        LocalTrackStorage(get(named(TRACK_SHARED_PREFERENCES)), get())
    }

    single<LocalThemeStorage> {
        LocalThemeStorage(get(named(THEME_SHARED_PREFERENCES)))
    }

    factory<PlayerDataSource> {
        PlayerData(get())
    }

    factory { Gson() }

    factory {
        MediaPlayer()
    }
}