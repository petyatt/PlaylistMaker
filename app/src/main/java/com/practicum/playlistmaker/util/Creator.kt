package com.practicum.playlistmaker.util

import android.content.Context
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.storage.model.TrackStorage
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.player.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.data.dto.PlayerData
import com.practicum.playlistmaker.player.data.dto.PlayerDataSource
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.storage.sharedPrefsStorage.SharedPrefsStorage

object Creator {

    private fun getTracksRepository(context: Context): SearchRepository {
        val networkClient: NetworkClient = RetrofitNetworkClient(context)
        val trackStorage: TrackStorage = SharedPrefsStorage(context)
        return SearchRepositoryImpl(networkClient, trackStorage)
    }

    fun provideTracksInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTracksRepository(context))
    }

    private fun getPlayerRepository(): PlayerRepository {
        val playerDataSource: PlayerDataSource = PlayerData()
        return PlayerRepositoryImpl(playerDataSource)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}