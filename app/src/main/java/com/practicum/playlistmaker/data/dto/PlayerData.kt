package com.practicum.playlistmaker.data.dto

import android.media.MediaPlayer

class PlayerData : PlayerDataSource {

    private val mediaPlayer = MediaPlayer()

    override fun seekPlayer(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun startPlayer() {
        mediaPlayer.start()

    }

    override fun pausePlayer() {
        mediaPlayer.pause()

    }

    override fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun isPlaing(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun setDataSource(url: String) {
        mediaPlayer.setDataSource(url)
    }

    override fun setOnPreparedListener(onPreparedListener: () -> Unit) {
        mediaPlayer.setOnPreparedListener {
            onPreparedListener()
        }
    }

    override fun setOnCompletionListener(onCompletionListener: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onCompletionListener()
        }
    }
}