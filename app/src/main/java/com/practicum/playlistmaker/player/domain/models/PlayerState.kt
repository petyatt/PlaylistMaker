package com.practicum.playlistmaker.player.domain.models

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonText: String, val progress: String) {

    class Default : PlayerState(false, PLAY, INITIAL_POSITION)

    class Prepared : PlayerState(true, PLAY, INITIAL_POSITION)

    class Playing(progress: String) : PlayerState(true, PAUSE, progress)

    class Paused(progress: String) : PlayerState(true, PLAY, progress)

    companion object {
        private const val PLAY = "PLAY"
        private const val PAUSE = "PAUSE"
        private const val INITIAL_POSITION = "00:00"
    }
}