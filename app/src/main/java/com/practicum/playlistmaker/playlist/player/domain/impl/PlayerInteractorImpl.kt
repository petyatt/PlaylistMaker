package com.practicum.playlistmaker.playlist.player.domain.impl

import com.practicum.playlistmaker.playlist.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.playlist.player.domain.api.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository): PlayerInteractor {

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun prepareAsync() {
        playerRepository.prepareAsync()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun isPlaing(): Boolean {
        return playerRepository.isPlaing()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun seekPlayer(position: Int) {
        playerRepository.seekPlayer(position)
    }

    override fun setDataSource(url: String) {
        playerRepository.setDataSource(url)
    }

    override fun setOnPreparedListener(onPreparedListener: () -> Unit) {
        playerRepository.setOnPreparedListener(onPreparedListener)
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        playerRepository.setOnCompletionListener(onCompletionListener)
    }


}