package com.practicum.playlistmaker.settings.domain.model

interface ThemeStorage {

    fun getTheme(): Boolean

    fun changeTheme(changed: Boolean)
}