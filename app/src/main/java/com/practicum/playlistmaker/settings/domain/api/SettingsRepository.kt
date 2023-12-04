package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {

    fun changeTheme(changed: Boolean)

    fun getTheme(): Boolean
}