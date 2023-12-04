package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {

    fun changeTheme(changed: Boolean)

    fun getTheme(): Boolean
}