package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.impl.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.storage.model.TrackStorage
import com.practicum.playlistmaker.data.storage.sharedprefs.SharedPrefsStorage
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.data.impl.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.dto.PlayerData
import com.practicum.playlistmaker.data.dto.PlayerDataSource
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl

object Creator {

    private fun getTracksRepository(context: Context): TracksRepository {
        val networkClient: NetworkClient = RetrofitNetworkClient()
        val trackStorage: TrackStorage = SharedPrefsStorage(context)
        return TracksRepositoryImpl(networkClient, trackStorage)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getPlayerRepository(): PlayerRepository {
        val playerDataSource: PlayerDataSource = PlayerData()
        return PlayerRepositoryImpl(playerDataSource)
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}