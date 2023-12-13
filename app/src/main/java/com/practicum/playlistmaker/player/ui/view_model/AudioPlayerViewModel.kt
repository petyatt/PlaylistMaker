package com.practicum.playlistmaker.player.ui.view_model

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(private val playerInteractor: PlayerInteractor): ViewModel() {

    private val _playerState = MutableLiveData(PlayerState.STATE_DEFAULT)
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _progressTimer = MutableLiveData(TIMER_START)
    val progressTimer: LiveData<String> get() = _progressTimer

    private var mainThreadHandler: Handler? = null

    fun preparePlayer(trackUrl: String) {
        playerInteractor.setDataSource(trackUrl)
        playerInteractor.prepareAsync()
        playerInteractor.setOnPreparedListener {
            postPlayState(PlayerState.STATE_PREPARED)
        }
        playerInteractor.setOnCompletionListener {
            postPlayState(PlayerState.STATE_PREPARED)
            playerInteractor.seekPlayer(INITIAL_POSITION)
            postProgress(TIMER_START)
            mainThreadHandler?.removeCallbacks(progressRunnable)
        }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        postPlayState(PlayerState.STATE_PLAYING)
        progressRunnable.run()
        mainThreadHandler?.postDelayed(progressRunnable, DELAY_MILLIS)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        postPlayState(PlayerState.STATE_PAUSED)
    }

    fun playbackControl() {
        when(playerState.value) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {
                PlayerState.STATE_DEFAULT
            }
        }
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
    }

    private fun postPlayState(state: PlayerState) {
        _playerState.postValue(state)
    }

    private fun postProgress(progress: String) {
        _progressTimer.postValue(progress)
    }

    val progressRunnable = object : Runnable {
        override fun run() {
            postProgress(SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerInteractor.getCurrentPosition()))
            mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
        }
    }

    companion object {
        private const val INITIAL_POSITION = 0
        private const val TIMER_START = "00:00"
        const val DELAY_MILLIS = 300L
    }
}