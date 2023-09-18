package com.practicum.playlistmaker.playlist.settings.domain.api

interface SettingsInteractor {

    fun changeTheme(changed: Boolean)

    fun getTheme(): Boolean
}