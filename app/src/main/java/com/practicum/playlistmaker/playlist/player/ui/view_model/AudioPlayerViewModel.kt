package com.practicum.playlistmaker.playlist.player.ui.view_model

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.playlist.player.domain.models.PlayerState
import com.practicum.playlistmaker.playlist.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(application: Application): AndroidViewModel(application) {

    private val _playerState = MutableLiveData(PlayerState.STATE_DEFAULT)
    val playerState: LiveData<PlayerState> get() = _playerState

    private val _progressTimer = MutableLiveData(TIMER_START)
    val progressTimer: LiveData<String> get() = _progressTimer

    private var mainThreadHandler: Handler? = null
     val mediaPlayer = Creator.providePlayerInteractor()

    fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            postPlayState(PlayerState.STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            postPlayState(PlayerState.STATE_PREPARED)
            mediaPlayer.seekPlayer(INITIAL_POSITION)
            postProgress(TIMER_START)
            mainThreadHandler?.removeCallbacks(progressRunnable)
        }
    }

    fun startPlayer() {
        mediaPlayer.startPlayer()
        postPlayState(PlayerState.STATE_PLAYING)
        progressRunnable.run()
        mainThreadHandler?.postDelayed(progressRunnable, DELAY_MILLIS)
    }

    fun pausePlayer() {
        mediaPlayer.pausePlayer()
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
        mediaPlayer.releasePlayer()
    }

    private fun postPlayState(state: PlayerState) {
        _playerState.postValue(state)
    }

    private fun postProgress(progress: String) {
        _progressTimer.postValue(progress)
    }

    val progressRunnable = object : Runnable {
        override fun run() {
            postProgress(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.getCurrentPosition()))
            mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
        }
    }

    companion object {

        private const val INITIAL_POSITION = 0
        private const val TIMER_START = "00:00"
        const val DELAY_MILLIS = 300L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }

}