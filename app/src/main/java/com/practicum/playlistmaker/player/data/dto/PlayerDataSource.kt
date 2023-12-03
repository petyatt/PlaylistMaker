package com.practicum.playlistmaker.player.data.dto

interface PlayerDataSource {

    fun startPlayer()

    fun pausePlayer()

    fun prepareAsync()

    fun releasePlayer()

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int

    fun seekPlayer(position: Int)

    fun setDataSource(url: String)

    fun setOnPreparedListener(onPreparedListener : () -> Unit)

    fun setOnCompletionListener(onCompletionListener : () -> Unit)

}