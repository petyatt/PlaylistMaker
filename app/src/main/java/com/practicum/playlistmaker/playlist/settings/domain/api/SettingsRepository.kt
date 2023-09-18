package com.practicum.playlistmaker.playlist.settings.domain.api

interface SettingsRepository {

    fun changeTheme(changed: Boolean)

    fun getTheme(): Boolean
}