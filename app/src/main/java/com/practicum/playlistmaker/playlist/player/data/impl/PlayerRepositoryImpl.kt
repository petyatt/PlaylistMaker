package com.practicum.playlistmaker.playlist.player.data.impl

import com.practicum.playlistmaker.playlist.player.data.dto.PlayerDataSource
import com.practicum.playlistmaker.playlist.player.domain.api.PlayerRepository

class PlayerRepositoryImpl(private val playerDataSource: PlayerDataSource) : PlayerRepository {

    override fun startPlayer() {
        playerDataSource.startPlayer()
    }

    override fun pausePlayer() {
        playerDataSource.pausePlayer()
    }

    override fun prepareAsync() {
        playerDataSource.prepareAsync()
    }

    override fun releasePlayer() {
        playerDataSource.releasePlayer()
    }

    override fun isPlaing(): Boolean {
        return playerDataSource.isPlaing()
    }

    override fun getCurrentPosition(): Int {
        return playerDataSource.getCurrentPosition()
    }

    override fun seekPlayer(position: Int) {
        playerDataSource.seekPlayer(position)
    }

    override fun setDataSource(url: String) {
        playerDataSource.setDataSource(url)
    }

    override fun setOnPreparedListener(onPreparedListener: () -> Unit) {
        playerDataSource.setOnPreparedListener(onPreparedListener)
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        playerDataSource.setOnCompletionListener(onCompletionListener)
    }

}